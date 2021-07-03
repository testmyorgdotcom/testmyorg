package org.testmy.screenplay.question.data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.factory.question.Partner;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ObjectsDescription implements Question<List<DescribeSObjectResult>> {
    private String [] objectTypes;

    public ObjectsDescription(final Set<String> objectTypes) {
        this.objectTypes = objectTypes.toArray(new String[0]);
    }

    @Override
    public List<DescribeSObjectResult> answeredBy(final Actor actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
        try {
            final DescribeSObjectResult[] result = connection.describeSObjects(objectTypes);
            return Arrays.asList(result);
        } catch (final ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
