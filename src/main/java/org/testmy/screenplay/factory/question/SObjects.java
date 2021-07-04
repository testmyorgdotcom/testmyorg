package org.testmy.screenplay.factory.question;

import org.testmy.screenplay.question.data.SObjectsQuestion;

import net.serenitybdd.core.steps.Instrumented;

public class SObjects {
    public static SObjectsQuestion usingQuery(String soqlQuery) {
        return Instrumented.instanceOf(SObjectsQuestion.class).withProperties(soqlQuery);
    }
}
