package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.Proxy;
import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.config.Config;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class CallPartnerSoapApiTest {
    @Mock
    AbilityProvider abilityProvider;
    @Mock
    AuthenticateWithCredentials authInfo;
    @Mock
    Function<ConnectorConfig, PartnerConnection> connectionFactory;
    @Mock
    PartnerConnection mockPartnerConnection;
    @InjectMocks
    CallPartnerSoapApi callApiAbility;
    final String actorName = "Tester";
    final Actor actor = Actor.named(actorName);

    @Before
    public void before() {
        callApiAbility.abilityProvider = abilityProvider;
        when(abilityProvider.as(actor, AuthenticateWithCredentials.class)).thenReturn(authInfo);
        when(connectionFactory.apply(any())).thenReturn(mockPartnerConnection);
        callApiAbility.asActor(actor);
    }

    @Test
    public void hasNoPartnerConnectionInitially() {
        assertThat(callApiAbility.getConnection().isPresent(), is(false));
    }

    @Test
    public void ensureConnection_usesConnectionFactoryToGetConnection() {
        callApiAbility.ensureConnection();
        verify(connectionFactory, times(1)).apply(any());
    }

    @Test
    public void ensureConnection_storesConnection() {
        assertThat(callApiAbility.getConnection().isPresent(), is(false));
        callApiAbility.ensureConnection();
        assertThat(callApiAbility.getConnection().isPresent(), is(true));
    }

    @Test
    public void ensureConnection_reuseEstablishedConnection() {
        final PartnerConnection conn1 = callApiAbility.ensureConnection();
        final PartnerConnection conn2 = callApiAbility.ensureConnection();
        assertThat(conn2, is(conn1));
        verify(connectionFactory, times(1)).apply(any());
    }

    @Test
    public void setupConfig_UseProxyIfAvailableAsSystemProperty() {
        final String proxyHost = "proxyHost";
        final String proxyPort = "8888";
        final String proxyUrl = String.format("http://%s:%s", proxyHost, proxyPort);
        System.setProperty(Config.PROPERTY_URL_PROXY, proxyUrl);
        final ConnectorConfig configWithProxy = callApiAbility.setupConfig();
        final Proxy proxy = configWithProxy.getProxy();
        assertThat(proxy, is(notNullValue()));
        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        System.out.println(proxy);
    }

    @Test
    public void setupConfig_NoProxyIfUnAvailableAsSystemProperty() {
        System.setProperty(Config.PROPERTY_URL_PROXY, "");
        final ConnectorConfig configWithProxy = callApiAbility.setupConfig();
        final Proxy proxy = configWithProxy.getProxy();
        assertThat(proxy, is(notNullValue()));
        assertThat(proxy.type(), is(Proxy.Type.DIRECT));
    }
}
