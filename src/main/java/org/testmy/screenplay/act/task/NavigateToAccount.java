package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import org.testmy.screenplay.ui.AppLauncher;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.waits.WaitUntil;

@AllArgsConstructor
public class NavigateToAccount implements Task {
    private String accountName;

    public static Task called(String accountName) {
        return Instrumented.instanceOf(NavigateToAccount.class).withProperties(accountName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(AppLauncher.icon()),
                SendKeys.of("accounts").into(AppLauncher.searchInput()),
                WaitUntil.the(AppLauncher.itemCalled("Accounts"), isVisible()),
                JavaScriptClick.on(AppLauncher.itemCalled("Accounts")));
    }
}
