package org.testmy.data.matchers;

import java.util.Objects;
import java.util.function.Consumer;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.testmy.data.query.SoqlComponent;

import lombok.Getter;

public class HasField extends TypeSafeMatcher<SObject> implements ConstructingMatcher {
    @Getter
    private String fieldName;
    private Object fieldValue;
    @Getter
    private SoqlComponent soqlComponent;
    private Consumer<SObject> constructLogic;

    public HasField(final String fieldName,
            final Object fieldValue) {
        Objects.requireNonNull(fieldName);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        constructLogic = sObj -> sObj.setField(fieldName, fieldValue);
        soqlComponent = new SoqlComponent(fieldName, fieldValue);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("hasField(").appendValue(fieldName).appendText(")").appendText(": ")
                .appendText(fieldValue.toString());
    }

    @Override
    protected boolean matchesSafely(final SObject item) {
        final Object itemFieldValue = item.getField(fieldName);
        return Objects.equals(itemFieldValue, fieldValue);
    }

    @Override
    public void visitForUpdate(final SObject sObject) {
        constructLogic.accept(sObject);
    }
}
