package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import org.testmy.screenplay.ui.NewContact;
import org.testmy.screenplay.ui.NewContact.LastName;
import org.testmy.screenplay.ui.NewContact.Save;
import org.testmy.screenplay.ui.Toast;
import org.testmy.screenplay.ui.WebPage;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.waits.WaitUntil;

@AllArgsConstructor
public class CreateContact implements Task {
    private String lastName;

    public static Performable withLastName(final String lastName) {
        return Instrumented.instanceOf(CreateContact.class).withProperties(lastName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(NewContact.quickActionButton(), isVisible()),
                Click.on(NewContact.quickActionButton()),
                WaitUntil.the(NewContact.quickActionLayout(), isVisible()),
                SendKeys.of(lastName).into(LastName.input()),
                Click.on(Save.button()),
                WaitUntil.the(Toast.success(), isVisible()),
                Click.on(Toast.objectName()),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()),
                StoreObjectAtScene.intoDataCache());
        // final WebDriver wd = actor.abilityTo(BrowseTheWeb.class).getDriver();
        // final String wd.getCurrentUrl();
    }
}
