package org.testmy.screenplay.act;

import com.sforce.soap.partner.PartnerConnection;

import org.testmy.data.TestDataManager;
import org.testmy.data.action.Clean;
import org.testmy.screenplay.factory.question.Partner;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Shared;

public class CleanData implements Performable {
    @Shared
    private TestDataManager testDataManager;

    public static Performable afterTest() {
        return new CleanData();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
        testDataManager.cleanData(new Clean(connection));
    }
}
