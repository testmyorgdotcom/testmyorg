package org.testmy.config;

public interface Config {
    // System properties
    String PROPERTY_LOGIN_URL = "testmyorg.login.url";
    // System default properties
    String PROPERTY_DEFAULT_LOGIN_URL = "https://test.salesforce.com";

    // String patterns
    // urls
    String PATTERN_LOGIN_WITH_CREDENTIALS_IN_URL = "%s/login.jsp?un=%s&pw=%s";
    String PATTERN_LOGIN_WITH_FRONTDOOR_URL = "%s/secur/frontdoor.jsp?sid=%s";
}
