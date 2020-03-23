package com.sap.cloud.lm.sl.cf.process.steps;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.cloudfoundry.client.lib.domain.CloudTask;
import org.cloudfoundry.client.lib.domain.ImmutableCloudMetadata;
import org.cloudfoundry.client.lib.domain.ImmutableCloudTask;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.client.lib.domain.ImmutableCloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.core.cf.clients.RecentLogsRetriever;
import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.process.variables.Variables;

@RunWith(Parameterized.class)
public class PollExecuteTaskStatusStepTest extends AsyncStepOperationTest<ExecuteTaskStep> {

    @Mock
    private RecentLogsRetriever recentLogsRetriever;

    private static final int START_TIMEOUT = 900;
    private static final String TASK_NAME = "foo";
    private static final String APPLICATION_NAME = "bar";

    private static final UUID TASK_UUID = UUID.randomUUID();
    private static final CloudApplicationExtended APPLICATION = ImmutableCloudApplicationExtended.builder()
                                                                                                 .name(APPLICATION_NAME)
                                                                                                 .build();

    @Parameters
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
// @formatter:off
            // (0)
            {
                CloudTask.State.SUCCEEDED, 100L, AsyncExecutionState.FINISHED,
            },
            // (1)
            {
                CloudTask.State.SUCCEEDED, TimeUnit.SECONDS.toMillis(START_TIMEOUT) + 1, AsyncExecutionState.FINISHED,
            },
            // (2)
            {
                CloudTask.State.FAILED, 100L, AsyncExecutionState.ERROR,
            },
            // (3)
            {
                CloudTask.State.FAILED, TimeUnit.SECONDS.toMillis(START_TIMEOUT) + 1, AsyncExecutionState.ERROR,
            },
            // (4)
            {
                CloudTask.State.PENDING, 100L, AsyncExecutionState.RUNNING,
            },
            // (5)
            {
                CloudTask.State.PENDING, TimeUnit.SECONDS.toMillis(START_TIMEOUT) + 1, AsyncExecutionState.RUNNING,
            },
            // (6)
            {
                CloudTask.State.RUNNING, 100L, AsyncExecutionState.RUNNING,
            },
            // (7)
            {
                CloudTask.State.RUNNING, TimeUnit.SECONDS.toMillis(START_TIMEOUT) + 1, AsyncExecutionState.RUNNING,
            },
            // (8)
            {
                CloudTask.State.CANCELING, 100L, AsyncExecutionState.RUNNING,
            },
            // (9)
            {
                CloudTask.State.CANCELING, TimeUnit.SECONDS.toMillis(START_TIMEOUT) + 1, AsyncExecutionState.RUNNING,
            },
// @formatter:on
        });
    }

    private final CloudTask.State currentTaskState;
    private final long currentTime;
    private final AsyncExecutionState expectedExecutionStatus;

    private final CloudTask task = ImmutableCloudTask.builder()
                                                     .metadata(ImmutableCloudMetadata.builder()
                                                                                     .guid(TASK_UUID)
                                                                                     .build())
                                                     .name(TASK_NAME)
                                                     .build();

    public PollExecuteTaskStatusStepTest(CloudTask.State currentTaskState, long currentTime, AsyncExecutionState expectedExecutionStatus) {
        this.currentTaskState = currentTaskState;
        this.currentTime = currentTime;
        this.expectedExecutionStatus = expectedExecutionStatus;
    }

    @Before
    public void setUp() {
        step.currentTimeSupplier = () -> currentTime;
        prepareContext();
        prepareClientExtensions();
        when(recentLogsRetriever.getRecentLogsSafely(any(), any(), any())).thenReturn(Collections.emptyList());
    }

    private void prepareContext() {
        context.setVariable(Variables.STARTED_TASK, task);
        execution.setVariable(Constants.VAR_TASKS_INDEX, 0);
        execution.setVariable(Constants.VAR_START_TIME, 0L);
        context.setVariable(Variables.START_TIMEOUT, START_TIMEOUT);
        StepsTestUtil.mockApplicationsToDeploy(Collections.singletonList(APPLICATION), execution);
    }

    private void prepareClientExtensions() {
        CloudTask taskWithState = ImmutableCloudTask.builder()
                                                    .from(task)
                                                    .state(currentTaskState)
                                                    .build();
        when(client.getTask(TASK_UUID)).thenReturn(taskWithState);
    }

    @Override
    protected void validateOperationExecutionResult(AsyncExecutionState result) {
        assertEquals(expectedExecutionStatus.toString(), result.toString());
    }

    @Override
    protected ExecuteTaskStep createStep() {
        return new ExecuteTaskStep();
    }

    @Override
    protected List<AsyncExecution> getAsyncOperations(ProcessContext wrapper) {
        return step.getAsyncStepExecutions(wrapper);
    }

}
