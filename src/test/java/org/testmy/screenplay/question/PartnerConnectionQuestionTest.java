package org.testmy.screenplay.question;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.sforce.soap.partner.PartnerConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class PartnerConnectionQuestionTest {
    @Mock
    Actor actor;
    @Mock
    AbilityProvider abilityProvider;
    @Mock
    CallPartnerSoapApi partnerApiAbility;
    @Mock
    PartnerConnection mockedConenction;
    @InjectMocks
    PartnerConnectionQuestion connectionQuestion;

    @Test
    public void usesCallPartnerApiAbilityToGetTheConnectio() {
        when(abilityProvider.as(actor, CallPartnerSoapApi.class)).thenReturn(partnerApiAbility);
        when(partnerApiAbility.ensureConnection()).thenReturn(mockedConenction);

        final PartnerConnection partnerConnection = connectionQuestion.answeredBy(actor);

        assertThat(partnerConnection, is(mockedConenction));
    }
}
