package org.testmy.screenplay.ability;

import org.apache.http.auth.Credentials;
import org.testmy.persona.Persona;
import org.testmy.persona.PersonaManager;
import org.testmy.persona.auth.CredentialsProvider;

import lombok.Getter;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;

public class AuthenticateWithCredentials implements Ability, RefersToActor {
    private Actor actor;
    private String persona;
    private PersonaManager personaManager;
    private CredentialsProvider credentialsProvider;
    @Getter
    private String username;
    @Getter
    private String password;

    public AuthenticateWithCredentials(final String persona) {
        this.persona = persona;
    }

    void resolveCredentials() {
        if (null == username) {
            final Persona sfPersona = personaManager.reservePersonaFor(actor.getName(), persona);
            final Credentials credentials = credentialsProvider.getCredentialsFor(sfPersona);
            username = credentials.getUserPrincipal().getName();
            password = credentials.getPassword();
        }
    }

    public static AuthenticateWithCredentials as(final Actor actor) {
        final AuthenticateWithCredentials credAbility = SafeAbility.as(actor, AuthenticateWithCredentials.class);
        credAbility.resolveCredentials();
        return credAbility;
    }

    @Override
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
