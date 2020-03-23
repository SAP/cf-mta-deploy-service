package com.sap.cloud.lm.sl.cf.process.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import org.cloudfoundry.client.lib.CloudControllerClient;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationLog.MessageType;
import org.cloudfoundry.client.lib.domain.ImmutableApplicationLog;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.client.lib.domain.ImmutableCloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.core.Constants;
import com.sap.cloud.lm.sl.cf.core.cf.CloudControllerClientProvider;
import com.sap.cloud.lm.sl.cf.core.cf.apps.ApplicationStateAction;
import com.sap.cloud.lm.sl.cf.core.cf.clients.RecentLogsRetriever;
import com.sap.cloud.lm.sl.cf.core.model.SupportedParameters;
import com.sap.cloud.lm.sl.cf.persistence.services.ProcessLogger;
import com.sap.cloud.lm.sl.cf.persistence.services.ProcessLoggerProvider;
import com.sap.cloud.lm.sl.cf.process.mock.MockDelegateExecution;
import com.sap.cloud.lm.sl.cf.process.util.StepLogger;
import com.sap.cloud.lm.sl.cf.process.variables.Variables;
import com.sap.cloud.lm.sl.common.util.JsonUtil;
import com.sap.cloud.lm.sl.common.util.MapUtil;

public class PollExecuteAppStatusExecutionTest {

    private static final String USER_NAME = "testUsername";
    private static final String APP_SOURCE = "APP";
    private static final String APPLICATION_GUID = "1";
    private static final String SOURCE_ID = "0";
    private static final Date LOG_TIMESTAMP = Date.from(new GregorianCalendar(2019, Calendar.AUGUST, 1).toInstant());
    private static final long PROCESS_START_TIME = new GregorianCalendar(2019, Calendar.JANUARY, 1).toInstant()
                                                                                                   .toEpochMilli();

    @Mock
    private RecentLogsRetriever recentLogsRetriever;
    @Mock
    private StepLogger stepLogger;
    @Mock
    private ProcessLoggerProvider processLoggerProvider;
    @Mock
    private CloudControllerClientProvider clientProvider;
    @Mock
    private CloudControllerClient client;

