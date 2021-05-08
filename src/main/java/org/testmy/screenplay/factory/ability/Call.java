package org.testmy.screenplay.factory.ability;

import java.util.function.Function;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

public class Call {

    public static CallPartnerSoapApi partnerApi() {
        return partnerApi((config) -> {

            try {
                return new PartnerConnection(config);
            }
            catch (final ConnectionException e) {
                throw new TestRuntimeException(e);
            }
        });
    }

    public static CallPartnerSoapApi partnerApi(final Function<ConnectorConfig, PartnerConnection> connectionFactory) {
        return new CallPartnerSoapApi(connectionFactory);
    }
}
