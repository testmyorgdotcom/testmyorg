package org.testmy.screenplay.act.performable;

import org.testmy.screenplay.factory.performable.Initialize;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class InitializeFramework implements Performable {
    private String[] sObjects = new String[0];

    public InitializeFramework withSObjects(final String... sObjects) {
        this.sObjects = sObjects;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(final T actor) {
        actor.attemptsTo(
                Initialize.allRecordTypes(),
                Initialize.referenceAttributesFor(sObjects),
                Initialize.digitalExperienceSites());
    }
}
