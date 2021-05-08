package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class LoginForm {

    public static Target usernameInput() {
        return Target.the("Username Input")
                .locatedBy("//*[@id='username']");
    }

    public static Target passwordInput() {
        return Target.the("Password Input")
                .locatedBy("//*[@id='password']");
    }

    public static Target loginButton() {
        return Target.the("Login Button")
                .locatedBy("//*[@id='Login']");
    }

    public static Target continueButton() {
        return Target.the("Continue Button")
                .locatedBy("//input[@name='thePage:inputForm:continue']");
    }

    public static Target registerPhone() {
        return Target.the("I do not want to register phone")
                .locatedBy("//a[contains(text(), 'Register')]");
    }
}
