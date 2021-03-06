package org.cloudfoundry.multiapps.controller.persistence.query;

import java.util.List;
import java.util.Map;

import org.cloudfoundry.multiapps.controller.persistence.model.CloudTarget;
import org.cloudfoundry.multiapps.controller.persistence.model.ConfigurationEntry;

public interface ConfigurationEntryQuery extends Query<ConfigurationEntry, ConfigurationEntryQuery> {

    ConfigurationEntryQuery id(Long id);

    ConfigurationEntryQuery providerNid(String providerNid);

    ConfigurationEntryQuery providerId(String providerId);

    ConfigurationEntryQuery providerNamespace(String providerNamespace, boolean ensureEmptyIfNull);

    ConfigurationEntryQuery target(CloudTarget targetOrg);

    ConfigurationEntryQuery requiredProperties(Map<String, Object> requiredProperties);

    ConfigurationEntryQuery spaceId(String spaceId);

    ConfigurationEntryQuery version(String version);

    ConfigurationEntryQuery visibilityTargets(List<CloudTarget> visibilityTargets);

    ConfigurationEntryQuery mtaId(String mtaId);

    int deleteAll(String spaceId);

}