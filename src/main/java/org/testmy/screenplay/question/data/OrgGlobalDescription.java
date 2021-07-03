package org.testmy.screenplay.question.data;

import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;
import org.testmy.screenplay.factory.question.Partner;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class OrgGlobalDescription implements Question<DescribeGlobalResult> {

    @Override
    public DescribeGlobalResult answeredBy(final Actor actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
        try {
            final DescribeGlobalResult globalDescription = connection.describeGlobal();
            return globalDescription;
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
