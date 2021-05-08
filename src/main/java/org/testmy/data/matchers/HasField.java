package org.testmy.data.matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.testmy.data.query.SoqlComponent;

import lombok.Getter;

public class HasField extends TypeSafeMatcher<SObject> implements ConstructingMatcher {
    @Getter
    private String fieldName;
    private Matcher<? extends Object> fieldValueMatcher;
    @Getter
    private Supplier<SoqlComponent> soqlComponent = () -> {
        throw new UnsupportedOperationException("cannot build SOQL if based on constructor with matcher");
    };
    private Consumer<SObject> constructLogic = sObj -> {
        throw new UnsupportedOperationException("cannot update SObject if based on constructor with matcher");
    };

    public HasField(final String fieldName,
            final String fieldValue) {
        this(fieldName, is(equalTo(fieldValue)));
        constructLogic = sObj -> sObj.setField(fieldName, fieldValue);
        soqlComponent = () -> new SoqlComponent(fieldName, fieldValue);
    }

    public HasField(final String fieldName,
            final Matcher<? extends Object> fieldvalueMatcher) {
        Objects.requireNonNull(fieldName);
        this.fieldName = fieldName;
        this.fieldValueMatcher = fieldvalueMatcher;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("hasField(").appendValue(fieldName).appendText(")").appendText(": ")
                .appendText(fieldValueMatcher.toString());
    }

    @Override
    protected boolean matchesSafely(final SObject item) {
        final Object itemFieldValue = item.getField(fieldName);
        return fieldValueMatcher.matches(itemFieldValue);
    }

    @Override
    public void visitForUpdate(final SObject sObject) {
        constructLogic.accept(sObject);
    }
}
