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
import com.sap.cloud.lm.sl.cf.process.message.Messages;

@Component("prepareAppsDeploymentStep")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrepareAppsDeploymentStep extends AbstractXS2ProcessStep {

    @Override
    protected ExecutionStatus executeStepInternal(DelegateExecution context) {
        getStepLogger().logActivitiTask();

        getStepLogger().info(Messages.PREPARING_APPS_DEPLOYMENT);

        // Get the list of cloud applications from the context:
        List<CloudApplicationExtended> apps = StepsUtil.getAppsToDeploy(context);

        // Initialize the iteration over the applications list:
        context.setVariable(Constants.VAR_APPS_COUNT, apps.size());
        context.setVariable(Constants.VAR_APPS_INDEX, 0);
        context.setVariable(Constants.VAR_INDEX_VARIABLE_NAME, Constants.VAR_APPS_INDEX);

        context.setVariable(Constants.VAR_CONTROLLER_POLLING_INTERVAL, ConfigurationUtil.getControllerPollingInterval());
        context.setVariable(Constants.VAR_UPLOAD_APP_TIMEOUT, ConfigurationUtil.getUploadAppTimeout());
        context.setVariable(Constants.VAR_PLATFORM_TYPE, ConfigurationUtil.getPlatformType().toString());

        return ExecutionStatus.SUCCESS;
    }

}
