package org.testmy.screenplay.question;

import com.sforce.soap.partner.PartnerConnection;

import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class PartnerConnectionQuestion implements Question<PartnerConnection> {
    private AbilityProvider abilityProvider = AbilityProvider.getInstance();

    @Override
    public PartnerConnection answeredBy(final Actor actor) {
        final CallPartnerSoapApi partnerApiAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        return partnerApiAbility.ensureConnection();
    }
}
