package org.testmy.screenplay.act.performable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.RecordTypeIdProvider;
import org.testmy.screenplay.factory.question.SObjects;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class LoadRecordTypesTest {
    @Mock
    Actor actor;
    @Mock
    SObject singleRecordType;
    @Mock
    RecordTypeIdProvider recordTypeIdProvider;
    @InjectMocks
    LoadRecordTypes actionUnderTest;

    @Test
    public void initializesRecordTypeIdServiceWithRecordTypesLoadedViaSOQL() {
        final String query = "SELECT Id, DeveloperName, SobjectType FROM RecordType";
        when(actor.asksFor(SObjects.usingQuery(query))).thenReturn(Collections.singletonList(singleRecordType));

        actionUnderTest.performAs(actor);

        verify(recordTypeIdProvider).init(Collections.singletonList(singleRecordType));
    }
}
