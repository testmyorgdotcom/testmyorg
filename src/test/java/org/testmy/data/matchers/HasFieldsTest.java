package org.testmy.data.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.testmy.data.matchers.Matchers.account;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.query.SoqlBuilder.soqlBuilder;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;
import org.testmy.data.query.SoqlBuilder;

public class HasFieldsTest {
    @Test
    public void testVisitForUpdate() {
        final SObject testObject = new SObject();

        final HasFields objectShape = ofShape(
                hasField("fn1", "v1"),
                hasField("fn2", "v2"),
                hasField("fn3", "v3"),
                account());

        objectShape.visitForUpdate(testObject);

        assertThat(testObject, is(objectShape));
    }

    @Test
    public void buildSoql() {
        final HasField shapePart1 = hasField("fn1", "v1");
        final HasField shapePart2 = hasField("fn2", "v2");

        final SoqlBuilder soqlBuilder = soqlBuilder(
                shapePart1.getSoqlComponent().get(),
                shapePart2.getSoqlComponent().get(),
                account().getSoqlComponent().get());

        final HasFields objectShape = ofShape(
                shapePart1,
                shapePart2,
                account());

        assertThat(objectShape.toSoql(), equalTo(soqlBuilder.buildSoql()));
    }
}
