package com.entertainment.bamboo.plugins.pulp.model.puppet;

import com.google.gson.annotations.Expose;

import java.util.*;

/**
 * Created by dwang on 8/31/16.
 */
public class Metadata {
    private static String nullToEmpty(String s) {
        if(s == null)
            s = "";
        else
            s = s.trim();
        return s;
    }

    @Expose
    private ModuleName name;

    @Expose
    private String version;

    @Expose
    private String summary;

    @Expose
    private String author;

    @Expose
    private String description;

    @Expose
    private List<Dependency> dependencies;


    @Expose
    private Map<String, byte[]> checksums;

    @Expose
    private String source;

    @Expose
    private String project_page;

    @Expose
    private String license;

    /**
     * Creates an empty Metadata instance
     */
    public Metadata() {
    }

    /**
     * Copy values from the given instance and assign default values for
     * values that has not been filled in.
     *
     * @param src
     *            The instance to copy values from
     */
    public Metadata(Metadata src) {
        name = src.name;
        version = src.version;
        summary = nullToEmpty(src.summary);
        author = nullToEmpty(src.author);
        description = nullToEmpty(src.description);
        source = nullToEmpty(src.source);
        project_page = nullToEmpty(src.project_page);
        license = nullToEmpty(src.license);

        if(src.dependencies == null || src.dependencies.isEmpty())
            dependencies = Collections.emptyList();
        else
            dependencies = new ArrayList<Dependency>(src.dependencies);


        if(checksums == null || checksums.isEmpty())
            checksums = Collections.emptyMap();
        else
            checksums = new HashMap<String, byte[]>(src.checksums);
    }

    /**
     * The verbose name of the author of this module.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * A map with filename <-> checksum entries for each file in the module
     *
     * @return the checksums or an empty map if no checksums has been assigned
     */
    public Map<String, byte[]> getChecksums() {
        if(checksums == null)
            checksums = new HashMap<String, byte[]>();
        return checksums;
    }

    /**
     * The list of module dependencies.
     *
     * @return the dependencies or an empty list.
     */
    public List<Dependency> getDependencies() {
        if(dependencies == null)
            dependencies = new ArrayList<Dependency>();
        return dependencies;
    }

    /**
     * A description of the module.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * The license pertaining to this module.
     *
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * The qualified name of the module.
     *
     * @return the qualified name
     */
    public ModuleName getName() {
        return name;
    }

    /**
     * A URL that points to the project page for this module.
     *
     * @return the project_page
     */
    public String getProjectPage() {
        return project_page;
    }

    /**
     * A URL that points to the source for this module.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * A brief summary
     *
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }


    /**
     * The version of the module.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param author
     *            the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @param checksums
     *            the checksums to set
     */
    public void setChecksums(Map<String, byte[]> checksums) {
        this.checksums = checksums;
    }

    /**
     * @param dependencies
     *            the dependencies to set
     */
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param license
     *            the license to set
     */
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(ModuleName name) {
        this.name = name == null
                ? null
                : name.withSeparator('-');
    }

    /**
     * @param project_page
     *            the project_page to set
     */
    public void setProjectPage(String project_page) {
        this.project_page = project_page;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @param summary
     *            the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }


    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
}