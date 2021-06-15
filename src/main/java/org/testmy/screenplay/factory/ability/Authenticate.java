package org.testmy.screenplay.factory.ability;

import org.testmy.screenplay.ability.AuthenticateWithCredentials;

import net.serenitybdd.core.steps.Instrumented;

public class Authenticate {
    public static AuthenticateWithCredentials as(String persona) {
        return Instrumented.instanceOf(AuthenticateWithCredentials.class).withProperties(persona);
    }
}
