package org.testmy.data.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.query.SoqlComponent.soqlComponent;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;

public class HasFieldTest {
    @Test
    public void matchByField() {
        final String fieldName = "FieldName";
        final String fieldValue = "FieldValue";
        final SObject sobject = new SObject();
        sobject.setField(fieldName, fieldValue);

        assertThat(sobject, hasField(fieldName, fieldValue));
    }

    @Test
    public void matchWithNullFieldValue() {
        final String fieldName = "FieldName";
        final String fieldValue = null;
        final SObject sobject = new SObject();
        sobject.setField(fieldName, fieldValue);

        assertThat(sobject, hasField(fieldName, fieldValue));
    }

    @Test
    public void noMatchIfFieldValuesDifferent() {
        final String fieldName = "FieldName";
        final String fieldValue = "FieldValue";
        final String fieldValueDifferent = "DifferentValue";
        final SObject sobject = new SObject();
        sobject.setField(fieldName, fieldValueDifferent);

        assertThat(sobject, not(hasField(fieldName, fieldValue)));
    }

    @Test
    public void noMatchIfFieldsDifferent() {
        final String fieldName = "FieldName";
        final String fieldNameDifferent = "DifferentFieldName";
        final String fieldValue = "FieldValue";
        final SObject sobject = new SObject();
        sobject.setField(fieldNameDifferent, fieldValue);

        assertThat(sobject, not(hasField(fieldName, fieldValue)));
    }

    @Test(expected = NullPointerException.class)
    public void exceptionWithNullFieldName() {
        final String fieldValue = "FieldValue";

        hasField(null, fieldValue);
    }

    @Test
    public void testVisitForUpdate() {
        final SObject testObject = new SObject();
        final String fieldName = "Name";
        final String fieldValue = "Value";

        final HasField fieldShape = hasField(fieldName, fieldValue);

        fieldShape.visitForUpdate(testObject);

        assertThat(testObject, is(fieldShape));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void visitForUpdateNotSupportedIfBasedOnMatcherConstructor() {
        final SObject testObject = new SObject();
        final String fieldName = "Name";
        final String fieldValue = "Value";

        final HasField fieldShape = hasField(fieldName, equalTo(fieldValue));

        fieldShape.visitForUpdate(testObject);
    }

    @Test
    public void constructSoqlComponent() {
        final String fieldName = "name", fieldValue = "value";
        final HasField fieldShape = hasField(fieldName, fieldValue);

        assertThat(
                fieldShape.getSoqlComponent().get().getFieldToSelect(),
                equalTo(soqlComponent(fieldName, fieldValue).getFieldToSelect()));
        assertThat(
                fieldShape.getSoqlComponent().get().getWhereCriterion(),
                equalTo(soqlComponent(fieldName, fieldValue).getWhereCriterion()));
    }
}
