package org.testmy.screenplay.question;

import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class PartnerConnection implements Question<String> {
    @Override
    public String answeredBy(final Actor actor) {
        return CallPartnerSoapApi.as(actor).getConnection().get().getConfig().getSessionId();
    }

    public static PartnerConnection sessionId() {
        return new PartnerConnection();
    }
}
