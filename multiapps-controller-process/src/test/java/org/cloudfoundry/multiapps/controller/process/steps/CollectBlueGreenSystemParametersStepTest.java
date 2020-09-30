package org.cloudfoundry.multiapps.controller.process.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.cloudfoundry.multiapps.controller.core.helpers.SystemParameters;
import org.cloudfoundry.multiapps.controller.core.model.SupportedParameters;
import org.cloudfoundry.multiapps.controller.process.variables.Variables;
import org.cloudfoundry.multiapps.mta.model.DeploymentDescriptor;
import org.cloudfoundry.multiapps.mta.model.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectBlueGreenSystemParametersStepTest extends CollectSystemParametersStepBaseTest {

    @BeforeEach
    public void setNoConfirm() {
        context.setVariable(Variables.NO_CONFIRM, false);
    }

    @Test
    void testIdleGeneralParameters() {
        prepareDescriptor("system-parameters/mtad.yaml");
        prepareClient();

        step.execute(execution);

        DeploymentDescriptor descriptor = context.getVariable(Variables.DEPLOYMENT_DESCRIPTOR_WITH_SYSTEM_PARAMETERS);
        Map<String, Object> generalParameters = descriptor.getParameters();
        assertEquals(DEFAULT_DOMAIN, generalParameters.get(SupportedParameters.DEFAULT_IDLE_DOMAIN));
        assertEquals(DEFAULT_DOMAIN, generalParameters.get(SupportedParameters.DEFAULT_DOMAIN));
    }

    @Test
    void testIdleHostBasedModuleParameters() {
        prepareDescriptor("system-parameters/mtad.yaml");
        prepareClient();

        step.execute(execution);

        DeploymentDescriptor descriptor = context.getVariable(Variables.DEPLOYMENT_DESCRIPTOR_WITH_SYSTEM_PARAMETERS);
        List<Module> modules = descriptor.getModules();
        validateGlobalHostParameters(descriptor.getParameters());
        assertEquals(2, modules.size());
        for (Module module : modules) {
            validateIdleHostBasedModuleParameters(module);
        }
    }

    private void validateGlobalHostParameters(Map<String, Object> parameters) {
        assertEquals(DEFAULT_DOMAIN, parameters.get(SupportedParameters.DEFAULT_IDLE_DOMAIN));
    }

    private void validateIdleHostBasedModuleParameters(Module module) {
        Map<String, Object> parameters = module.getParameters();
        String expectedIdleHost = String.format("%s-%s-%s-idle", ORGANIZATION_NAME, SPACE_NAME, module.getName());
        assertEquals("${default-idle-domain}", parameters.get(SupportedParameters.IDLE_DOMAIN));
        assertEquals(expectedIdleHost, parameters.get(SupportedParameters.DEFAULT_IDLE_HOST));
        assertEquals("${default-idle-host}", parameters.get(SupportedParameters.IDLE_HOST));
        assertEquals(expectedIdleHost, parameters.get(SupportedParameters.DEFAULT_HOST));
        assertEquals("${default-host}", parameters.get(SupportedParameters.HOST));
        assertEquals(SystemParameters.DEFAULT_HOST_BASED_IDLE_URI, parameters.get(SupportedParameters.DEFAULT_IDLE_URI));
        assertEquals(SystemParameters.DEFAULT_HOST_BASED_IDLE_URI, parameters.get(SupportedParameters.DEFAULT_URI));
        assertEquals(SystemParameters.DEFAULT_IDLE_URL, parameters.get(SupportedParameters.DEFAULT_IDLE_URL));
        assertEquals(SystemParameters.DEFAULT_IDLE_URL, parameters.get(SupportedParameters.DEFAULT_URL));
    }

    @Override
    protected CollectBlueGreenSystemParametersStep createStep() {
        return new CollectBlueGreenSystemParametersStep();
    }

}
