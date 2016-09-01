package com.entertainment.bamboo.plugins.pulp.model.puppet;
import com.google.gson.annotations.Expose;
/**
 * Created by dwang on 8/31/16.
 */
public class Dependency {

    @Expose
    private ModuleName name;

    @Expose
    private String repository;

    @Expose
    private String version_requirement;

    @Override
    public boolean equals(Object other) {
        if(this == other)
            return true;
        if(!(other instanceof Dependency))
            return false;
        Dependency d = (Dependency) other;
        return safeEquals(name, d.name) && safeEquals(version_requirement, d.version_requirement);
    }

    /**
     * @return the name
     */
    public ModuleName getName() {
        return name;
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @return the version requirement
     */
    public String getVersionRequirement() {
        return version_requirement;
    }


    /**
     * @param name
     *            the name to set
     */
    public void setName(ModuleName name) {
        this.name = name == null
                ? null
                : name.withSeparator('/');
    }

    /**
     * @param repository
     *            the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * @param version_requirement
     *            the version requirement to set
     */
    public void setVersionRequirement(String version_requirement) {
        this.version_requirement = version_requirement;
    }
    protected static boolean safeEquals(Object a, Object b) {
        return a == b || a != null && b != null && a.equals(b);
    }
}