package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
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
public class SalesforceDataActionTest {
    @Mock
    PartnerConnection partnerConnection;
    @InjectMocks
    SalesforceDataAction action;

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
    public void insert_returnsSfIdOfCreatedObject() throws ConnectionException {
        final String sfId = "0x123...";
        mockPartnerConnectionInsertIdResult(sfId);

        final String storedRecordSfId = action.insert(new SObject());

        assertThat(storedRecordSfId, is(sfId));
        verify(partnerConnection).create(any());
    }

    @Test
    public void insert_failsIfStoreOperationFailed() {
        failPartnerConnectionCreate();

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.insert(new SObject());
        });
        assertThat(tre.getCause(), instanceOf(ConnectionException.class));
    }

    @Test(expected = TestRuntimeException.class)
    public void insert_failsIfStoreOperationDoesNotHaveSuccessStatus() throws ConnectionException {
        mockPartnerConnectionInsertSuccess(false, null);

        action.insert(new SObject());
    }

    @Test
    public void query_callsPartnerConnectionQueryMethod() throws ConnectionException {
        final String soql = "any soql query";
        mockPartnerConnectionQueryResult(new SObject[0]);

        action.query(soql);

        verify(partnerConnection).query(soql);
    }

    @Test
    public void query_returnsSObjectsProvidedByPartnerConnection() {
        final SObject[] expectedObjects = new SObject[] {
                new SObject(), new SObject()
        };
        mockPartnerConnectionQueryResult(expectedObjects);

        final List<SObject> actualObjects = action.query("any soql");

        assertThat(actualObjects, hasItems(expectedObjects));
    }

    @Test
    public void query_failsIfQueryOperationFailed() {
        failPartnerConnectionQuery();

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            action.query("any soql");
        });
        assertThat(tre.getCause(), instanceOf(ConnectionException.class));
    }

    private void failPartnerConnectionCreate() {
        try {
            when(partnerConnection.create(any())).thenThrow(new ConnectionException());
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }

    private void failPartnerConnectionQuery() {
        try {
            when(partnerConnection.query(anyString())).thenThrow(new ConnectionException());
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }

    private void mockPartnerConnectionQueryResult(final SObject[] expectedObjects) {
        final QueryResult qr = mock(QueryResult.class);
        when(qr.getRecords()).thenReturn(expectedObjects);
        try {
            when(partnerConnection.query(anyString())).thenReturn(qr);
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }

    private void mockPartnerConnectionInsertIdResult(final String sfId) {
        mockPartnerConnectionInsertSuccess(true, sfId);
    }

    private void mockPartnerConnectionInsertSuccess(final Boolean success,
            final String sfId) {
        final SaveResult saveResultWithId = new SaveResult();
        saveResultWithId.setId(sfId);
        saveResultWithId.setSuccess(success);
        try {
            when(partnerConnection.create(any())).thenReturn(new SaveResult[] {
                    saveResultWithId
            });
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
