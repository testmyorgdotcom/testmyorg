package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class WebPage {

    public static Target loadingLogo() {
        return Target.the("Loading Logo").locatedBy("//div[@class='loadingText']");
    }

    public static Target spinner() {
        return Target.the("Spinner")
                .locatedBy(
                        "//div[@data-aura-class='forceListViewManagerPrimaryDisplayManager']//span[text()='Loading...']");
    }

}
