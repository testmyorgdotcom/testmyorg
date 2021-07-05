package org.testmy.data.matchers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.core.AllOf;
import org.testmy.data.query.SoqlBuilder;
import org.testmy.data.query.SoqlComponent;

public class HasFields extends AllOf<SObject> implements ConstructingMatcher {
    private final List<HasField> objectShapeComponents = new LinkedList<>();

    public HasFields(final HasField... matchers) {
        super(matchers);
        for (final HasField matcher : matchers) {
            objectShapeComponents.add(matcher);
        }
    }

    @Override
    public void visitForUpdate(final SObject result) {
        for (final ConstructingMatcher cm : objectShapeComponents) {
            cm.visitForUpdate(result);
        }
    }

    public String toSoql() {
        return new SoqlBuilder(
                objectShapeComponents.stream()
                        .map(hf -> hf.getSoqlComponent().get())
                        .collect(Collectors.toList())
                        .toArray(new SoqlComponent[0]))
                                .buildSoql();
    }
}
