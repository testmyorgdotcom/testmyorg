package org.testmy.screenplay.act;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.testmy.data.TestDataManager;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Shared;

public class CleanData implements Performable {
    @Shared
    private TestDataManager testDataManager;

    public static Performable afterTest() {
        return new CleanData();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final PartnerConnection connection = CallPartnerSoapApi.as(actor).ensureConnection();
        final String[] ids = testDataManager.getData()
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toList())
                .toArray(new String[0]);

        try {
            System.out.println("Going to delete: " + Arrays.asList(ids));
            final DeleteResult[] deleteResults = connection.delete(ids);
            final List<DeleteResult> nonDeletedObjects = Arrays.asList(deleteResults).stream()
                    .filter(dr -> !dr.getSuccess())
                    .collect(Collectors.toList());

            if (!nonDeletedObjects.isEmpty()) {
                System.err.println("Could not delete: " + nonDeletedObjects); // TODO: move to loggers
            }
        }
        catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
