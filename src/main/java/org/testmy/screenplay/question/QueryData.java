package org.testmy.screenplay.question;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.data.matchers.HasFields;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

@AllArgsConstructor
public class QueryData implements Question<SObject> {
    private HasFields objectShape;

    public static Question<SObject> similarTo(HasFields objectShape) {
        return Instrumented.instanceOf(QueryData.class).withProperties(objectShape);
    }

    @Override
    public SObject answeredBy(Actor actor) {
        final PartnerConnection connection = CallPartnerSoapApi.as(actor).ensureConnection();

        try {
            final QueryResult qr = connection.query(objectShape.toSoql());
            return qr.getRecords()[0];
        }
        catch (ConnectionException e) {
            throw new IllegalStateException(e); // TODO: change to a dedicated exception
        }
    }
}
