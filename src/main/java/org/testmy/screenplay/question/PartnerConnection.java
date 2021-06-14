package org.testmy.screenplay.question;

import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class PartnerConnection implements Question<String> {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();

    @Override
    public String answeredBy(final Actor actor) {
        final CallPartnerSoapApi callApiAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        return callApiAbility.getConnection().get().getConfig().getSessionId();
    }

    public static PartnerConnection sessionId() {
        return new PartnerConnection();
    }
}
