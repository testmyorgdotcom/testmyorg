package org.testmy.data.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testmy.data.query.SoqlComponent.soqlComponent;

import org.junit.Test;

public class SoqlComponentTest {
    @Test
    public void buildSelectPart() {
        assertThat(
                soqlComponent("field", "anything").getFieldToSelect(),
                equalTo("field"));
    }

    @Test
    public void buildFromPart() {
        assertThat(
                soqlComponent("type", "Any Object").getObjectToSelectFrom(),
                equalTo("Any Object"));
    }

    @Test
    public void buildWherePart() {
        assertThat(
                soqlComponent("field", "value").getWhereCriterion(),
                equalTo("field = 'value'"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfFromIsCalledOnNonTypeComponent() {
        soqlComponent("name", "value").getObjectToSelectFrom();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfWhereIsCalledOnTypeComponent() {
        soqlComponent("type", "Account").getWhereCriterion();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfSelectPartIsCalledOnTypeComponent() {
        soqlComponent("type", "Account").getFieldToSelect();
    }
}
