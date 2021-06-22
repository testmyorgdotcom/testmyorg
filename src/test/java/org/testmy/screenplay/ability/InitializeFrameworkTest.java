package org.testmy.screenplay.ability;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.RecordTypeIdProvider;
import org.testmy.data.SalesforceDataAction;

@RunWith(MockitoJUnitRunner.class)
public class InitializeFrameworkTest {
    @Mock
    SalesforceDataAction queryAction;
    @Mock
    AbilityProvider abilityProvider;
    @Mock
    CallPartnerSoapApi callPartnerApiAbility;
    @Mock
    PartnerConnection partnerConnection;
    @Mock
    QueryResult queryResult;
    @Mock
    RecordTypeIdProvider recordTypeIdProvider;
    @InjectMocks
    InitializeFramework initializeFramework;

    @Test
    public void loadRecordTypes_usesCallPartnerSoapApiToGetPartnerConnection() throws ConnectionException {
        when(abilityProvider.as(any(), any())).thenReturn(callPartnerApiAbility);
        when(callPartnerApiAbility.ensureConnection()).thenReturn(partnerConnection);
        when(partnerConnection.queryAll(anyString())).thenReturn(queryResult);
        when(queryResult.getRecords()).thenReturn(new SObject[0]);
        initializeFramework.loadRecordTypes();
        verify(callPartnerApiAbility).ensureConnection();
        verify(partnerConnection).queryAll(anyString());
        verify(queryResult).getRecords();
    }
}
