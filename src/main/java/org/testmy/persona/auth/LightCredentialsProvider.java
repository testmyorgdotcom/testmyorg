package org.testmy.persona.auth;

import org.testmy.persona.Persona;

public class LightCredentialsProvider implements CredentialsProvider {
    static String passwordPropertyName = "testmyorg.commonPass";

    @Override
    public Credentials getCredentialsFor(Persona persona) {
        return new Credentials(persona.getUsername(), System.getProperty(passwordPropertyName));
    }
}
