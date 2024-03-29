package org.testmy.screenplay.act.interaction.login;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

import com.sforce.ws.ConnectorConfig;

import org.testmy.URLHelper;
import org.testmy.config.Config;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class ViaFrontDoorUrl implements Interaction, Config {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();

    @Override
    public <T extends Actor> void performAs(T actor) {
        final CallPartnerSoapApi callApiAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        final ConnectorConfig connectorConfig = callApiAbility.ensureConnection().getConfig();
        final String endPointUrl = connectorConfig.getServiceEndpoint();
        final String loginUrl = URLHelper.extractMainUrl(endPointUrl);
        final String sessionId = connectorConfig.getSessionId();
        final String targetUrl = String.format(PATTERN_URL_LOGIN_VIA_FRONTDOOR, loginUrl, sessionId);
        actor.attemptsTo(
                Open.url(targetUrl),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));
    }
}
