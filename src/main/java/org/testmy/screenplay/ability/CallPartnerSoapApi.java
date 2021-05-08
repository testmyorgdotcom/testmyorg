package org.testmy.screenplay.ability;

import java.util.Optional;
import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

import org.testmy.config.Config;

import lombok.Getter;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;

public class CallPartnerSoapApi implements Ability, RefersToActor, Config {
    @Getter
    private Actor actor;
    private Function<ConnectorConfig, PartnerConnection> connectionFactory;
    @Getter
    private Optional<PartnerConnection> connection = Optional.empty();

    public CallPartnerSoapApi(final Function<ConnectorConfig, PartnerConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public PartnerConnection ensureConnection() {

        if (!connection.isPresent()) {
            final AuthenticateWithCredentials credentials = AuthenticateWithCredentials.as(actor);
            final ConnectorConfig config = new ConnectorConfig();
            config.setUsername(credentials.getUsername());
            config.setPassword(credentials.getPassword());
            config.setAuthEndpoint(System.getProperty(PROPERTY_LOGIN_URL, PROPERTY_DEFAULT_LOGIN_URL));
            this.connection = Optional.of(connectionFactory.apply(config));
        }
        return connection.get();
    }

    public static CallPartnerSoapApi as(final Actor actor) {
        return SafeAbility.as(actor, CallPartnerSoapApi.class);
    }

    @Override
    public <T extends Ability> T asActor(Actor actor) {
        this.actor = actor;
        return (T) this;
    }
}
