package com.sap.cloud.lm.sl.cf.process;

import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String DEPLOY_SERVICE_ID = "xs2-deploy";
    public static final String BLUE_GREEN_DEPLOY_SERVICE_ID = "xs2-bg-deploy";
    public static final String UNDEPLOY_SERVICE_ID = "xs2-undeploy";
    public static final String CTS_DEPLOY_SERVICE_ID = "CTS_DEPLOY";
    public static final String DEPLOY_APP_SUB_PROCESS_ID = "deployAppSubProcess";
    public static final String EXECUTE_HOOK_TASKS_SUB_PROCESS_ID = "executeHookTasksSubProcess";
    public static final String SERVICE_VERSION_1_2 = "1.2";
    public static final String SERVICE_VERSION_1_1 = "1.1";
    public static final String SERVICE_VERSION_1_0 = "1.0";
    public static final int DEFAULT_START_TIMEOUT = (int) TimeUnit.HOURS.toSeconds(1);

    public static final String PARAM_APP_ARCHIVE_ID = "appArchiveId";
    public static final String PARAM_EXT_DESCRIPTOR_FILE_ID = "mtaExtDescriptorId";
    public static final String PARAM_NO_START = "noStart";
    public static final String PARAM_START_TIMEOUT = "startTimeout";
    public static final String PARAM_UPLOAD_TIMEOUT = "uploadTimeout";
    public static final String PARAM_USE_NAMESPACES = "useNamespaces";
    public static final String PARAM_USE_NAMESPACES_FOR_SERVICES = "useNamespacesForServices";
    public static final String PARAM_ALLOW_INVALID_ENV_NAMES = "allowInvalidEnvNames";
    public static final String PARAM_VERSION_RULE = "versionRule";
    public static final String PARAM_DELETE_SERVICES = "deleteServices";
    public static final String PARAM_DELETE_SERVICE_KEYS = "deleteServiceKeys";
    public static final String PARAM_DELETE_SERVICE_BROKERS = "deleteServiceBrokers";
    public static final String PARAM_FAIL_ON_CRASHED = "failOnCrashed";
    public static final String PARAM_MTA_ID = "mtaId";
    public static final String PARAM_KEEP_FILES = "keepFiles";
    public static final String PARAM_NO_CONFIRM = "noConfirm";
    public static final String PARAM_NO_RESTART_SUBSCRIBED_APPS = "noRestartSubscribedApps";
    public static final String PARAM_GIT_URI = "gitUri";
    public static final String PARAM_GIT_REF = "gitRef";
    public static final String PARAM_GIT_REPO_PATH = "gitRepoPath";
    public static final String PARAM_GIT_SKIP_SSL = "gitSkipSsl";
    public static final String PARAM_NO_FAIL_ON_MISSING_PERMISSIONS = "noFailOnMissingPermissions";
    public static final String PARAM_ABORT_ON_ERROR = "abortOnError";
    public static final String PARAM_CTS_PROCESS_ID = "ctsProcessId";
    public static final String PARAM_FILE_LIST = "fileList";
    public static final String PARAM_DEPLOY_URI = "deployUri";
    public static final String PARAM_USERNAME = "userId";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_APPLICATION_TYPE = "applType";
    public static final String PARAM_TRANSFER_TYPE = "transferType";
    public static final String PARAM_GIT_REPOSITORY_LIST = "gitRepositoryList";
    public static final String PARAM_SKIP_OWNERSHIP_VALIDATION = "skipOwnershipValidation";
    public static final String PARAM_MODULES_FOR_DEPLOYMENT = "modulesForDeployment";
    public static final String PARAM_RESOURCES_FOR_DEPLOYMENT = "resourcesForDeployment";
    public static final String PARAM_VERIFY_ARCHIVE_SIGNATURE = "verifyArchiveSignature";
    public static final String PARAM_KEEP_ORIGINAL_APP_NAMES_AFTER_DEPLOY = "keepOriginalAppNamesAfterDeploy";

    public static final String VAR_USER = "user";

    public static final String VAR_MTA_MANIFEST = "mtaManifest";
    public static final String VAR_MTA_DEPLOYMENT_DESCRIPTOR = "mtaDeploymentDescriptor";
    public static final String VAR_MTA_DEPLOYMENT_DESCRIPTOR_WITH_SYSTEM_PARAMETERS = "mtaDeploymentDescriptorWithSystemParameters";
    public static final String VAR_COMPLETE_MTA_DEPLOYMENT_DESCRIPTOR = "completeMtaDeploymentDescriptor";
    public static final String VAR_MTA_EXTENSION_DESCRIPTOR_CHAIN = "mtaExtensionDescriptorChain";
    public static final String VAR_MTA_MAJOR_SCHEMA_VERSION = "mtaMajorSchemaVersion";
    public static final String VAR_MTA_MODULE_CONTENT_PREFIX = "mtaModuleContent_";
    public static final String VAR_MTA_MODULE_FILE_NAME_PREFIX = "mtaModuleFileName_";
    public static final String VAR_MTA_REQUIRES_FILE_NAME_PREFIX = "mtaRequiresFileName_";
    public static final String VAR_MTA_RESOURCE_FILE_NAME_PREFIX = "mtaResourceFileName_";
    public static final String VAR_MTA_ARCHIVE_MODULES = "mtaArchiveModules";
    public static final String VAR_NEW_MTA_VERSION = "newMtaVersion";
    public static final String VAR_MTA_MODULES = "mtaModules";
    public static final String MODULE_NAME_TO_MODULE_TYPE_MAP = "moduleNameToModuleTypeMap";
    public static final String VAR_MTA_MODULES_TO_DEPLOY_ALWAYS = "mtaModulesToDeployAlways";

    public static final String VAR_APP_STATE_ACTIONS_TO_EXECUTE = "appStateActionsToExecute";
    public static final String VAR_DEPLOYED_MTA = "deployedMta";
    public static final String VAR_SYSTEM_PARAMETERS = "systemParameters";
    public static final String VAR_ORG = "org";
    public static final String VAR_ORG_ID = "orgId";
    public static final String VAR_SPACE = "space";
    public static final String VAR_CUSTOM_DOMAINS = "customDomains";
    public static final String VAR_DEPLOYED_APPS = "deployedApps";
    public static final String VAR_SERVICES_TO_CREATE = "servicesToCreate";
    public static final String VAR_SERVICE_TO_PROCESS = "serviceToProcess";
    public static final String VAR_SERVICE_TO_PROCESS_NAME = "serviceToProcessName";
    public static final String VAR_IS_SERVICE_UPDATED = "isServiceUpdated";
    public static final String VAR_IS_SERVICE_UPDATED_VAR_PREFIX = "IS_SERVICE_UPDATED_";
    public static final String VAR_APP_SERVICE_URL_VAR_PREFIX = "APP_SERVICE_URL_";
    public static final String VAR_APP_SERVICE_BROKER_VAR_PREFIX = "APP_SERVICE_BROKER_";
    public static final String VAR_SERVICE_ACTIONS_TO_EXCECUTE = "serviceActionsToExecute";
    public static final String VAR_SERVICES_TO_BIND = "servicesToBind";
    public static final String VAR_SERVICE_KEYS_TO_CREATE = "serviceKeysToCreate";
    public static final String VAR_SERVICE_KEYS_CREDENTIALS_TO_INJECT = "serviceKeysCredentialsToInject";
    public static final String VAR_MODULES_TO_DEPLOY_CLASSNAME = "modulesToDeployClassname";
    public static final String VAR_MODULES_TO_DEPLOY = "modulesToDeploy";
    public static final String VAR_ALL_MODULES_TO_DEPLOY = "allModulesToDeploy";
    public static final String VAR_APPS_TO_DEPLOY = "appsToDeploy";
    public static final String VAR_CONTENTS_TO_DEPLOY = "contentsToDeploy";
    public static final String VAR_DEPLOYMENT_MODE = "deploymentMode";
    public static final String VAR_ITERATED_MODULES_IN_PARALLEL = "iteratedModulesInParallel";
    public static final String VAR_MODULES_TO_ITERATE_IN_PARALLEL = "modulesToIterateInParallel";
    public static final String VAR_UPDATED_SUBSCRIBERS = "updatedSubscribers";
    public static final String VAR_SERVICE_OFFERING = "serviceOffering";
    public static final String VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS = "updatedServiceBrokerSubscribers";
    public static final String VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS_COUNT = "updatedServiceBrokerSubscribersCount";
    public static final String VAR_UPDATED_SERVICE_BROKER_SUBSCRIBERS_INDEX = "updatedServiceBrokerSubscribersIndex";
    public static final String VAR_MODULES_INDEX = "modulesIndex";
    public static final String VAR_MODULES_COUNT = "modulesCount";
    public static final String VAR_INDEX_VARIABLE_NAME = "indexVariableName";
    public static final String VAR_MTARS_COUNT = "mtarsCount";
    public static final String VAR_MTARS_INDEX = "mtarsIndex";
    public static final String VAR_TASKS_TO_EXECUTE = "tasksToExecute";
    public static final String VAR_TASKS_INDEX = "tasksIndex";
    public static final String VAR_TASKS_COUNT = "tasksCount";
    public static final String VAR_STARTED_TASK = "startedTask";
    public static final String VAR_APPS_TO_RESTART_COUNT = "appsToRestartCount";
    public static final String VAR_APPS_TO_UNDEPLOY = "appsToUndeploy";
    public static final String VAR_SERVICES_TO_DELETE = "servicesToDelete";
    public static final String VAR_SERVICES_DATA = "servicesData";
    public static final String VAR_CREATED_OR_UPDATED_SERVICE_BROKER = "createdOrUpdatedServiceBroker";
    public static final String VAR_SUBSCRIPTIONS_TO_CREATE = "subscriptionsToCreate";
    public static final String VAR_SUBSCRIPTIONS_TO_DELETE = "subscriptionsToDelete";
    public static final String VAR_CONFIGURATION_ENTRIES_TO_PUBLISH = "configurationEntriesToPublish";
    public static final String VAR_EXISTING_APP = "existingApp";
    public static final String VAR_START_TIME = "startTime";
    public static final String VAR_STARTING_INFO = "startingInfo";
    public static final String VAR_STARTING_INFO_CLASSNAME = "startingInfoClass";
    public static final String VAR_UPLOAD_TOKEN = "uploadToken";
    public static final String VAR_PUBLISHED_ENTRIES = "publishedEntries";
    public static final String VAR_DELETED_ENTRIES = "deletedEntries";
    public static final String VAR_HAS_APP_CHANGED = "hasAppChanged";
    public static final String VAR_HAS_APP_CONTENT_CHANGED = "hasAppContentChanged";
    public static final String VAR_VCAP_APP_PROPERTIES_CHANGED = "vcapAppPropertiesChanged";
    public static final String VAR_VCAP_SERVICES_PROPERTIES_CHANGED = "vcapServicesPropertiesChanged";
    public static final String VAR_USER_PROPERTIES_CHANGED = "vcapUserPropertiesChanged";
    public static final String VAR_TRIGGERED_SERVICE_OPERATIONS = "triggeredServiceOperations";
    public static final String VAR_CORRELATION_ID = "correlationId";
    public static final String VAR_MODULE_TO_DEPLOY = "moduleToDeploy";
    public static final String VAR_APP_TO_PROCESS = "appToProcess";
    public static final String VAR_SUBPROCESS_ID = "subProcessId";
    public static final String VAR_PARENTPROCESS_ID = "parentProcessId";
    public static final String VAR_SERVICES_TO_CREATE_COUNT = "servicesToCreateCount";
    public static final String VAR_SERVICES_TO_POLL = "servicesToPoll";
    public static final String VAR_ERROR_TYPE = "errorType";
    public static final String VAR_GIT_REPOSITORY_CONFIG_MAP = "gitRepositoryConfigMap";
    public static final String VAR_USE_IDLE_URIS = "useIdleUris";
    public static final String VAR_DELETE_IDLE_URIS = "deleteIdleUris";
    public static final String VAR_SKIP_UPDATE_CONFIGURATION_ENTRIES = "skipUpdateConfigurationEntries";
    public static final String VAR_SKIP_MANAGE_SERVICE_BROKER = "skipManageServiceBroker";
    public static final String PROCESS_ABORTED = "__PROCESS_ABORTED";
    public static final String TASK_ID = "__TASK_ID";
    public static final String EXECUTE_ONE_OFF_TASKS = "executeOneOffTasks";
    public static final String SHOULD_UPLOAD_APPLICATION_CONTENT = "shouldUploadApplicationContent";
    public static final String REBUILD_APP_ENV = "rebuildAppEnv";
    public static final String ADDITIONAL_APP_ENV = "additionalAppEnv";
    public static final String ASYNC_STEP_EXECUTION_INDEX = "asyncStepExecutionIndex";
    public static final String VAR_STEP_EXECUTION = "StepExecution";
    public static final String VAR_STEP_PHASE = "stepPhase";
    public static final String VAR_APP_CONTENT_CHANGED = "appContentChanged";
    public static final String VAR_STEP_START_TIME = "stepStartTime_";
    public static final String VAR_DEPLOYED_MTA_COLOR = "deployedMtaColor";
    public static final String VAR_LIVE_MTA_COLOR = "liveMtaColor";
    public static final String VAR_MTA_COLOR = "mtaColor";
    public static final String VAR_IDLE_MTA_COLOR = "idleMtaColor";
    public static final String VAR_BUILD_GUID = "buildGuid";
    public static final String VAR_MTA_ARCHIVE_ELEMENTS = "mtaArchiveElements";
    public static final String VAR_PHASE = "phase";
    public static final String VAR_EXECUTED_HOOKS_FOR_PREFIX = "executedHooksFor_";
    public static final String VAR_HOOKS_FOR_EXECUTION = "hooksForExecution";
    public static final String VAR_HOOK_FOR_EXECUTION = "hookForExecution";
    public static final String VAR_FILE_ENTRIES = "fileEntries";
    public static final String VAR_APPS_TO_RENAME = "appsToRename";

    public static final String TOOL_TYPE = "tool_type";
    public static final String FEEDBACK_MAIL = "feedback_form";
    public static final String SYMANTEC_CERTIFICATE_FILE = "/symantec.crt";
    public static final String CERTIFICATE_TYPE_X_509 = "X.509";

    protected Constants() {
    }
}
