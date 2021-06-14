package org.testmy.screenplay.act;

import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class Ensure implements Performable {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();

    public static Ensure partnerConnection() {
        return new Ensure();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final CallPartnerSoapApi callApiAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        callApiAbility.ensureConnection();
    }
}
