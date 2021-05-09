package org.testmy.screenplay.act.interaction.navigate;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.is;

import org.testmy.screenplay.question.ui.AppName;
import org.testmy.screenplay.ui.AppLauncher;
import org.testmy.screenplay.ui.WebPage;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.waits.WaitUntil;

@AllArgsConstructor
public class NavigateToApp implements Interaction {
    private String appName;

    public static NavigateToApp called(final String appName) {
        return Instrumented.instanceOf(NavigateToApp.class).withProperties(appName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Check.whether(AppName.displayed(), is(appName)).otherwise(
                        Click.on(AppLauncher.icon()),
                        SendKeys.of(appName).into(AppLauncher.searchInput()),
                        WaitUntil.the(AppLauncher.appCalled(appName), isVisible()),
                        Click.on(AppLauncher.appCalled(appName)),
                        WaitUntil.the(WebPage.loadingLogo(), isNotVisible())));
    }
}