    private DelegateExecution execution;
    private ProcessContext context;
    private PollExecuteAppStatusExecution step;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        execution = MockDelegateExecution.createSpyInstance();
        context = new ProcessContext(execution, stepLogger, clientProvider);
        step = new PollExecuteAppStatusExecution(recentLogsRetriever);
    }

    public static Stream<Arguments> testStep() {
        return Stream.of(
        //@formatter:off
                        // (1) Application is in running state
                        Arguments.of(createAppLog( "testMessage",  MessageType.STDOUT, APP_SOURCE ),
                                     null, null, false, AsyncExecutionState.RUNNING),
                        // (2) Application finished execution and should be stopped
                        Arguments.of(createAppLog( "SUCCESS",  MessageType.STDOUT, APP_SOURCE ),
                                     null, null, true, AsyncExecutionState.FINISHED),
                        // (3) Application finished execution and should be left to run
                        Arguments.of(createAppLog( "SUCCESS",  MessageType.STDOUT, APP_SOURCE ),
                                     null, null, false, AsyncExecutionState.FINISHED),
                        // (4) Application with Custom success marker
                        Arguments.of(createAppLog( "SUCCESS",  MessageType.STDOUT, APP_SOURCE ),
                                     "executed", null, false, AsyncExecutionState.RUNNING),
                        // (5) Application in failed state
                        Arguments.of(createAppLog( "FAILURE",  MessageType.STDERR, APP_SOURCE ),
                                     null, null, false, AsyncExecutionState.ERROR),
                        // (6) Application in failed state and should be stopped
                        Arguments.of(createAppLog( "FAILURE",  MessageType.STDERR, APP_SOURCE ),
                                     null, null, true, AsyncExecutionState.ERROR),
                        // (7) Application with Custom failure marker
                        Arguments.of(createAppLog( "FAILURE",  MessageType.STDERR, APP_SOURCE ),
                                     null, "execution failure", false, AsyncExecutionState.RUNNING),
                        // (8) Log message with non APP Source
                        Arguments.of(createAppLog( "info service",  MessageType.STDOUT, "service" ),
                                     null, null, false, AsyncExecutionState.RUNNING)
            );
        //@formatter:on
    }

    private static ApplicationLog createAppLog(String message, MessageType messageType, String sourceName) {
        return ImmutableApplicationLog.builder()
                                      .applicationGuid(PollExecuteAppStatusExecutionTest.APPLICATION_GUID)
                                      .message(message)
                                      .timestamp(PollExecuteAppStatusExecutionTest.LOG_TIMESTAMP)
                                      .messageType(messageType)
                                      .sourceId(PollExecuteAppStatusExecutionTest.SOURCE_ID)
                                      .sourceName(sourceName)
                                      .build();
    }

    @ParameterizedTest
    @MethodSource
    public void testStep(ApplicationLog applicationLog, String successMarker, String failureMarker, boolean shouldStopApp,
                         AsyncExecutionState expectedExecutionState) {
        CloudApplicationExtended application = buildApplication(successMarker, failureMarker, shouldStopApp);
        prepareContext(application);
        prepareRecentLogsRetriever(applicationLog);
        prepareStepLogger();
        prepareClientProvider();

        AsyncExecutionState resultState = step.execute(context);

        assertEquals(expectedExecutionState, resultState);

        if (shouldStopApp) {
            verify(client).stopApplication(application.getName());
            return;
        }
        verify(client, never()).stopApplication(application.getName());
    }

    private CloudApplicationExtended buildApplication(String successMarker, String failureMarker, boolean shouldStopApp) {
        Map<String, Object> deployAttributes = new HashMap<>();
        deployAttributes.put(SupportedParameters.STOP_APP, shouldStopApp);
        if (successMarker != null) {
            deployAttributes.put(SupportedParameters.SUCCESS_MARKER, successMarker);
        }
        if (failureMarker != null) {
            deployAttributes.put(SupportedParameters.FAILURE_MARKER, failureMarker);
        }

        Map<String, String> applicationEnv = MapUtil.asMap(Constants.ENV_DEPLOY_ATTRIBUTES, JsonUtil.toJson(deployAttributes));
        return ImmutableCloudApplicationExtended.builder()
                                                .name("test-app")
                                                .env(applicationEnv)
                                                .build();
    }

    private void prepareContext(CloudApplicationExtended application) {
        context.setVariable(Variables.APP_TO_PROCESS, application);
        StepsUtil.setAppStateActionsToExecute(execution, new HashSet<>(Collections.singletonList(ApplicationStateAction.EXECUTE)));
        context.setVariable(Variables.USER, USER_NAME);
        execution.setVariable(com.sap.cloud.lm.sl.cf.process.Constants.VAR_START_TIME, PROCESS_START_TIME);
    }

    private void prepareRecentLogsRetriever(ApplicationLog applicationLog) {
        when(recentLogsRetriever.getRecentLogs(any(), any(), any())).thenReturn(Arrays.asList(applicationLog));
    }

    private void prepareStepLogger() {
        when(stepLogger.getProcessLoggerProvider()).thenReturn(processLoggerProvider);
        when(processLoggerProvider.getLogger(any(), anyString())).thenReturn(mock(ProcessLogger.class));
    }

    private void prepareClientProvider() {
        when(clientProvider.getControllerClient(any(), any())).thenReturn(client);
    }

    @Test
    public void testStepWithoutExecuteAction() {
        StepsUtil.setAppStateActionsToExecute(execution, Collections.emptySet());

        AsyncExecutionState resultState = step.execute(context);

        assertEquals(AsyncExecutionState.FINISHED, resultState);
    }
}
