package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

import org.junit.Test;
import org.testmy.error.TestRuntimeException;

public class ReferenceAttributeTypeProviderTest {
    private ReferenceAttributeTypeProvider dataProvider = new ReferenceAttributeTypeProvider();

    @Test
    public void getTypeFor_usesCacheToResolveTypes() {
        final String sObjectType = "Contact";
        final String sObjectReferenceAttribute = "CustomReference__r";
        final String referenceAttributeType = "Account";

        initProviderWithData(sObjectType, sObjectReferenceAttribute, referenceAttributeType);

        assertThat(dataProvider.getTypeFor(sObjectType, sObjectReferenceAttribute), is(referenceAttributeType));
    }

    @Test(expected = TestRuntimeException.class)
    public void getTypeFor_throwsExceptionIfNoDataForObjectType() {
        dataProvider.getTypeFor("Missing Object Type", "Any");
    }

    @Test(expected = TestRuntimeException.class)
    public void getTypeFor_throwsExceptionIfNoDataForReferenceAttribute() {
        final String sObjectType = "Contact";
        final String sObjectReferenceAttribute = "CustomReference__r";
        final String referenceAttributeType = "Account";

        initProviderWithData(sObjectType, sObjectReferenceAttribute, referenceAttributeType);

        dataProvider.getTypeFor(sObjectType, "Missing Attribute");
    }

    private void initProviderWithData(final String sObjectType,
            final String sObjectReferenceAttribute,
            final String referenceAttributeType) {
        final Field field = mock(Field.class);
        final DescribeSObjectResult objectDescription = mock(DescribeSObjectResult.class);
        when(objectDescription.getName()).thenReturn(sObjectType);
        when(objectDescription.getFields()).thenReturn(new Field[]{field});
        when(field.getRelationshipName()).thenReturn(sObjectReferenceAttribute);
        when(field.getType()).thenReturn(FieldType.reference);
        when(field.getReferenceTo()).thenReturn(new String[]{referenceAttributeType});
        dataProvider.init(Collections.singletonList(objectDescription));
    }
}
