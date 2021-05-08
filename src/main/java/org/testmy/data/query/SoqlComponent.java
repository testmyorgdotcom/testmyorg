package org.testmy.data.query;

import java.util.function.Supplier;

import lombok.Getter;

public class SoqlComponent {
    public final static String MANDATORY_TYPE_COMPONENT = "type";
    @Getter
    private String fieldName;
    private Supplier<String> fromSupplier = () -> {
        throw new UnsupportedOperationException("'FROM' is illegal for non-type Object Attributes");
    };
    private Supplier<String> whereSupplier = () -> {
        throw new UnsupportedOperationException(MANDATORY_TYPE_COMPONENT + " attribute cannot be used in WHERE Clause");
    };
    private Supplier<String> selectSupplier = () -> {
        throw new UnsupportedOperationException(MANDATORY_TYPE_COMPONENT + " cannot be selected");
    };

    public SoqlComponent(final String fieldName,
            final Object fieldValue) {
        this.fieldName = fieldName;

        if (!MANDATORY_TYPE_COMPONENT.equals(fieldName)) {
            whereSupplier = () -> fieldName + " = '" + fieldValue + "'";
            selectSupplier = () -> fieldName;
        }
        else {
            fromSupplier = () -> "" + fieldValue;
        }
    }

    public static SoqlComponent soqlComponent(final String fieldName,
            final Object fieldValue) {
        return new SoqlComponent(fieldName, fieldValue);
    }

    public String getWhereCriterion() {
        return whereSupplier.get();
    }

    public String getObjectToSelectFrom() {
        return fromSupplier.get();
    }

    public String getFieldToSelect() {
        return selectSupplier.get();
    }
}
