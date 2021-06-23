package org.testmy.screenplay.question.data;

import java.util.Arrays;
import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class SObjectsQuestion implements Question<List<SObject>> {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();
    private String query;

    public SObjectsQuestion(final String query) {
        this.query = query;
    }

    public static SObjectsQuestion withQuery(String queryQuestion) {
        return Instrumented.instanceOf(SObjectsQuestion.class).withProperties(queryQuestion);
    }

    @Override
    public List<SObject> answeredBy(final Actor actor) {
        final CallPartnerSoapApi callPartnerSoapApi = abilityProvider.as(actor, CallPartnerSoapApi.class);
        final PartnerConnection connection = callPartnerSoapApi.ensureConnection();
        return queryDataUsing(connection);
    }

    private List<SObject> queryDataUsing(final PartnerConnection partnerConnection) {
        try {
            final QueryResult queryResult = partnerConnection.query(query);
            final SObject[] records = queryResult.getRecords();
            return Arrays.asList(records);
        } catch (final ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
