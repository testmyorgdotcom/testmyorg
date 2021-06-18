package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import org.testmy.data.matchers.HasFields;
import org.testmy.screenplay.factory.KeyboardShortcuts;
import org.testmy.screenplay.factory.interaction.UseGlobalAction;
import org.testmy.screenplay.ui.Toast;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class CreateRecord implements Task {
    private String globalAction;
    private HasFields objectShapeToCreate;

    public CreateRecord() {
    }

    public CreateRecord(final String globalAction) {
        this.globalAction = globalAction;
    }

    public static CreateRecord viaGlobalAction(final String globalAction) {
        return Instrumented.instanceOf(CreateRecord.class).withProperties(globalAction);
    }

    public Task of(final HasFields shape) {
        this.objectShapeToCreate = shape;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                UseGlobalAction.called(globalAction),
                PopulateContactComposer.from(objectShapeToCreate),
                KeyboardShortcuts.save(),
                WaitUntil.the(Toast.success(), isVisible()),
                Click.on(Toast.objectName()),
                StoreObjectAtScene.intoDataCache());
    }
}
