package org.testmy.screenplay.act.task;

import org.testmy.data.matchers.HasFields;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

public class PopulateComposer implements Interaction {
    public static PopulateComposer from(HasFields objectShapeToCreate) {
        return null;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
    }
}
