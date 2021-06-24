package org.testmy.persona.auth;

import org.testmy.persona.Persona;

public interface CredentialsProvider {
    Credentials getCredentialsFor(Persona persona);
}
