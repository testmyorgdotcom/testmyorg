package org.testmy.screenplay.act.interaction.global;

import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;

@AllArgsConstructor
public class GlobalAction implements Interaction {
    private String actioName;

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(plusIcon()),
                Click.on(globalActionItemFor(actioName)));
    }

    public static Target plusIcon() {
        return Target.the("Global Actions: '+' icon")
                .locatedBy("//header//ul/li//a[contains(@class, 'globalCreateTrigger')]");
    }

    public static Target createMenuList() {
        return Target.the("Global Actions: Menu List")
                .locatedBy("//header//div[@class='globalCreateMenuList']");
    }

    public static Target globalActionItemFor(final String globalActionName) {
        return Target.the("Global Actions: Menu Item")
                .locatedBy("//header//div[@class='globalCreateMenuList']//ul/li/a[@title='" + globalActionName + "']");
    }

    public static Target form(String globalActionName) {
        return Target.the("Global Actions: Create " + globalActionName)
                .locatedBy("//div[contains(@class, 'slds-docked-composer')]//h2[@title='" + globalActionName + "']");
    }
}
