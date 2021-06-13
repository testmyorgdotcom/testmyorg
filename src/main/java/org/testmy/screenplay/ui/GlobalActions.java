package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class GlobalActions {
    public static Target createButton() {
        return Target.the("Global Actions: Create Button")
                .locatedBy("//header//ul/li//a[contains(@class, 'globalCreateTrigger')]");
    }

    public static Target createMenuList() {
        return Target.the("Global Actions: Menu List")
                .locatedBy("//header//div[@class='globalCreateMenuList']");
    }

    public static Target createMenuListItem(final String globalActionName) {
        return Target.the("Global Actions: Menu Item")
                .locatedBy("//header//div[@class='globalCreateMenuList']//ul/li/a[@title='" + globalActionName + "']");
    }

    public static Target form(String globalActionName) {
        return Target.the("Global Actions: Create " + globalActionName)
                .locatedBy("//div[contains(@class, 'slds-docked-composer')]//h2[@title='" + globalActionName + "']");
    }
}
