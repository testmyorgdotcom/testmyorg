package org.testmy.screenplay.question;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.data.matchers.HasFields;
import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class QueryData implements Question<SObject> {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();
    private HasFields objectShape;

    public QueryData(final HasFields objectShape) {
        this.objectShape = objectShape;
    }

    public static Question<SObject> similarTo(HasFields objectShape) {
        return Instrumented.instanceOf(QueryData.class).withProperties(objectShape);
    }

    @Override
    public SObject answeredBy(Actor actor) {
        final CallPartnerSoapApi callApiAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        final PartnerConnection connection = callApiAbility.ensureConnection();
        try {
            final QueryResult qr = connection.query(objectShape.toSoql());
            return qr.getRecords()[0];
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
