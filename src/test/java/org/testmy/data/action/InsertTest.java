package org.testmy.data.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sforce.soap.partner.AllOrNoneHeader_element;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class InsertTest {
    @Mock
    PartnerConnection partnerConnection;
    @Mock
    Query queryAction;
    @Mock
    Logger logger;
    @InjectMocks
    Insert action;

    private List<SObject> nonEmptyListToInsert = Collections.singletonList(new SObject());

    @Before
    public void before() throws ConnectionException {
        emulateResultsCreation(0);
        mockAllOrNone(null);
    }

    @Test
    public void query_delegatesQueryToQueryAction() {
        final String soql = "SELECT Id FROM Account";

        action.query(soql);

        verify(queryAction).query(soql);
    }

    @Test
    public void query_propagatesQueryActionResults() {
        final List<SObject> queriedObjects = Collections.singletonList(new SObject());
        final String soql = "SELECT Id FROM Account";
        when(queryAction.query(soql)).thenReturn(queriedObjects);

        final List<SObject> result = action.query(soql);

        assertThat(result, is(queriedObjects));
    }

    @Test
    public void insertObjects_createsAllPassedDataInBulk() throws ConnectionException {
        action.insertObjects(nonEmptyListToInsert);

        verify(partnerConnection).create(nonEmptyListToInsert.toArray(new SObject[0]));
    }

    @Test
    public void insertObjects_returnsIdsForCreatedObjects() {
        final Integer numberOfObjectsToInser = 55;
        emulateResultsCreation(55);

        final List<String> sfIds = action.insertObjects(nonEmptyListToInsert);

        assertThat(sfIds, hasSize(numberOfObjectsToInser));
    }

    @Test
    public void insertObjects_usesAllOrNoneApproachToCreateData() {
        action.insertObjects(nonEmptyListToInsert);

        verify(partnerConnection).setAllOrNoneHeader(true);
    }

    @Test
    public void insertObjects_clearsAllOrNoneFlagIfItWasNotSetInitially() {
        action.insertObjects(nonEmptyListToInsert);

        verify(partnerConnection).clearAllOrNoneHeader();
    }

    @Test
    public void insertObjects_restoresAllOrNoneToPreviousState() {
        mockAllOrNone(false);

        action.insertObjects(nonEmptyListToInsert);

        verify(partnerConnection).setAllOrNoneHeader(false);
    }

    @Test
    public void insertObjects_doesNotTryToSetOrRestoreAllOrNoneFlagIfItIsTrueAlready() {
        mockAllOrNone(true);

        action.insertObjects(nonEmptyListToInsert);

        verify(partnerConnection, never()).setAllOrNoneHeader(anyBoolean());
        verify(partnerConnection, never()).clearAllOrNoneHeader();
    }

    @Test(expected = TestRuntimeException.class)
    public void insertObjects_logsAllErrorsIfAtLeastOneUnSuccessfullCreation() {
        final List<SaveResult> saveResults = emulateResultsCreation(3, 2);

        action.insertObjects(nonEmptyListToInsert);

        verify(logger).error(
                "Could not save some objects, so rolling back whole transaction: {}",
                saveResults.stream().filter(sr -> !sr.getSuccess()).collect(Collectors.toList()));
    }

    @Test
    public void insertObjects_throwsRuntimeOnConnectionException() {
        emulateConnectionException();

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.insertObjects(nonEmptyListToInsert);
        });

        assertThat(tre.getCause(), instanceOf(ConnectionException.class));
    }

    @Test
    public void insertObjects_thorwsRuntimeIfCouldNotCreateAtLeastOneRecord() {
        emulateResultsCreation(2, 1);

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.insertObjects(nonEmptyListToInsert);
        });

        assertThat(tre.getCause(), not(instanceOf(ConnectionException.class)));
    }

    private void mockAllOrNone(Boolean initialState) {
        if (null == initialState) {
            when(partnerConnection.getAllOrNoneHeader()).thenReturn(null);
        }
        else {
            final AllOrNoneHeader_element mockedHeader = mock(AllOrNoneHeader_element.class);
            when(mockedHeader.isAllOrNone()).thenReturn(initialState);
            when(partnerConnection.getAllOrNoneHeader()).thenReturn(mockedHeader);
        }
    }

    private void emulateResultsCreation(final Integer numberOfSuccesses) {
        emulateResultsCreation(numberOfSuccesses, 0);
    }

    private List<SaveResult> emulateResultsCreation(final Integer numberOfSuccesses,
            final Integer numberOfFailures) {
        final List<SaveResult> allResults = constructResults(true, numberOfSuccesses);
        final List<SaveResult> failedResults = constructResults(false, numberOfFailures);
        allResults.addAll(failedResults);
        try {
            when(partnerConnection.create(any())).thenReturn(allResults.toArray(new SaveResult[0]));
            return allResults;
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("Unreachable");
        }
    }

    private List<SaveResult> constructResults(final boolean success,
            final Integer numberOfSaveResults) {
        return IntStream.range(0, numberOfSaveResults).mapToObj(i -> {
            final SaveResult sr = new SaveResult();
            sr.setSuccess(success);
            sr.setId(UUID.randomUUID().toString());
            return sr;
        }).collect(Collectors.toList());
    }

    private void emulateConnectionException() {
        try {
            when(partnerConnection.create(any())).thenThrow(new ConnectionException());
        } catch (ConnectionException e) {
            throw new TestRuntimeException("Unexpected Exception");
        }
    }
}
