package org.testmy.data.matchers;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

import org.hamcrest.Matcher;
import org.testmy.config.Config;

public class Matchers {
    public static HasField hasField(final String fieldName,
            final Object fieldValue) {
        return new HasField(fieldName, fieldValue);
    }

    public static HasField hasField(final String fieldName,
            final Matcher<? extends Object> fieldValueMatcher) {
        return new HasField(fieldName, fieldValueMatcher);
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

    public static HasField hasRecordTypeName(final String recordTypeName) {
        return new HasField(Config.FIELD_RECORDTYPE_DEVELOPERNAME, recordTypeName);
    }

    public static HasFields ofShape(HasField... matchers) {
        return new HasFields(matchers);
    }
}
