package org.testmy.screenplay.ability;

import org.testmy.persona.Persona;
import org.testmy.persona.PersonaManager;
import org.testmy.persona.auth.Credentials;
import org.testmy.persona.auth.CredentialsProvider;
import org.testmy.persona.auth.LightCredentialsProvider;

import lombok.Getter;
import lombok.Setter;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Shared;

public class AuthenticateWithCredentials implements SalesforceAbility {
    @Setter
    private Actor actor;
    private String persona;
    @Shared
    PersonaManager personaManager;
    CredentialsProvider credentialsProvider = new LightCredentialsProvider();
    @Getter
    private Credentials credentials;

    public AuthenticateWithCredentials(final String persona) {
        this.persona = persona;
    }

    public Credentials resolveCredentials() {
        if (null == this.credentials) {
            final Persona sfPersona = personaManager.reservePersonaFor(actor.getName(), persona);
            final Credentials credentials = credentialsProvider.getCredentialsFor(sfPersona);
            this.credentials = credentials;
        }
        return this.credentials;
    }
}
