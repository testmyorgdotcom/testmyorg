package org.testmy.screenplay.factory.performable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testmy.screenplay.act.performable.InitializeFramework;
import org.testmy.screenplay.act.performable.LoadDigitalExperienceSites;
import org.testmy.screenplay.act.performable.LoadRecordTypes;
import org.testmy.screenplay.act.performable.LoadReferenceAttributes;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Performable;

public class Initialize {
    public static InitializeFramework framework() {
        return Instrumented.instanceOf(InitializeFramework.class).newInstance();
    }

    public static LoadRecordTypes allRecordTypes() {
        return Instrumented.instanceOf(LoadRecordTypes.class).newInstance();
    }

    public static LoadReferenceAttributes referenceAttributesFor(final String... sObjectsVarArg) {
        final Set<String> sObjects = new HashSet<String>(Arrays.asList(sObjectsVarArg));
        return referenceAttributesFor(sObjects);
    }

    public static LoadReferenceAttributes referenceAttributesFor(final Set<String> sObjects) {
        return Instrumented.instanceOf(LoadReferenceAttributes.class).withProperties(sObjects);
    }

    public static Performable digitalExperienceSites() {
        return Instrumented.instanceOf(LoadDigitalExperienceSites.class).newInstance();
    }
}
