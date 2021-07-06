package org.testmy.data.matchers;

import org.testmy.config.Config;

public class Matchers {
    public static HasField hasField(final String fieldName,
            final Object fieldValue) {
        return new HasField(fieldName, fieldValue);
    }

    public static HasField hasId(final String id) {
        return new HasField("Id", id);
    }

    public static HasField hasName(final String name) {
        return new HasField("Name", name);
    }

    public static HasField recordType(final String developerName) {
        return new HasField(Config.FIELD_RECORDTYPE_DEVELOPERNAME, developerName);
    }

    public static HasFields ofShape(HasField... matchers) {
        return new HasFields(matchers);
    }
}
