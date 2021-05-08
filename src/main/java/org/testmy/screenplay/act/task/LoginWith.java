package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

import com.sforce.ws.ConnectorConfig;

import org.openqa.selenium.Cookie;
import org.testmy.screenplay.ability.CallPartnerSoapApi;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class LoginWith implements Task {

    public static LoginWith sessionId() {
        return new LoginWith();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final ConnectorConfig config = CallPartnerSoapApi.as(actor).ensureConnection().getConfig();
        String url = config.getServiceEndpoint();
        url = url.substring(0, url.indexOf("/services/"));
        System.out.println("URL >>> " + url);
        actor.attemptsTo(Open.url(url));
        // TODO: task to add cookie
        BrowseTheWeb.as(actor).getDriver().manage().addCookie(
                new Cookie(
                        "sid",
                        config.getSessionId()));
        actor.attemptsTo(
                Open.url(url),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));
    }
}
