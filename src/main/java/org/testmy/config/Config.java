package org.testmy.config;

public interface Config {
    // System properties
    String PROPERTY_URL_LOGIN = "testmyorg.sf.login.url";
    String PROPERTY_VERSION_API_SOAP_PARTNER = "testmyorg.sf.api.partner.version";
    // System default properties
    String PROPERTY_DEFAULT_URL_LOGIN = "https://test.salesforce.com";
    String PROPERTY_DEFAULT_VERSION_API_SOAP_PARTNER = "51";

    // String patterns

    // urls
    // https://login.salesforce.com/login.jsp?un=abc.xyz@test.org&pw=password
    String PATTERN_URL_LOGIN_WITH_CREDENTIALS = "%s/login.jsp?un=%s&pw=%s";
    // https://login.salesforce.com/secur/frontdoor.jsp?sid=xyz123abc
    String PATTERN_URL_LOGIN_VIA_FRONTDOOR = "%s/secur/frontdoor.jsp?sid=%s";
    // https://login.salesforce.com/services/Soap/u/51
    String PATTERN_URL_PARTNER_SOAP_API = "%s/services/Soap/u/%s";

    // messages
    String PATTERN_MESSAGE_ERROR_CONSTRUCT_ATTRIBUTE_MISSING = "Object Shape is without mandatory %s attribute: %s";

    // Global Actions
    String GLOBAL_ACTION_NEW_CASE = "New Case";
    String GLOBAL_ACTION_NEW_CONTACT = "New Contact";
    String GLOBAL_ACTION_NEW_EVENT = "New Event";
    String GLOBAL_ACTION_NEW_LEAD = "New Lead";
    String GLOBAL_ACTION_NEW_TASK = "New Task";

    // Object Types
    String OBJECT_ACCOUNT = "Account";
    String OBJECT_CASE = "Case";
    String OBJECT_CONTACT = "Contact";
    String OBJECT_EVENT = "Event";
    String OBJECT_LEAD = "Lead";
    String OBJECT_TASK = "Task";
}
