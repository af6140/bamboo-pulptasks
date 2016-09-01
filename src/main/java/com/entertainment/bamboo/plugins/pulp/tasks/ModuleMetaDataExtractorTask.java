package com.entertainment.bamboo.plugins.pulp.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.*;

import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.agent.bootstrap.AgentContext;
import com.atlassian.bamboo.v2.build.agent.remote.RemoteAgent;
import com.atlassian.bamboo.variable.VariableContext;
import com.atlassian.bamboo.v2.build.agent.remote.sender.BambooAgentMessageSender;
import com.atlassian.bamboo.variable.VariableDefinitionManager;
import com.atlassian.spring.container.ContainerManager;
import com.entertainment.bamboo.plugins.pulp.util.BambooVariableManager;
import com.entertainment.bamboo.plugins.pulp.model.bamboo.CreateOrUpdateVariableMessage;
import com.entertainment.bamboo.plugins.pulp.model.bamboo.Variable;
import com.entertainment.bamboo.plugins.pulp.model.puppet.Metadata;
import com.entertainment.bamboo.plugins.pulp.util.MetaDataParser;
import com.entertainment.bamboo.plugins.pulp.util.PuppetMetaVariableExtractor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;


import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by dwang on 8/31/16.
 */
public class ModuleMetaDataExtractorTask implements TaskType {
    // Stuff for creating Plan variables
    private PlanManager planManager;
    private VariableDefinitionManager variableDefinitionManager;
    private BambooAgentMessageSender bambooAgentMessageSender;
    private AgentContext agentContext;

    public void setAgentContext(AgentContext agentContext) {
        this.agentContext = agentContext;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }
    public void setBambooAgentMessageSender(BambooAgentMessageSender bambooAgentMessageSender) {
        this.bambooAgentMessageSender = bambooAgentMessageSender;
    }
    public void setVariableDefinitionManager( VariableDefinitionManager variableDefinitionManager) {
        this.variableDefinitionManager = variableDefinitionManager;
    }

    @NotNull
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        ConfigurationMap configurationMap= taskContext.getConfigurationMap();

        String metaJSONFile = configurationMap.get(ModuleMetaDataExtractorTaskConfigurator.METADATA_JSON);
        File metaDataFile = findMetaJSON(taskContext, metaJSONFile);
        try {
            Metadata metaData= getMetadataFromFile(metaDataFile, taskContext);
            PuppetMetaVariableExtractor extractor =new PuppetMetaVariableExtractor(configurationMap.get(ModuleMetaDataExtractorTaskConfigurator.PREFIX_OPTION_CUSTOM_VALUE));
            List<Variable> variables = extractor.extractVariables(metaData);
            saveOrUpdateVariables(variables, taskContext);
            return success(taskContext);
        }catch (IOException e) {
            buildLogger.addBuildLogEntry("IO Exception : " +e.getMessage());
            throw new TaskException(e.getMessage());
        }
    }


    public File findMetaJSON(TaskContext taskContext, String metaJSONFile) {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        File workDir = taskContext.getWorkingDirectory();

        buildLogger.addBuildLogEntry("Working directory: "+workDir.getAbsolutePath());
        if (metaJSONFile==null || StringUtils.isEmpty(metaJSONFile)) {
            // find metadata.json in path

            IOFileFilter ioFileFilter = new NameFileFilter("metadata.json");
            IOFileFilter dirFilter = DirectoryFileFilter.INSTANCE;
            Collection<File> files = FileUtils.listFiles(workDir, ioFileFilter, dirFilter);
            // return the first found
            if (files.isEmpty()) {
                throw new RuntimeException("No metadata.json found in " + workDir);
            }
            File metaDataFile = files.iterator().next();
            buildLogger.addBuildLogEntry("Reading " + metaDataFile.getAbsolutePath());
            return metaDataFile;
        }else {
            String path = workDir.getAbsolutePath() + File.separator + metaJSONFile;
            File metaDataFile = new File(path);
            buildLogger.addBuildLogEntry("Reading " + metaDataFile.getAbsolutePath());
            return metaDataFile;
        }

    }

    public Metadata getMetadataFromFile(File metaDataFile, TaskContext taskContext) throws IOException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        Metadata metaData =null;
        String content =FileUtils.readFileToString(metaDataFile);
        metaData = MetaDataParser.parseJsonString(content);
        return metaData;
    }

    public TaskResult success(TaskContext taskContext) {
        return TaskResultBuilder.newBuilder(taskContext).success().build();
    }

    public TaskResult failed(TaskContext taskContext) {
        return TaskResultBuilder.newBuilder(taskContext).failed().build();
    }

    private void saveOrUpdateVariables(List<Variable> variables, TaskContext taskContext) {
        String v_type = taskContext.getConfigurationMap().get(ModuleMetaDataExtractorTaskConfigurator.VARIABLE_TYPE);
        if (ModuleMetaDataExtractorTaskConfigurator.VARIABLE_TYPE_PLAN.equalsIgnoreCase(v_type)) {
            saveAsPlanVariables(variables, taskContext);
        } else {
            saveAsJobOrResultVariables(variables, taskContext);
        }
    }

    private void saveAsJobOrResultVariables(List<Variable> variables, TaskContext taskContext) {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        for (Variable variable : variables) {
            String name = variable.getName();
            String value = variable.getValue();

            final VariableContext variableContext = taskContext.getCommonContext().getVariableContext();
            String v_type = taskContext.getConfigurationMap().get(ModuleMetaDataExtractorTaskConfigurator.VARIABLE_TYPE);
            buildLogger.addBuildLogEntry("Variable type is :"+v_type);
            if(ModuleMetaDataExtractorTaskConfigurator.VARIABLE_TYPE_RESULT.equalsIgnoreCase(v_type)){
                variableContext.addResultVariable(name, value);
            }else if(ModuleMetaDataExtractorTaskConfigurator.VARIABLE_TYPE_JOB.equalsIgnoreCase(v_type)){
                variableContext.addLocalVariable(name, value);
            }else{
                throw new IllegalArgumentException("Unknown variable type '" + v_type + "'");
            }
        }
    }

    private void saveAsPlanVariables(List<Variable> variables,
                                     TaskContext taskContext) {

        BuildContext parentBuildContext = taskContext.getBuildContext().getParentBuildContext();
        String topLevelPlanKey = parentBuildContext.getPlanResultKey().getKey();
        String buildResultKey = taskContext.getBuildContext().getBuildResultKey();

        AgentContext agentContext = null;
        try {
            // In 5.10, RemoteAgent.getContext() started throwing an exception instead of returning null.
            // I'm not sure of an alternative way to determine if we are running in a remote agent so this
            // ugly hack exists.
            agentContext = RemoteAgent.getContext();
        }catch (IllegalStateException e){

        }
        if (agentContext != null) {
            // We're in a remote agent and we can't get access to managers
            // we want. Send something back home so they can do what we want
            // instead.
            if (bambooAgentMessageSender == null) {
                bambooAgentMessageSender = (BambooAgentMessageSender) ContainerManager
                        .getComponent("bambooAgentMessageSender");
            }
            bambooAgentMessageSender.send(new CreateOrUpdateVariableMessage(topLevelPlanKey, buildResultKey,
                    variables));
        } else {
            BambooVariableManager manager = new BambooVariableManager(planManager, variableDefinitionManager,
                    taskContext.getBuildLogger());
            manager.addOrUpdateVariables(topLevelPlanKey, variables);
        }
    }

}
