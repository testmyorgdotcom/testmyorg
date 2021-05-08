package org.testmy.data.matchers;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;

public interface ConstructingMatcher extends Matcher<SObject> {
    void visitForUpdate(SObject result);
}
