package org.testmy.screenplay.act;

import com.sforce.soap.partner.PartnerConnection;

import org.testmy.data.TestDataManager;
import org.testmy.data.action.Insert;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.screenplay.factory.question.Partner;

import lombok.NoArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Shared;

@NoArgsConstructor
public class CreateData implements Task {
    @Shared
    TestDataManager testDataManager;
    private ConstructingMatcher objectShape;

    protected CreateData(final ConstructingMatcher objectShape) {
        this.objectShape = objectShape;
    }

    public static CreateData record(ConstructingMatcher ofShape) {
        return Instrumented.instanceOf(CreateData.class).withProperties(ofShape);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
        testDataManager.ensureObject(objectShape, new Insert(connection));
    }
}
