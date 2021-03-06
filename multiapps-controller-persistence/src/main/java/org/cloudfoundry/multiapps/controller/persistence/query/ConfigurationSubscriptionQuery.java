package org.cloudfoundry.multiapps.controller.persistence.query;

import java.util.List;

import org.cloudfoundry.multiapps.controller.persistence.model.ConfigurationEntry;
import org.cloudfoundry.multiapps.controller.persistence.model.ConfigurationSubscription;

public interface ConfigurationSubscriptionQuery extends Query<ConfigurationSubscription, ConfigurationSubscriptionQuery> {

    ConfigurationSubscriptionQuery id(Long id);

    ConfigurationSubscriptionQuery mtaId(String mtaId);

    ConfigurationSubscriptionQuery spaceId(String spaceId);

    ConfigurationSubscriptionQuery appName(String appName);

    ConfigurationSubscriptionQuery resourceName(String resourceName);

    ConfigurationSubscriptionQuery onSelectMatching(List<ConfigurationEntry> entries);

    int deleteAll(String spaceId);

}