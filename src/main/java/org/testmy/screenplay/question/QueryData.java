package org.testmy.screenplay.question;

import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;

import org.testmy.config.Config;
import org.testmy.data.SalesforceDataAction;
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
        final SalesforceDataAction sfDataAction = new SalesforceDataAction(connection);
        final String query = objectShape.toSoql();
        final List<SObject> sObjects = sfDataAction.queryRecords(query);
        if (sObjects.isEmpty()) {
            throw new TestRuntimeException(String.format(Config.PATTERN_MESSAGE_ERROR_NO_DATA_FOR_QUERY, query));
        }
        return sObjects.get(0);
    }
}
