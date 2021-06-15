package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @Mock
    QueryResult defaultQueryResult;
    @InjectMocks
    SalesforceDataAction action;

    @Before
    public void before() throws ConnectionException {
        mockPartnerConnectionInsertIdResult(UUID.randomUUID().toString());
        when(defaultQueryResult.getRecords()).thenReturn(new SObject[0]);
        when(partnerConnection.queryAll(anyString())).thenReturn(defaultQueryResult);
    }

    @Test
    public void insert_callPartnerConnectionCreateMethod() throws ConnectionException {
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

    @Test(expected = TestRuntimeException.class)
    public void insert_failsIfStoreOperationFailed() throws ConnectionException {
        when(partnerConnection.create(any())).thenThrow(ConnectionException.class);
        action.insert(new SObject());
    }

    @Test(expected = TestRuntimeException.class)
    public void insert_failsIfStoreOperationDoesNotHaveSuccessStatus() throws ConnectionException {
        mockPartnerConnectionInsertSuccess(false, null);
        action.insert(new SObject());
    }

    @Test
    public void queryRecords_usesPartnerConnectionToQueryData() throws ConnectionException {
        final String query = "any query";
        action.queryRecords(query);
        verify(partnerConnection).queryAll(query);
    }

    @Test
    public void queryRecords_emptyListByDefault() throws ConnectionException {
        final List<SObject> records = action.queryRecords("any query");
        assertThat(records.size(), is(0));
    }

    @Test
    public void queryRecords_returnsSObjects() throws ConnectionException {
        final SObject[] sObjects = {
                new SObject()
        };
        when(defaultQueryResult.getRecords()).thenReturn(sObjects);
        final List<SObject> returnedObjects = action.queryRecords("any query");
        assertThat(returnedObjects.size(), is(sObjects.length));
    }

    @Test(expected = TestRuntimeException.class)
    public void queryRecords_throwsRuntineExceptionInCaseOfConnectionException() throws ConnectionException {
        when(partnerConnection.queryAll(anyString())).thenThrow(new ConnectionException());
        action.queryRecords("any query");
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
