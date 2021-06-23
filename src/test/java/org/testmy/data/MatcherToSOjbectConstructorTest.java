package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;
import org.testmy.config.Config;
import org.testmy.data.matchers.ConstructingMatcher;

public class MatcherToSOjbectConstructorTest {
    MatcherToSOjbectConstructor constructor = new MatcherToSOjbectConstructor();

    @Test(expected = IllegalArgumentException.class)
    public void faileIfTypeIsMissing() {
        final ConstructingMatcher ofShape = ofShape(hasId("003xyz..."), hasName("Test Client"));
        constructor.constructSObject(ofShape);
    }

    @Test
    public void populatesObjectAttributesBasedOnShape() {
        final String name = "Test Client";
        final String id = "003xyz...";
        final ConstructingMatcher ofShape = ofShape(
                account(),
                hasId(id),
                hasName(name));
        final SObject account = constructor.constructSObject(ofShape);
        assertThat(account.getType(), is(Config.OBJECT_ACCOUNT));
        assertThat(account.getField("Name"), is(name));
        assertThat(account.getId(), is(id));
    }
}
