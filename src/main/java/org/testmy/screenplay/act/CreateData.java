package org.testmy.screenplay.act;

import java.util.function.Function;
import java.util.function.Supplier;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.data.TestDataManager;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import lombok.Setter;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Shared;

public class CreateData implements Task {
    @Setter
    @Shared
    private TestDataManager testDataManager;
    @Setter
    private Supplier<PartnerConnection> connectionSupplier;
    @Setter
    private Function<SObject[], SaveResult[]> storeFunction = so -> {
        SaveResult[] result = new SaveResult[0];

        try {
            result = connectionSupplier.get().create(so);
        }
        catch (final ConnectionException e) {
            e.printStackTrace();
            throw new IllegalStateException(e); // TODO: change to a dedicated exception
        }
        return result;
    };
    private ConstructingMatcher objectShape;

    protected CreateData(final ConstructingMatcher objectShape) {
        this.objectShape = objectShape;
    }

    public static CreateData record(ConstructingMatcher ofShape) {
        return Instrumented.instanceOf(CreateData.class).withProperties(ofShape);
    }

    protected CreateData withStoreFunction(final Function<SObject[], SaveResult[]> storeFunction) {
        this.storeFunction = storeFunction;
        return this;
    }

    protected CreateData withTestDataManager(final TestDataManager testDataManager) {
        this.testDataManager = testDataManager;
        return this;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        this.connectionSupplier = () -> CallPartnerSoapApi.as(actor).ensureConnection();
        testDataManager.ensureObject(objectShape, storeFunction);
    }
}
