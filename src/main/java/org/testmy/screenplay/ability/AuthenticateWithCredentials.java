package org.testmy.screenplay.ability;

import org.apache.http.auth.Credentials;
import org.testmy.persona.Persona;
import org.testmy.persona.auth.ICredentialsProvider;
import org.testmy.screenplay.fact.PersonaBehaviour;

import lombok.Getter;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;

public class AuthenticateWithCredentials implements Ability, RefersToActor {
    private Actor actor;
    private ICredentialsProvider credentialsProvider;
    @Getter
    private String username;
    @Getter
    private String password;

    public AuthenticateWithCredentials(final ICredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public static AuthenticateWithCredentials as(final Actor actor) {
        final AuthenticateWithCredentials credAbility = SafeAbility.as(actor, AuthenticateWithCredentials.class);
        final Credentials credentials = credAbility.getCredentials();
        credAbility.username = credentials.getUserPrincipal().getName();
        credAbility.password = credentials.getPassword();
        return credAbility;
    }

    private Credentials getCredentials() {
        final Persona persona = PersonaBehaviour.of(actor);
        return credentialsProvider.getCredentialsFor(persona);
    }

    @Override
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
