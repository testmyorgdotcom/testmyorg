package org.testmy.screenplay.question;

import java.util.List;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.config.Config;
import org.testmy.data.matchers.HasFields;
import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.question.data.SObjectsQuestion;

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
        final String query = objectShape.toSoql();
        final List<SObject> sObjects = actor.asksFor(SObjectsQuestion.withQuery(query));
        if (sObjects.isEmpty()) {
            throw new TestRuntimeException(String.format(Config.PATTERN_MESSAGE_ERROR_NO_DATA_FOR_QUERY, query));
        }
        return sObjects.get(0);
    }
}
