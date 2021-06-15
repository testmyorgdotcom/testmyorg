package org.testmy.screenplay.ability;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.RecordTypeIdProvider;
import org.testmy.data.RecordTypeIdProvider.RecordType;
import org.testmy.data.SalesforceDataAction;

import lombok.Setter;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Shared;

public class InitializeFramework implements SalesforceAbility {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();
    @Setter
    private Actor actor;
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;

    public static InitializeFramework withMetadata() {
        return Instrumented.instanceOf(InitializeFramework.class).newInstance();
    }

    public void loadRecordTypes() {
        final CallPartnerSoapApi partnerAbility = abilityProvider.as(actor, CallPartnerSoapApi.class);
        final SalesforceDataAction queryRecordTypes = new SalesforceDataAction(partnerAbility.ensureConnection());
        loadRecordTypes(queryRecordTypes);
    }

    void loadRecordTypes(final SalesforceDataAction queryAction) {
        final String allRecordTypesQuery = "SELECT Id, DeveloperName, SobjectType FROM RecordType";
        final List<SObject> recordTypes = queryAction.queryRecords(allRecordTypesQuery);
        recordTypeIdProvider.init(groupByObjectType(recordTypes));
    }

    private Map<String, List<RecordType>> groupByObjectType(final List<SObject> recordTypes) {
        final Map<String, List<RecordType>> recordTypesByObjectType = recordTypes.stream()
                .collect(groupingBy(sObject -> sObject.getField("SobjectType").toString(), mapping(
                        sObject -> {
                            final RecordType recordType = new RecordType(sObject.getField("DeveloperName").toString(),
                                    sObject.getId());
                            return recordType;
                        }, toList())));
        return recordTypesByObjectType;
    }
}
