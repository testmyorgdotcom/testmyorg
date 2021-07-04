package org.testmy.data;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.matchers.ConstructingMatcher;

import lombok.NoArgsConstructor;

//TODO: remove this class
@NoArgsConstructor
@Deprecated
public class MatcherToSOjbectConstructor {
    public SObject constructSObject(ConstructingMatcher ofShape) {
        final SObject result = new SObject("Type must be initialized 1st, but can be changed later");
        ofShape.visitForUpdate(result);
        return result;
    }
}
