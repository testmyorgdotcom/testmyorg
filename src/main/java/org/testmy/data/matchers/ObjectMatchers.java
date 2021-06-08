package org.testmy.data.matchers;

import static org.testmy.data.query.SoqlComponent.MANDATORY_TYPE_COMPONENT;

import org.testmy.config.Config;

public class ObjectMatchers implements Config {
    public static HasField account() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_ACCOUNT);
    }

    public static HasField caseObject() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_CASE);
    }

    public static HasField contact() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_CONTACT);
    }

    public static HasField event() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_EVENT);
    }

    public static HasField lead() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_LEAD);
    }

    public static HasField task() {
        return new HasField(MANDATORY_TYPE_COMPONENT, OBJECT_TASK);
    }
}
