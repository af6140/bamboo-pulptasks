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
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Created by dwang on 8/31/16.
 */
@Component
public class AbstractPulpTaskConfigurator extends AbstractTaskConfigurator {

    public static final String PULPTASK_PULPURL="pulpURL";
    public static final String PULPTASK_REPOSITORY="pulpRepo";
    public static final String PULPTASK_USER="pulpUser";
    public static final String PULPTASK_PASSWORD="pulpPASSWORD";

    private static final Set<String> FIELDS = ImmutableSet.of(
            PULPTASK_PULPURL,
            PULPTASK_REPOSITORY,
            PULPTASK_USER,
            PULPTASK_PASSWORD
    );

    private TaskConfiguratorHelper taskConfiguratorHelper=null;

    @Autowired
    public AbstractPulpTaskConfigurator(@ComponentImport final TaskConfiguratorHelper taskConfiguratorHelper) {
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

        final String pulpURL = params.getString(PULPTASK_PULPURL);
        if (StringUtils.isEmpty(pulpURL))
        {
            errorCollection.addError(PULPTASK_PULPURL, "URL for pulp server is needed");
        }
        if (!UrlValidator.getInstance().isValid(pulpURL)) {
            errorCollection.addError(PULPTASK_PULPURL, "Invalid URL");
        }

    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        //context.put("say", "Hello, World!");
    }
    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);

    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);
    }
}
