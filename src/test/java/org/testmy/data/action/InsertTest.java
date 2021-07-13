package org.testmy.data.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class InsertTest {
    @Mock
    PartnerConnection partnerConnection;
    @Mock
    Query queryAction;
    @InjectMocks
    Insert action;

    @Before
    public void before() throws ConnectionException {
        mockPartnerConnectionInsertIdResult(UUID.randomUUID().toString());
    }

    @Test
    public void insert_callsPartnerConnectionCreateMethod() throws ConnectionException {
        final SObject object = new SObject();

        action.insert(object);

        verify(partnerConnection).create(new SObject[] {
                object
        });
    }

    @Test
    public void insert_returnsSfIdOfCreatedObject() {
        final String sfId = "0x123...";
        mockPartnerConnectionInsertIdResult(sfId);

        final String storedRecordSfId = action.insert(new SObject());

        assertThat(storedRecordSfId, is(sfId));
    }

    @Test
    public void insert_throwsRuntimeOnConnectionException() {
        final SObject objectToCreate = new SObject();
        emulateConnectionException();

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.insert(objectToCreate);
        });

        assertThat(tre.getCause(), instanceOf(ConnectionException.class));
        assertThat(tre.getMessage(), equalTo("Failed to create object: " + objectToCreate));
    }

    @Test
    public void insert_thorwsRuntimeIfCouldNotCreateRecord() {
        final SObject objectToCreate = new SObject();
        final SaveResult saveResult = mockPartnerConnectionInsertResult(false, null);

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.insert(objectToCreate);
        });

        assertThat(tre.getCause(), not(instanceOf(ConnectionException.class)));
        assertThat(tre.getMessage(), equalTo("Failed to create object: " + saveResult));
    }

    @Test
    public void query_delegatesToQueryAction() {
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

    private void emulateConnectionException() {
        try {
            when(partnerConnection.create(any())).thenThrow(new ConnectionException());
        } catch (ConnectionException e) {
            throw new TestRuntimeException("Unexpected Exception");
        }
    }

    private void mockPartnerConnectionInsertIdResult(final String sfId) {
        mockPartnerConnectionInsertResult(true, sfId);
    }

    private SaveResult mockPartnerConnectionInsertResult(final Boolean resultSuccess,
            final String createdSfId) {
        final SaveResult saveResultWithId = new SaveResult();
        saveResultWithId.setId(createdSfId);
        saveResultWithId.setSuccess(resultSuccess);
        try {
            when(partnerConnection.create(any())).thenReturn(new SaveResult[] {
                    saveResultWithId
            });
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
        return saveResultWithId;
    }
}
