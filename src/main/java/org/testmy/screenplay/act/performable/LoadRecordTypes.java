package org.testmy.screenplay.act.performable;

import java.util.List;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.RecordTypeIdProvider;
import org.testmy.screenplay.factory.question.SObjects;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Shared;

public class LoadRecordTypes implements Performable {
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;

    @Override
    public <T extends Actor> void performAs(final T actor) {
        final String allRecordTypesQuery = "SELECT Id, DeveloperName, SobjectType FROM RecordType";
        final List<SObject> recordTypes = actor.asksFor(SObjects.usingQuery(allRecordTypesQuery));
        recordTypeIdProvider.init(recordTypes);
    }
}
