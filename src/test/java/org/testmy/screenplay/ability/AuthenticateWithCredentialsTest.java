package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.persona.PersonaManager;
import org.testmy.persona.auth.Credentials;
import org.testmy.persona.auth.CredentialsProvider;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticateWithCredentialsTest {
    String actorName = "Mike";
    Actor mike = new Actor(actorName);
    String testUser = "Test User";
    String testPass = "123456";
    @Mock
    private CredentialsProvider credentialsProvider;
    @Mock
    private PersonaManager personaManager;

    @Before
    public void before() {
        when(credentialsProvider.getCredentialsFor(any()))
                .thenReturn(new Credentials(testUser, testPass));
    }

    @Test
    public void resolveCredentials_usesPersonaManagerToFindPersonaForActor() {
        final String salesManagerPersonaName = "Sales Manager";
        final AuthenticateWithCredentials authInfo = getAuthenticateAbilityWithMocksInjected(salesManagerPersonaName);
        authInfo.resolveCredentials();
        verify(personaManager).reservePersonaFor(actorName, salesManagerPersonaName);
    }

    @Test
    public void resolveCredentials_usesCredentilasProviderToGetAuthInfoForPersona() {
        final AuthenticateWithCredentials authInfo = getAuthenticateAbilityWithMocksInjected("Admin");
        authInfo.resolveCredentials();
        verify(credentialsProvider).getCredentialsFor(any());
    }

    @Test
    public void resolveCredentials_storesResolvedUsernameAndPassword() {
        final AuthenticateWithCredentials authInfo = getAuthenticateAbilityWithMocksInjected("Admin");
        Credentials credentials = authInfo.getCredentials();
        assertThat(credentials, is(nullValue()));
        credentials = authInfo.resolveCredentials();
        assertThat(credentials.getUsername(), is(testUser));
        assertThat(credentials.getPassword(), is(testPass));
    }

    @Test
    public void resolveCredentials_doesNotCallServicesIfUsernameAndPasswordAreResolvedAlready() {
        final String adminPersonaName = "Admin";
        final AuthenticateWithCredentials authInfo = getAuthenticateAbilityWithMocksInjected(adminPersonaName);
        final Integer numOfResolveCalls = 10;
        IntStream.range(0, numOfResolveCalls).forEach(i -> {
            authInfo.resolveCredentials();
        });
        verify(personaManager, times(1)).reservePersonaFor(actorName, adminPersonaName);
        verify(credentialsProvider, times(1)).getCredentialsFor(any());
    }

    private AuthenticateWithCredentials getAuthenticateAbilityWithMocksInjected(String salesManagerPersonaName) {
        final AuthenticateWithCredentials authInfo = new AuthenticateWithCredentials(salesManagerPersonaName);
        authInfo.personaManager = personaManager;
        authInfo.credentialsProvider = credentialsProvider;
        authInfo.asActor(mike);
        return authInfo;
    }
}
