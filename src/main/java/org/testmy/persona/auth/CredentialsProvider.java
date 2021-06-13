package org.testmy.persona.auth;

import org.apache.http.auth.Credentials;
import org.testmy.persona.Persona;

public interface CredentialsProvider {
    Credentials getCredentialsFor(Persona persona);
}