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
import org.testmy.screenplay.factory.question.SObjects;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class SObjectsQuestionTest {
    @Mock
    Actor mike;
    @Mock
    QueryResult queryResult;
    @Mock
    PartnerConnection partnerConnection;

    @Before
    public void before() throws ConnectionException {
        when(mike.asksFor(any())).thenReturn(partnerConnection);
        when(partnerConnection.query(any())).thenReturn(queryResult);
        when(queryResult.getRecords()).thenReturn(new SObject[0]);
    }

    @Test
    public void answeredBy_usesProvidedQueryToLoadData() throws ConnectionException {
        final String query = "SELECT Id FROM Account";
        final SObjectsQuestion question = SObjects.usingQuery(query);

        question.answeredBy(mike);

        verify(partnerConnection).query(query);
    }
}
