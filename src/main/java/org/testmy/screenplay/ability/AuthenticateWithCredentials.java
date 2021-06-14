package org.testmy.screenplay.ability;

import org.apache.http.auth.Credentials;
import org.testmy.persona.Persona;
import org.testmy.persona.PersonaManager;
import org.testmy.persona.auth.CredentialsProvider;

import lombok.Getter;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Shared;

public class AuthenticateWithCredentials implements SalesforceAbility {
    private Actor actor;
    private String persona;
    @Shared
    PersonaManager personaManager;
    @Shared
    CredentialsProvider credentialsProvider;
    @Getter
    private String username;
    @Getter
    private String password;

    public AuthenticateWithCredentials(final String persona) {
        this.persona = persona;
    }

    public void resolveCredentials() {
        if (null == username) {
            final Persona sfPersona = personaManager.reservePersonaFor(actor.getName(), persona);
            final Credentials credentials = credentialsProvider.getCredentialsFor(sfPersona);
            username = credentials.getUserPrincipal().getName();
            password = credentials.getPassword();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
