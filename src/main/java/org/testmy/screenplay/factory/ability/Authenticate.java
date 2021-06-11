package org.testmy.screenplay.factory.ability;

import org.testmy.screenplay.ability.AuthenticateWithCredentials;

public class Authenticate {
    public static AuthenticateWithCredentials as(String persona) {
        return new AuthenticateWithCredentials(persona);
    }
}
