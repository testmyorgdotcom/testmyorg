package org.testmy.screenplay.ability;

import java.util.Optional;
import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

import org.apache.commons.lang3.StringUtils;
import org.testmy.URLHelper;
import org.testmy.config.Config;
import org.testmy.persona.auth.Credentials;

import lombok.Getter;
import lombok.Setter;
import net.serenitybdd.screenplay.Actor;

public class CallPartnerSoapApi implements SalesforceAbility, Config {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();
    Function<ConnectorConfig, PartnerConnection> connectionFactory;
    @Setter
    private Actor actor;
    @Getter
    private Optional<PartnerConnection> connection = Optional.empty();

    public CallPartnerSoapApi(final Function<ConnectorConfig, PartnerConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public PartnerConnection ensureConnection() {
        if (!connection.isPresent()) {
            final ConnectorConfig config = setupConfig();
            this.connection = Optional.of(connectionFactory.apply(config));
        }
        return connection.get();
    }

    ConnectorConfig setupConfig() {
        final AuthenticateWithCredentials credAbility = abilityProvider.as(actor, AuthenticateWithCredentials.class);
        final Credentials credentials = credAbility.resolveCredentials();
        final ConnectorConfig config = new ConnectorConfig();
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.setAuthEndpoint(constructEndPoint());
        final String proxyUrl = System.getProperty(Config.PROPERTY_URL_PROXY);
        if (!StringUtils.isEmpty(proxyUrl)) {
            setProxy(config, proxyUrl);
        }
        return config;
    }

    private void setProxy(final ConnectorConfig config,
            final String proxyUrl) {
        final String host = URLHelper.extractDomain(proxyUrl);
        final Integer port = URLHelper.extractPort(proxyUrl);
        config.setProxy(host, port);
    }

    private String constructEndPoint() {
        final String loginUrl = System.getProperty(PROPERTY_URL_LOGIN, PROPERTY_DEFAULT_URL_LOGIN);
        final String partnerApiVersion = System.getProperty(PROPERTY_VERSION_API_SOAP_PARTNER,
                PROPERTY_DEFAULT_VERSION_API_SOAP_PARTNER);
        return String.format(PATTERN_URL_PARTNER_SOAP_API, loginUrl, partnerApiVersion);
    }
}
