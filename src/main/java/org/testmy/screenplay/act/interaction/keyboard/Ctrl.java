package org.testmy.screenplay.act.interaction.keyboard;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

@AllArgsConstructor
public class Ctrl implements Interaction {
    private CharSequence shortcut;

    public static Ctrl plus(final CharSequence shortcut) {
        return Instrumented.instanceOf(Ctrl.class).withProperties(shortcut);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final Actions actionProvider = new Actions(BrowseTheWeb.as(actor).getDriver());
        final Action keydown = actionProvider.keyDown(Keys.CONTROL).sendKeys(shortcut).build();
        keydown.perform();
        actionProvider.sendKeys(Keys.NULL);
    }
}
