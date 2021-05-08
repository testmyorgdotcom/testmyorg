package org.testmy.screenplay.act.interaction.login;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

import com.sforce.ws.ConnectorConfig;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testmy.URLHelper;
import org.testmy.config.Config;
import org.testmy.screenplay.ability.CallPartnerSoapApi;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class WithSessionIdAsCookie implements Interaction, Config {

    @Override
    public <T extends Actor> void performAs(T actor) {
        final ConnectorConfig connectorConfig = CallPartnerSoapApi.as(actor).ensureConnection().getConfig();
        final String endPointUrl = connectorConfig.getServiceEndpoint();
        final String mainUrl = URLHelper.extractMainUrl(endPointUrl);
        final String domain = URLHelper.extractDomain(mainUrl);
        final String sessionId = connectorConfig.getSessionId();

        final Cookie sessionCookie = new Cookie.Builder("sid", sessionId)
                .domain(domain)
                .build();

        final WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        driver.get(mainUrl);
        driver.manage().addCookie(sessionCookie);

        actor.attemptsTo(
                Open.url(mainUrl),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));
    }
}
