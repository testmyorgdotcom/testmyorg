package org.testmy.persona.auth;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.testmy.persona.Persona;

public class LightCredentialsProvider implements ICredentialsProvider {
    public static String passwordPropertyName = "testmyorg.commonPass";

    @Override
    public Credentials getCredentialsFor(Persona testPersona) {
        return new UsernamePasswordCredentials(testPersona.getUsername(), System.getProperty(passwordPropertyName));
    }
}
