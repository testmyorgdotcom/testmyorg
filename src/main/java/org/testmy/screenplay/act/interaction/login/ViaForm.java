package org.testmy.screenplay.act.interaction.login;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

import org.testmy.config.Config;
import org.testmy.screenplay.ability.AuthenticateWithCredentials;
import org.testmy.screenplay.ui.LoginForm;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class ViaForm implements Interaction, Config {

    @Override
    public <T extends Actor> void performAs(T actor) {
        final String loginUrl = System.getProperty(PROPERTY_LOGIN_URL, PROPERTY_DEFAULT_LOGIN_URL);
        final AuthenticateWithCredentials credentials = AuthenticateWithCredentials.as(actor);
        actor.attemptsTo(
                Open.url(loginUrl),
                SendKeys.of(credentials.getUsername()).into(LoginForm.usernameInput()),
                SendKeys.of(credentials.getPassword()).into(LoginForm.passwordInput()),
                Click.on(LoginForm.loginButton()),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));
    }
}
