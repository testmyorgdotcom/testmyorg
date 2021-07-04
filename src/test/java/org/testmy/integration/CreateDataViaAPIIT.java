package org.testmy.integration;

import static net.serenitybdd.screenplay.GivenWhenThen.and;
import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.GivenWhenThen.then;
import static net.serenitybdd.screenplay.GivenWhenThen.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;
import static org.testmy.data.matchers.ObjectMatchers.contact;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testmy.data.matchers.HasFields;
import org.testmy.screenplay.act.CleanData;
import org.testmy.screenplay.act.CreateData;
import org.testmy.screenplay.factory.Login;
import org.testmy.screenplay.factory.ability.Authenticate;
import org.testmy.screenplay.factory.ability.Call;
import org.testmy.screenplay.factory.performable.Initialize;
import org.testmy.screenplay.question.QueryData;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;

@RunWith(SerenityRunner.class)
public class CreateDataViaAPIIT {
    Actor admin = Actor.named("Andim");

    @Before
    public void before() {
        givenThat(admin)
                .can(Authenticate.as("Admin"))
                .can(Call.partnerApi())
                .wasAbleTo(Login.viaAPI());
    }

    @After
    public void after() {
        admin.attemptsTo(CleanData.afterTest());
    }

    @Test
    public void createData_WithReferenceLookupAttributes() {
        final HasFields account = ofShape(
                account(),
                hasField("ExternalId__c", "123"),
                hasName("Test Account"));

        givenThat(admin).wasAbleTo(Initialize.referenceAttributesFor("Account", "Contact"));
        and(admin).wasAbleTo(CreateData.record(account));

        final HasFields contactWithReferenceAttribute = ofShape(
                contact(),
                hasField("LastName", "Test User"),
                hasField("Account.ExternalId__c", "123"));

        when(admin).attemptsTo(CreateData.record(contactWithReferenceAttribute));

        then(admin).should(seeThat(QueryData.similarTo(contactWithReferenceAttribute), is(notNullValue())));
    }
}
