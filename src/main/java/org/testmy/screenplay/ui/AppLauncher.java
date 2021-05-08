package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class AppLauncher {

    public static Target icon() {
        return Target.the("App Launcher").locatedBy("//span[text()='App Launcher']/parent::*");
    }

    public static Target searchInput() {
        return Target.the("App Launcher").locatedBy(".//input[@placeholder='Search apps and items...']");
    }

    public static Target itemCalled(String itemName) {
        return Target.the("Item called: " + itemName)
                .locatedBy(
                        String.format("//div[@aria-label='Items']//a[@data-label='%s']", itemName));
    }
}
