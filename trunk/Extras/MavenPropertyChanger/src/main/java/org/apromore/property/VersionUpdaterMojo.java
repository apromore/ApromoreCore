package org.apromore.property;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Very specific maven plugin to update the values in a property file
 * to the next value, if it's a date then now and if it is a version number just +1
 * to the build number.
 *
 * @author Cameron James
 */
@Mojo(name = "update", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
@Execute(phase = LifecyclePhase.PREPARE_PACKAGE, goal = "update")
public class VersionUpdaterMojo extends AbstractMojo {

    private static final String INVALID_MISSING_FILE_MESSAGE = "<propertyFile> can not be empty!";
    private static final String INVALID_INCREMENT_VALUE = "<increment> must be a positive integer value!";
    private static final String INVALID_DATE_FORMAT_VALUE = "<dateFormat> must be populated with a correct date format!";
    private static final String INVALID_TIME_FORMAT_VALUE = "<timeFormat> must be populated with a correct time format!";
    private static final String SUMMARY_FORMAT = "Properties updates successfully";

    private FileUtil fileUtils;

    /**
     * This base directory we are looking for the property file in.
     *
     * @since 1.0
     */
    @Parameter(property = "basedir", defaultValue = ".")
    protected String basedir = ".";

    /**
     * This versions build date property name.
     *
     * @since 1.0
     */
    @Parameter(property = "propertyFile", required = true)
    protected String propertyFile;

    /**
     * This versions version number property name.
     *
     * @since 1.0
     */
    @Parameter(property = "versionName", required = true, defaultValue = "version.number")
    protected String versionName;

    /**
     * This versions build date property name.
     *
     * @since 1.0
     */
    @Parameter(property = "buildDateName", required = true, defaultValue = "version.builddate")
    protected String buildDateName;

    /**
     * The Date format we are using for the build date property.
     *
     * @since 1.0
     */
    @Parameter(property = "dateFormat", required = true, defaultValue = "dd-MM-yyyy")
    protected String dateFormat;

    /**
     * the Time format we are using for the build date property.
     *
     * @since 1.0
     */
    @Parameter(property = "timeFormat", defaultValue = "HH:mm:ss")
    protected String timeFormat;

    /**
     * Are we going to add the time to date format.
     *
     * @since 1.0
     */
    @Parameter(property = "addTime", defaultValue = "false")
    protected boolean addTime;

    /**
     * Are we going to add the time to date format.
     *
     * @since 1.0
     */
    @Parameter(property = "increment", defaultValue = "1")
    protected int increment;

    /**
     * This versions build date property name.
     *
     * @since 1.0
     */
    @Parameter(property = "skip", defaultValue = "false")
    protected boolean skip;

    /**
     * This versions build date property name.
     *
     * @since 1.0
     */
    @Parameter(property = "quiet", defaultValue = "false")
    protected boolean quiet;


    /**
     * Default Constructor.
     */
    public VersionUpdaterMojo() {
        super();
        fileUtils = new FileUtil();
    }

    /**
     * Default Constructor.
     */
    public VersionUpdaterMojo(FileUtil fileUtils) {
        super();
        this.fileUtils = fileUtils;
    }


    /**
     * The entry point for the maven plugin.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().info("Skipping");
            return;
        }
        if (checkFileExists()) {
            getLog().info("Ignoring missing file");
            return;
        }

        try {
            String filename = getBaseDirPrefixedFilename(getPropertyFile());
            Properties prop = loadProperties(filename);

            prop.setProperty(getVersionName(), processVersionNumberChange(prop.getProperty(getVersionName()), getIncrement(), getLog()));
            prop.setProperty(getBuildDateName(), processBuildDateChange(getDateFormat(), getTimeFormat(), isAddTime(), getLog()));

            prop.store(new FileOutputStream(filename), null);

            if (!isQuiet()) {
                getLog().info(SUMMARY_FORMAT);
            }
        } catch (IOException ex) {
            getLog();
        }
    }


    /* With the data given build the new version number and return the result. */
    private String processVersionNumberChange(String property, int increment, Log log) throws MojoExecutionException {
        if (increment <= 0) {
            log.error(INVALID_INCREMENT_VALUE);
            throw new MojoExecutionException(INVALID_INCREMENT_VALUE);
        }
        if (StringUtils.isEmpty(property)) {
            property = "0";
        }

        DefaultArtifactVersion version = new DefaultArtifactVersion(property);
        int changed = version.getBuildNumber() + increment;
        return buildVersionNumber(version, changed);
    }

    /* With the data given build the new version build date and return the result. */
    private String processBuildDateChange(String dateFormat, String timeFormat, boolean addTime, Log log) throws MojoExecutionException {
        if (StringUtils.isEmpty(dateFormat)) {
            log.error(INVALID_DATE_FORMAT_VALUE);
            throw new MojoExecutionException(INVALID_DATE_FORMAT_VALUE);
        }
        if (addTime && StringUtils.isEmpty(timeFormat)) {
            log.error(INVALID_TIME_FORMAT_VALUE);
            throw new MojoExecutionException(INVALID_TIME_FORMAT_VALUE);
        }

        DateFormat df;
        if (addTime) {
            df = new SimpleDateFormat(dateFormat + " " + timeFormat);
        } else {
            df = new SimpleDateFormat(dateFormat);
        }

        return df.format(new Date());
    }

    /* Build new version number. */
    private String buildVersionNumber(DefaultArtifactVersion version, int changed) {
        StringBuilder buf = new StringBuilder();
        buf.append(version.getMajorVersion()).append(".");
        buf.append(version.getMinorVersion()).append(".");
        buf.append(version.getIncrementalVersion()).append("-");
        buf.append(changed);
        return buf.toString();
    }

    /* Loads the file we specified into a properties object. */
    private Properties loadProperties(String baseDirPrefixedFilename) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(baseDirPrefixedFilename));
        return prop;
    }

    /* Check that the filename passed in exists within the maven project. */
    private boolean checkFileExists() throws MojoExecutionException {
        if (getPropertyFile() == null) {
            getLog().error(INVALID_MISSING_FILE_MESSAGE);
            throw new MojoExecutionException(INVALID_MISSING_FILE_MESSAGE);
        }
        return fileUtils.fileNotExists(getBaseDirPrefixedFilename(getPropertyFile()));
    }

    /* Get the correct path to the file. */
    private String getBaseDirPrefixedFilename(String file) {
        if (isBlank(getBasedir()) || fileUtils.isAbsolutePath(file)) {
            return file;
        }
        return getBasedir() + File.separator + file;
    }


    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getBuildDateName() {
        return buildDateName;
    }

    public void setBuildDateName(String buildDateName) {
        this.buildDateName = buildDateName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public boolean isAddTime() {
        return addTime;
    }

    public void setAddTime(boolean addTime) {
        this.addTime = addTime;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
}
