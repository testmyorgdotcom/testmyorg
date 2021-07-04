package org.testmy.screenplay.factory.question;

import java.util.Set;

import org.testmy.screenplay.question.data.ObjectsDescription;
import org.testmy.screenplay.question.data.OrgGlobalDescription;

import net.serenitybdd.core.steps.Instrumented;

public class Org {

    public static OrgGlobalDescription globalDescription() {
        return Instrumented.instanceOf(OrgGlobalDescription.class).newInstance();
    }

    public static ObjectsDescription objectsDescription(final Set<String> objectTypes) {
        return Instrumented.instanceOf(ObjectsDescription.class).withProperties(objectTypes);
    }

}
