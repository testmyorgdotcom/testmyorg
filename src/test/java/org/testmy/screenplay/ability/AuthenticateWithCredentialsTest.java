package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.IntStream;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.persona.PersonaManager;
import org.testmy.persona.auth.CredentialsProvider;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticateWithCredentialsTest {
    String actorName = "Mike";
    Actor testActor = new Actor(actorName);

    String persona = "Sales Manager";
    String testUser = "Test User";
    String testPass = "123456";

    @Mock
    private CredentialsProvider credentialsProvider;
    @Mock
    private PersonaManager personaManager;
    @InjectMocks
    final AuthenticateWithCredentials authInfo = new AuthenticateWithCredentials(persona);

    @Before
    public void before() {
        authInfo.asActor(testActor);
        when(credentialsProvider.getCredentialsFor(any()))
                .thenReturn(new UsernamePasswordCredentials(testUser, testPass));
    }

    @Test
    public void resolveCredentialsForPersonaUsingCredentialsProvider() {
        authInfo.resolveCredentials();

        assertThat(authInfo.getUsername(), equalTo(testUser));
        assertThat(authInfo.getPassword(), equalTo(testPass));

        verify(personaManager, times(1)).reservePersonaFor(actorName, persona);
        verify(credentialsProvider, times(1)).getCredentialsFor(any());
    }

    @Test
    public void avoidResolvingIfUsernamePasswordAreAvailableAlready() {
        final Integer numOfResolveCalls = 10;
        IntStream.range(0, numOfResolveCalls).forEach(i -> {
            authInfo.resolveCredentials();
        });

        verify(personaManager, times(1)).reservePersonaFor(actorName, persona);
        verify(credentialsProvider, times(1)).getCredentialsFor(any());
    }
}
