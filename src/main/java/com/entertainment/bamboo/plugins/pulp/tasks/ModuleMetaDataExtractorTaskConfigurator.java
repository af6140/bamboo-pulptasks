package com.entertainment.bamboo.plugins.pulp.tasks;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskConfiguratorHelper;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * Created by dwang on 8/31/16.
 */
public class ModuleMetaDataExtractorTaskConfigurator extends AbstractTaskConfigurator {

    public final static String METADATA_JSON="metaJSON";
    public final static String VARIABLE_TYPE="variableType";
    public static final String VARIABLE_TYPE_JOB = "0";
    public static final String VARIABLE_TYPE_PLAN = "1";
    public static final String VARIABLE_TYPE_RESULT = "2";
    public static final String PREFIX_OPTION = "prefixOption";
    public static final String PREFIX_OPTION_DEFAULT = "1";
    public static final String PREFIX_OPTION_CUSTOM = "0";
    public static final String PREFIX_OPTION_CUSTOM_VALUE = "customPrefix";


    private static final Set<String> FIELDS = ImmutableSet.of(
            METADATA_JSON,
            VARIABLE_TYPE,
            PREFIX_OPTION,
            PREFIX_OPTION_CUSTOM_VALUE
    );
    private TaskConfiguratorHelper taskConfiguratorHelper=null;

    @Autowired
    public ModuleMetaDataExtractorTaskConfigurator(@ComponentImport final TaskConfiguratorHelper taskConfiguratorHelper) {
        this.taskConfiguratorHelper = taskConfiguratorHelper;
    }


    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(config, params, FIELDS);
        return config;
    }

    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String metaJSON = params.getString(METADATA_JSON);
        if (!StringUtils.isEmpty(metaJSON))
        {
            //errorCollection.addError(PULPTASK_PULPURL, "URL for pulp server is needed");
        }

    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        context.put(PREFIX_OPTION, PREFIX_OPTION_DEFAULT);
        populateContextForAll(context);
    }
    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);
        populateContextForAll(context);

    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);
    }

    private void populateContextForAll(@NotNull final Map<String, Object> context) {
        Map<String, String> servers = Maps.newHashMap();

        Map<String, String> prefixOptions = Maps.newHashMap();
        prefixOptions.put(PREFIX_OPTION_DEFAULT, "puppet");
        prefixOptions.put(PREFIX_OPTION_CUSTOM,  "custom");
        context.put("prefixOptions", prefixOptions);

        Map<String, String> variableTypeOptions = Maps.newHashMap();
        variableTypeOptions.put(VARIABLE_TYPE_JOB,
                "Job");
        variableTypeOptions.put(VARIABLE_TYPE_RESULT,
                "Result");
        variableTypeOptions.put(VARIABLE_TYPE_PLAN,
                "Plan");
        context.put("variableTypeOptions", variableTypeOptions);
    }
}
