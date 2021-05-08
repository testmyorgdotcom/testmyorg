package org.testmy.screenplay.factory.ability;

import org.testmy.persona.auth.ICredentialsProvider;
import org.testmy.persona.auth.LightCredentialsProvider;
import org.testmy.screenplay.ability.AuthenticateWithCredentials;

public class Authenticate {
    private final static ICredentialsProvider DEFAULT_CREDENTIALS_PROVIDER = new LightCredentialsProvider();

    public static AuthenticateWithCredentials withCredentials() {
        return using(DEFAULT_CREDENTIALS_PROVIDER);
    }

    public static AuthenticateWithCredentials using(final ICredentialsProvider credentialsProvider) {
        return new AuthenticateWithCredentials(credentialsProvider);
    }
}
