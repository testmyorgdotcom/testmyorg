package org.testmy.screenplay.ability;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.PortalUrlProvider;
import org.testmy.data.RecordTypeIdProvider;
import org.testmy.data.RecordTypeIdProvider.RecordType;
import org.testmy.screenplay.question.data.SObjectsQuestion;

import lombok.Getter;
import lombok.Setter;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Shared;

public class InitializeFramework implements SalesforceAbility {
    AbilityProvider abilityProvider = AbilityProvider.getInstance();
    @Setter
    private Actor actor;
    @Getter@Setter
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;
    @Getter@Setter
    @Shared
    private PortalUrlProvider portalUrlProvider;

    public static InitializeFramework withMetadata() {
        return Instrumented.instanceOf(InitializeFramework.class).newInstance();
    }

    public void initialize(){
        loadRecordTypes();
        loadPortalInfo();
    }
    
    public void loadRecordTypes() {
        final String allRecordTypesQuery = "SELECT Id, DeveloperName, SobjectType FROM RecordType";
        final List<SObject> recordTypes = actor.asksFor(SObjectsQuestion.withQuery(allRecordTypesQuery));
        recordTypeIdProvider.init(groupRecordTypesByObjectType(recordTypes));
    }

    private Map<String, List<RecordType>> groupRecordTypesByObjectType(final List<SObject> recordTypes) {
        final Map<String, List<RecordType>> recordTypesByObjectType = recordTypes.stream()
                .collect(groupingBy(sObject -> sObject.getField("SobjectType").toString(), mapping(
                        sObject -> {
                            final RecordType recordType = new RecordType(sObject.getField("DeveloperName").toString(),
                                    sObject.getId());
                            return recordType;
                        }, toList())));
        return recordTypesByObjectType;
    }

    public void loadPortalInfo() {
        final String communityPortalQuery = "SELECT Site.URLpathPrefix, Site.MasterLabel, Domain.Domain"
                + " FROM DomainSite"
                + " WHERE Domain.HttpsOption = 'Community'"
                + " AND Site.Status = 'Active'";
        final List<SObject> portalInfos = actor.asksFor(SObjectsQuestion.withQuery(communityPortalQuery));
        portalUrlProvider.init(portalInfos);

    }
}
