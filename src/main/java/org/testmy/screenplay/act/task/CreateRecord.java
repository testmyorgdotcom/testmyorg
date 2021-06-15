package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.TestDataManager;
import org.testmy.data.matchers.HasFields;
import org.testmy.screenplay.factory.KeyboardShortcuts;
import org.testmy.screenplay.ui.GlobalActions;
import org.testmy.screenplay.ui.NewContact.LastName;
import org.testmy.screenplay.ui.Toast;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Shared;

public class CreateRecord implements Task {
    @Shared
    private TestDataManager dataManager;
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
        final SObject sOjbect = dataManager.constructSObject(objectShapeToCreate);
        actor.attemptsTo(
                Click.on(GlobalActions.createButton()),
                WaitUntil.the(GlobalActions.createMenuList(), isVisible()),
                Click.on(GlobalActions.createMenuListItem(globalAction)),
                WaitUntil.the(GlobalActions.form(globalAction), isVisible()),
                PopulateComposer.from(objectShapeToCreate),
                SendKeys.of(sOjbect.getField("LastName").toString()).into(LastName.input()),
                KeyboardShortcuts.save(),
                WaitUntil.the(Toast.success(), isVisible()),
                Click.on(Toast.objectName()),
                StoreObjectAtScene.intoDataCache());
    }
}
