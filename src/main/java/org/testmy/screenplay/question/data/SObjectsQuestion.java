package org.testmy.screenplay.question.data;

import java.util.Arrays;
import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.factory.question.Partner;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class SObjectsQuestion implements Question<List<SObject>> {
    private String query;

    public SObjectsQuestion(final String query) {
        this.query = query;
    }

    @Override
    public List<SObject> answeredBy(final Actor actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
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
