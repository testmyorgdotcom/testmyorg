package org.testmy.screenplay.ui.global.composer;

import net.serenitybdd.screenplay.targets.Target;

public class NewContact {
    public static Target firstName() {
        return Target.the("First Name input field").locatedBy("//input[contains(@class, 'firstName')]");
    }

    public static Target lastName() {
        return Target.the("Last Name input field").locatedBy("//input[contains(@class, 'lastName')]");
    }

    public static Target email() {
        return Target.the("Email input field").locatedBy("//input[@class=' input' and @inputmode='email']");
    }

    public static Target phone() {
        return Target.the("Phone input field").locatedBy("//input[@class=' input' and @type='tel']");
    }
}
