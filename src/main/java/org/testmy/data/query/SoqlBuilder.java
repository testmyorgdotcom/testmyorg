package org.testmy.data.query;

import static org.testmy.data.query.SoqlComponent.MANDATORY_TYPE_COMPONENT;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SoqlBuilder {
    private final static String ID_COMPONENT = "Id";
    private final List<SoqlComponent> soqlComponents = new LinkedList<>();
    private boolean hasIdComponent = false;

    public SoqlBuilder(final SoqlComponent... soqlComponents) {
        boolean mandatoryComponentFound = false;

        for (final SoqlComponent sc : soqlComponents) {
            this.soqlComponents.add(sc);
            mandatoryComponentFound = mandatoryComponentFound || MANDATORY_TYPE_COMPONENT.equals(sc.getFieldName());
            hasIdComponent = hasIdComponent || ID_COMPONENT.equals(sc.getFieldName());
        }

        if (!mandatoryComponentFound) {
            this.soqlComponents.clear();
            throw new IllegalArgumentException("mandatory component was not passed: " + MANDATORY_TYPE_COMPONENT);
        }
    }

    public static SoqlBuilder soqlBuilder(final SoqlComponent... soqlComponents) {
        return new SoqlBuilder(soqlComponents);
    }

    public String buildSoql() {
        final StringBuilder sb = new StringBuilder("SELECT ");
        final String whereCondition = buildWhereCondition();
        sb
                .append(buildSelectPart())
                .append(" FROM ")
                .append(buildFromPart());

        if (!whereCondition.isEmpty()) {
            sb
                    .append(" WHERE ")
                    .append(buildWhereCondition());
        }
        return sb.toString();
    }

    String buildWhereCondition() {
        return String.join(
                " AND ",
                soqlComponents
                        .stream()
                        .filter(sc -> !MANDATORY_TYPE_COMPONENT.equalsIgnoreCase(sc.getFieldName()))
                        .map(sc -> sc.getWhereCriterion())
                        .collect(Collectors.toList()));
    }

    String buildSelectPart() {
        final String partOfSelectedFields = String.join(
                ", ",
                soqlComponents
                        .stream()
                        .filter(sc -> !MANDATORY_TYPE_COMPONENT.equalsIgnoreCase(sc.getFieldName()))
                        .map(sc -> sc.getFieldToSelect())
                        .collect(Collectors.toList()));
        return partOfSelectedFields.isEmpty()
                ? ID_COMPONENT
                : hasIdComponent
                        ? partOfSelectedFields
                        : ID_COMPONENT + ", " + partOfSelectedFields;
    }

    public String buildFromPart() {

        for (final SoqlComponent soqlComponent : soqlComponents) {

            if (MANDATORY_TYPE_COMPONENT.equals(soqlComponent.getFieldName())) {
                return soqlComponent.getObjectToSelectFrom();
            }
        }
        throw new IllegalStateException("Cannot build 'FROM' part without " + MANDATORY_TYPE_COMPONENT + " Attribute");
    }
}
