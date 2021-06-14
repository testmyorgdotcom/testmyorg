package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class SalesforceDataActionTest {
    @Mock
    PartnerConnection partnerConnection;
    @InjectMocks
    SalesforceDataAction action;

    @Before
    public void before() {
        mockWithId(UUID.randomUUID().toString());
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
        mockWithId(sfId);
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
        mockSuccess(false, null);
        action.insert(new SObject());
    }

    private void mockWithId(final String sfId) {
        mockSuccess(true, sfId);
    }

    private void mockSuccess(final Boolean success,
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
