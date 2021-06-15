package org.testmy.screenplay.ability;

import java.util.Optional;
import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

import org.testmy.config.Config;

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

    private ConnectorConfig setupConfig() {
        final AuthenticateWithCredentials credentials = abilityProvider.as(actor, AuthenticateWithCredentials.class);
        credentials.resolveCredentials();
        final ConnectorConfig config = new ConnectorConfig();
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.setAuthEndpoint(constructEndPoint());
        return config;
    }

    private String constructEndPoint() {
        final String loginUrl = System.getProperty(PROPERTY_URL_LOGIN, PROPERTY_DEFAULT_URL_LOGIN);
        final String partnerApiVersion = System.getProperty(PROPERTY_VERSION_API_SOAP_PARTNER,
                PROPERTY_DEFAULT_VERSION_API_SOAP_PARTNER);
        return String.format(PATTERN_URL_PARTNER_SOAP_API, loginUrl, partnerApiVersion);
    }
}
