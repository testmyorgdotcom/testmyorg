package org.testmy.data.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testmy.data.query.SoqlComponent.soqlComponent;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class SoqlComponentTest {
    @Test
    public void buildSelectPart() {
        assertThat(
                soqlComponent("field", "anything").getFieldToSelect(),
                equalTo("field"));
    }

    @Test
    public void buildFromPart() {
        assertThat(
                soqlComponent("type", "Any Object").getObjectToSelectFrom(),
                equalTo("Any Object"));
    }

    @Test
    public void buildWherePart() {
        assertThat(
                soqlComponent("field", "value").getWhereCriterion(),
                equalTo("field = 'value'"));
    }

    @Test
    public void buildWherePart_formatsDateFields() {
        final String fieldName = "CloseDate";
        final Calendar calendarForDate = Calendar.getInstance();
        calendarForDate.add(Calendar.DAY_OF_YEAR, 10);
        calendarForDate.set(Calendar.HOUR_OF_DAY, 10);
        final Date fieldValue = calendarForDate.getTime();
        final String formatedDateValue = new SimpleDateFormat("yyyy-MM-dd").format(fieldValue);
        assertThat(
                soqlComponent(fieldName, fieldValue).getWhereCriterion(),
                equalTo("CloseDate = " + formatedDateValue));
    }

    @Test
    public void buildWherePart_formatsDateLocalTimeZoneIsNonUTC() {
        final String fieldName = "CloseDate";
        final Calendar calendarForDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        calendarForDate.set(Calendar.YEAR, 2021);
        calendarForDate.set(Calendar.MONTH, Calendar.JUNE);
        calendarForDate.set(Calendar.DAY_OF_MONTH, 16);
        calendarForDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarForDate.set(Calendar.MINUTE, 0);
        calendarForDate.set(Calendar.SECOND, 0);
        calendarForDate.set(Calendar.MILLISECOND, 0);
        final Date fieldValue = calendarForDate.getTime();
        assertThat(
                "SOQL is using UTC so date in where clause should differ",
                soqlComponent(fieldName, fieldValue).getWhereCriterion(),
                equalTo("CloseDate = 2021-06-15"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfFromIsCalledOnNonTypeComponent() {
        soqlComponent("name", "value").getObjectToSelectFrom();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfWhereIsCalledOnTypeComponent() {
        soqlComponent("type", "Account").getWhereCriterion();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failIfSelectPartIsCalledOnTypeComponent() {
        soqlComponent("type", "Account").getFieldToSelect();
    }
}
