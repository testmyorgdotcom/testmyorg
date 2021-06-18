package org.testmy.screenplay.act.task;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.MatcherToSOjbectConstructor;
import org.testmy.data.matchers.HasFields;
import org.testmy.screenplay.factory.KeyboardShortcuts;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.targets.Target;

public class PopulateContactComposer implements Interaction {
    private SObject contactSObject;

    public PopulateContactComposer(final HasFields contactShape) {
        final MatcherToSOjbectConstructor sOjbectConstructor = new MatcherToSOjbectConstructor();
        this.contactSObject = sOjbectConstructor.constructSObject(contactShape);
    }

    public static PopulateContactComposer from(final HasFields contactShape) {
        return Instrumented.instanceOf(PopulateContactComposer.class).withProperties(contactShape);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Check.whether(contactSObject.getField("LastName") != null)
                        .andIfSo(SendKeys.of(contactSObject.getField("LastName").toString())
                                .into(lastName())),
                Check.whether(contactSObject.getField("FirstName") != null)
                        .andIfSo(SendKeys.of(contactSObject.getField("FirstName").toString())
                                .into(firstName())),
                Check.whether(contactSObject.getField("Email") != null)
                        .andIfSo(SendKeys.of(contactSObject.getField("Email").toString()).into(email())),
                Check.whether(contactSObject.getField("Phone") != null)
                        .andIfSo(SendKeys.of(contactSObject.getField("Phone").toString()).into(phone())),
                KeyboardShortcuts.save());
    }

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
