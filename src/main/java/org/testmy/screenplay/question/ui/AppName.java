package org.testmy.screenplay.question.ui;

import org.testmy.screenplay.ui.NavigationBar;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.WebElementQuestion;

public class AppName implements Question<String> {
    private final Question<WebElementState> whatIsCurrentApp = WebElementQuestion.the(NavigationBar.appName());

    public static Question<String> displayed() {
        return new AppName();
    }

    @Override
    public String answeredBy(Actor actor) {
        final WebElementState webElementState = whatIsCurrentApp.answeredBy(actor);
        return webElementState.getText();
    }
}
