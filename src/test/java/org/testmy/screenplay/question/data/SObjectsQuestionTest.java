package org.testmy.screenplay.question.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class SObjectsQuestionTest {
    final Actor mike = Actor.named("Mike");
    @Mock
    QueryResult queryResult;
    @Mock
    PartnerConnection partnerConnection;
    @Mock
    AbilityProvider abilityProvider;
    @Mock
    CallPartnerSoapApi callPartnerSoapApi;

    @Before
    public void before() throws ConnectionException {
        when(abilityProvider.as(any(), any())).thenReturn(callPartnerSoapApi);
        when(callPartnerSoapApi.ensureConnection()).thenReturn(partnerConnection);
        when(partnerConnection.query(any())).thenReturn(queryResult);
        when(queryResult.getRecords()).thenReturn(new SObject[0]);
    }

    @Test
    public void answeredBy_usesProvidedQueryToLoadData() throws ConnectionException {
        final String query = "SELECT Id FROM Account";
        final SObjectsQuestion question = SObjectsQuestion.withQuery(query);
        question.abilityProvider = abilityProvider;
        question.answeredBy(mike);
        verify(partnerConnection).query(query);
    }
}
