package com.sap.cloud.lm.sl.cf.process.steps;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sap.activiti.common.ExecutionStatus;
import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.core.util.ConfigurationUtil;
import com.sap.cloud.lm.sl.cf.process.Constants;

@Component("prepareToRestartServiceBrokersStep")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrepareToRestartServiceBrokersStep extends AbstractProcessStep {

    @Override
    protected ExecutionStatus executeStepInternal(DelegateExecution context) {
        getStepLogger().logActivitiTask();

        List<CloudApplicationExtended> serviceBrokersToRestart = StepsUtil.getServiceBrokerSubscribersToRestart(context);
        prepareServiceBrokersToRestart(context, serviceBrokersToRestart);
        return ExecutionStatus.SUCCESS;

    }

    private void prepareServiceBrokersToRestart(DelegateExecution context, List<CloudApplicationExtended> serviceBrokersToRestart) {
        context.setVariable(Constants.VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS_COUNT, serviceBrokersToRestart.size());
        context.setVariable(Constants.VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS_INDEX, 0);
        context.setVariable(Constants.VAR_INDEX_VARIABLE_NAME, Constants.VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS_INDEX);
        context.setVariable(Constants.VAR_CONTROLLER_POLLING_INTERVAL, ConfigurationUtil.getControllerPollingInterval());
    }

}
