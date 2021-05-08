package org.testmy.screenplay.act.interaction.login;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import org.testmy.URLHelper;
import org.testmy.config.Config;
import org.testmy.screenplay.ability.AuthenticateWithCredentials;
import org.testmy.screenplay.ui.LoginForm;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class WithCredentialsInUrl implements Interaction, Config {

    @Override
    public <T extends Actor> void performAs(T actor) {
        final String loginUrl = System.getProperty(PROPERTY_LOGIN_URL, PROPERTY_DEFAULT_LOGIN_URL);
        final AuthenticateWithCredentials credentials = AuthenticateWithCredentials.as(actor);
        final String password = URLHelper.encode(credentials.getPassword());
        final String username = URLHelper.encode(credentials.getUsername());

        final String targetUrl = String.format(PATTERN_LOGIN_WITH_CREDENTIALS_IN_URL, loginUrl, username, password);

        System.err.println("Target URL: " + targetUrl);

        actor.attemptsTo(
                Open.url(targetUrl),
                Check.whether(WebElementQuestion.stateOf(LoginForm.continueButton()), isVisible()).andIfSo(
                        Click.on(LoginForm.continueButton())),
                Check.whether(WebElementQuestion.stateOf(LoginForm.registerPhone()), isVisible()).andIfSo(
                        Click.on(LoginForm.registerPhone())),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));
    }
}
