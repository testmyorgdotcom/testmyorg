package org.testmy.persona.auth;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.testmy.persona.Persona;

public class LightCredentialsProvider implements CredentialsProvider {
    static String passwordPropertyName = "testmyorg.commonPass";

    @Override
    public Credentials getCredentialsFor(Persona persona) {
        return new UsernamePasswordCredentials(persona.getUsername(), System.getProperty(passwordPropertyName));
    }
}
