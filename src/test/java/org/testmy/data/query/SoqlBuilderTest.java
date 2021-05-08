package org.testmy.data.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testmy.data.query.SoqlBuilder.soqlBuilder;
import static org.testmy.data.query.SoqlComponent.soqlComponent;

import org.junit.Test;

public class SoqlBuilderTest {
    @Test(expected = IllegalArgumentException.class)
    public void failIfMandatoryComponentIsMissing() {
        soqlBuilder(
                soqlComponent("nonType", "any"),
                soqlComponent("1", "2"));
    }

    @Test
    public void alwaysAddIdToSelectStatement() {
        assertThat(
                soqlBuilder(
                        soqlComponent("type", "Account")).buildSoql(),
                equalTo("SELECT Id FROM Account"));
    }

    @Test
    public void doNotAddIdTwoTimes() {
        assertThat(
                soqlBuilder(
                        soqlComponent("type", "Account"),
                        soqlComponent("Id", "123")).buildSoql(),
                equalTo("SELECT Id FROM Account WHERE Id = '123'"));
    }

    @Test
    public void addItemsToSelectAndWhereParts() {
        assertThat(
                soqlBuilder(
                        soqlComponent("type", "Account"),
                        soqlComponent("A", "1"),
                        soqlComponent("B", "2")).buildSoql(),
                equalTo("SELECT Id, A, B FROM Account WHERE A = '1' AND B = '2'"));
    }
}
