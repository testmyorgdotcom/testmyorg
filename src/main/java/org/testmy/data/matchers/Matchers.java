package org.testmy.data.matchers;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.testmy.data.query.SoqlComponent.MANDATORY_TYPE_COMPONENT;

import org.hamcrest.Matcher;

public class Matchers {
    public static HasField hasField(final String fieldName,
            final String fieldValue) {
        return new HasField(fieldName, fieldValue);
    }

    public static HasField hasField(final String fieldName,
            final Matcher<? extends Object> fieldValueMatcher) {
        return new HasField(fieldName, fieldValueMatcher);
    }

    public static HasField account() {
        return new HasField(MANDATORY_TYPE_COMPONENT, "Account");
    }

    public static HasField contact() {
        return new HasField(MANDATORY_TYPE_COMPONENT, "Contact");
    }

    public static HasField hasId() {
        return new HasField("Id", not(emptyOrNullString()));
    }

    public static HasField hasId(final String id) {
        return new HasField("Id", id);
    }

    public static HasField hasName(final String name) {
        return new HasField("Name", name);
    }

    public static HasFields ofShape(HasField... matchers) {
        return new HasFields(matchers);
    }
}
