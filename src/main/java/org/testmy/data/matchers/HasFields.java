package org.testmy.data.matchers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.core.AllOf;
import org.testmy.data.query.SoqlBuilder;
import org.testmy.data.query.SoqlComponent;

public class HasFields extends AllOf<SObject> implements ConstructingMatcher {
    private final List<HasField> objectShape = new LinkedList<>();
    private final SoqlBuilder soqlBuilder;

    public HasFields(final HasField... matchers) {
        super(matchers);

        for (final HasField matcher : matchers) {
            objectShape.add(matcher);
        }
        soqlBuilder = new SoqlBuilder(
                objectShape.stream()
                        .map(hf -> hf.getSoqlComponent().get())
                        .collect(Collectors.toList())
                        .toArray(new SoqlComponent[0]));
    }

    @Override
    public void visitForUpdate(final SObject result) {

        for (final ConstructingMatcher cm : objectShape) {
            cm.visitForUpdate(result);
        }
    }

    public String toSoql() {
        return soqlBuilder.buildSoql();
    }
}
