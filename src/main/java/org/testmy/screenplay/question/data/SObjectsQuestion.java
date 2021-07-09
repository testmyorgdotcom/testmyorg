package org.testmy.screenplay.question.data;

import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.SalesforceDataAction;
import org.testmy.screenplay.factory.question.Partner;

import lombok.Data;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

@Data
public class SObjectsQuestion implements Question<List<SObject>> {
    private String query;

    public SObjectsQuestion(final String query) {
        this.query = query;
    }

    @Override
    public List<SObject> answeredBy(final Actor actor) {
        final PartnerConnection connection = actor.asksFor(Partner.connection());
        return queryDataUsing(connection);
    }

    private List<SObject> queryDataUsing(final PartnerConnection partnerConnection) {
        final SalesforceDataAction sda = new SalesforceDataAction(partnerConnection);
        return sda.query(query);
    }
}
