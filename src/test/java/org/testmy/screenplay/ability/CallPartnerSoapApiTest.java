package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.error.AbilityIsAbsentException;
import org.testmy.persona.auth.CredentialsProvider;
import org.testmy.screenplay.factory.ability.Authenticate;
import org.testmy.screenplay.factory.ability.Call;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class CallPartnerSoapApiTest {
    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();
    @Mock
    AuthenticateWithCredentials authInfo;
    @Mock
    CredentialsProvider credentialsProvider;
    final Credentials testCredentials = new UsernamePasswordCredentials("x", "y");
    @Mock
    Function<ConnectorConfig, PartnerConnection> connectionFactory;
    @Mock
    PartnerConnection mockPartnerConnection;
    final String actorName = "Tester";
    final Actor actor = Actor.named(actorName);
    final String otherActorName = "Other Tester";
    final Actor actorWithoutAbility = Actor.named(otherActorName);

    @Before
    public void before() {
        actor.can(Call.partnerApi(connectionFactory));
        actor.can(Authenticate.as("Sales"));
    }

    @Test
    public void hasInformationAboutActor() {
        final CallPartnerSoapApi callPartnerApiAbility = CallPartnerSoapApi.as(actor);
        assertThat(callPartnerApiAbility.getActor(), is(actor));
    }

    @Test
    public void hasNoPartnerConnectionInitially() {
        final CallPartnerSoapApi callPartnerApiAbility = CallPartnerSoapApi.as(actor);
        assertThat(callPartnerApiAbility.getConnection().isPresent(), is(false));
    }

    @Test
    public void establishesConnection() {
        when(connectionFactory.apply(any())).thenReturn(mockPartnerConnection);
        final CallPartnerSoapApi callPartnerApiAbility = CallPartnerSoapApi.as(actor);
        try (MockedStatic<AuthenticateWithCredentials> auth = Mockito.mockStatic(AuthenticateWithCredentials.class)) {
            auth.when(() -> AuthenticateWithCredentials.as(actor))
                    .thenReturn(authInfo);
            assertThat(callPartnerApiAbility.getConnection().isPresent(), is(false));
            callPartnerApiAbility.ensureConnection();
            assertThat(callPartnerApiAbility.getConnection().isPresent(), is(true));
        }
    }

    @Test
    public void reuseEstablishedConnection() {
        when(connectionFactory.apply(any())).thenReturn(mockPartnerConnection);
        final CallPartnerSoapApi callPartnerApiAbility = CallPartnerSoapApi.as(actor);
        try (MockedStatic<AuthenticateWithCredentials> auth = Mockito.mockStatic(AuthenticateWithCredentials.class)) {
            auth.when(() -> AuthenticateWithCredentials.as(actor))
                    .thenReturn(authInfo);
            assertThat(callPartnerApiAbility.getConnection().isPresent(), is(false));
            callPartnerApiAbility.ensureConnection();
            callPartnerApiAbility.ensureConnection();
            assertThat(callPartnerApiAbility.getConnection().isPresent(), is(true));
            verify(connectionFactory, times(1)).apply(any());
        }
    }

    @Test
    public void failsIfWasNotGivenAbility() {
        final String expectedMessage = String.format("Actor: %s has no requested ability: %s",
                otherActorName,
                CallPartnerSoapApi.class.getSimpleName());
        exceptionRule.expect(AbilityIsAbsentException.class);
        exceptionRule.expectMessage(expectedMessage);
        CallPartnerSoapApi.as(actorWithoutAbility);
    }

    @Test
    public void failsToEstablishConnectionWithoutCredentials() {
        final String expectedMessage = String.format("Actor: %s has no requested ability: %s",
                otherActorName,
                AuthenticateWithCredentials.class.getSimpleName());
        exceptionRule.expect(AbilityIsAbsentException.class);
        exceptionRule.expectMessage(expectedMessage);
        actorWithoutAbility.can(Call.partnerApi(connectionFactory));
        final CallPartnerSoapApi callPartnerApiAbility = CallPartnerSoapApi.as(actorWithoutAbility);
        assertThat(callPartnerApiAbility.getConnection().isPresent(), is(false));
        callPartnerApiAbility.ensureConnection();
    }
}
