package org.testmy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class URLHelperTest {
    final String urlUnderTest = "https://abc.xyz.force.com/lightning/r/Account/1234567890/view";

    @Test
    public void testExtractMainUrl() {
        assertThat(URLHelper.extractMainUrl(urlUnderTest), equalTo("https://abc.xyz.force.com"));
    }

    @Test
    public void testExtractDodmain() {
        assertThat(URLHelper.extractDomain(urlUnderTest), equalTo("abc.xyz.force.com"));
    }

    @Test
    public void testParseObjectType() {
        assertThat(URLHelper.parseObjectType(urlUnderTest), equalTo("Account"));
    }

    @Test
    public void testParseObjectId() {
        assertThat(URLHelper.parseObjectId(urlUnderTest), equalTo("1234567890"));
    }
}
