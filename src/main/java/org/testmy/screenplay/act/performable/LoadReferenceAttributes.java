package org.testmy.screenplay.act.performable;

import java.util.List;
import java.util.Set;

import com.sforce.soap.partner.DescribeSObjectResult;

import org.testmy.data.ReferenceAttributeTypeProvider;
import org.testmy.screenplay.factory.question.Org;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Shared;

public class LoadReferenceAttributes implements Performable {
    private Set<String> sObjects;
    @Shared
    private ReferenceAttributeTypeProvider referenceAttributeTypeProvider;

    public LoadReferenceAttributes(final Set<String> sfObjects) {
        this.sObjects = sfObjects;
    }

    @Override
    public <T extends Actor> void performAs(final T actor) {
        final List<DescribeSObjectResult> objectsDescription = actor.asksFor(Org.objectsDescription(sObjects));
        referenceAttributeTypeProvider.init(objectsDescription);
    }
}
