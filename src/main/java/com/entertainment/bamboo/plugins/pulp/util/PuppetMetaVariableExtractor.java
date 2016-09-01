package com.entertainment.bamboo.plugins.pulp.util;

import com.entertainment.bamboo.plugins.pulp.model.bamboo.Variable;
import com.entertainment.bamboo.plugins.pulp.model.puppet.Metadata;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwang on 9/1/16.
 */
public class PuppetMetaVariableExtractor {
    private static final String DEFAULT_VARIABLE_PREFIX="puppet.";
    private static final String PPMETA_OWNER= "owner";
    private static final String PPMETA_NAME="name";
    private static final String PPMETA_VERSION="version";

    private String customPrefix =null;
    public PuppetMetaVariableExtractor(String customPrefix) {
        this.customPrefix = customPrefix;
    }
    public List<Variable> extractVariables(Metadata metadata) {
        List<Variable> variables =new ArrayList<Variable>();
        String owner = metadata.getName().getOwner();
        String name = metadata.getName().getName();
        String version = metadata.getVersion();

        variables.add(new Variable(getFullVariableName("owner"), owner));
        variables.add(new Variable(getFullVariableName("name"), name));
        variables.add(new Variable(getFullVariableName("version"), version));
        return variables;
    }

    private  String getFullVariableName(String name) {
        String prefix = DEFAULT_VARIABLE_PREFIX;
        if (this.customPrefix!=null && !StringUtils.isEmpty(this.customPrefix.trim())) {
            prefix = this.customPrefix.trim();
        }
        if (!prefix.endsWith(".")) prefix = prefix+".";
        return prefix+name;
    }
}
