package org.testmy.screenplay.factory.question;

import org.testmy.screenplay.question.PartnerConnectionQuestion;

import net.serenitybdd.core.steps.Instrumented;

public class Partner {

    public static PartnerConnectionQuestion connection() {
        return Instrumented.instanceOf(PartnerConnectionQuestion.class).newInstance();
    }

}
