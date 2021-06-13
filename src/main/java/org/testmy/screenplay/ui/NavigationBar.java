package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class NavigationBar {
    public static Target appName() {
        return Target.the("App Name")
                .locatedBy("//div[@data-aura-class='oneAppNavContainer']//span[contains(@class, 'appName')]//span");
    }
}
