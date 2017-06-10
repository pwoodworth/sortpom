package sortpom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import sortpom.exception.ExceptionConverter;
import sortpom.logger.MavenLogger;
import sortpom.parameter.PluginParameters;

import java.io.File;

/**
 * Verifies that the pom.xml is sorted. If the verification fails then the pom.xml is sorted.
 *
 * @author Bjorn Ekryd
 */
@Mojo(name = "verify", threadSafe = true, defaultPhase = LifecyclePhase.VALIDATE)
@SuppressWarnings({"UnusedDeclaration"})
public class VerifyMojo extends AbstractMojo {
    /**
     * This is the File instance that refers to the location of the pom that
     * should be sorted.
     */
    @Parameter(property = "sort.pomFile", defaultValue = "${project.file}")
    private File pomFile;

    /**
     * Choose between a number of predefined sort order files.
     */
    @Parameter(property = "sort.predefinedSortOrder")
    private String predefinedSortOrder;

    /**
     * Custom sort order file.
     */
    @Parameter(property = "sort.sortOrderFile")
    private String sortOrderFile;

    /**
     * Comma-separated ordered list how dependencies should be sorted. Example: scope,groupId,artifactId
     * If scope is specified in the list then the scope ranking is COMPILE, PROVIDED, SYSTEM, RUNTIME, IMPORT and TEST.
     * The list can be separated by ",;:"
     */
    @Parameter(property = "sort.sortDependencies")
    private String sortDependencies;

    /**
     * Comma-separated ordered list how dependencies should be sorted.
     */
    @Parameter(property = "sort.dependencyPriorityGroups", defaultValue = "${project.groupId}")
    private String dependencyPriorityGroups;

    /**
     * Comma-separated ordered list how plugins should be sorted. Example: groupId,artifactId
     * The list can be separated by ",;:"
     */
    @Parameter(property = "sort.sortPlugins")
    private String sortPlugins;

    /**
     * Comma-separated ordered list how plugins should be sorted.
     */
    @Parameter(property = "sort.pluginPriorityGroups")
    private String pluginPriorityGroups;

    /**
     * Should the Maven pom properties be sorted alphabetically. Affects both
     * project/properties and project/profiles/profile/properties
     */
    @Parameter(property = "sort.sortProperties", defaultValue = "false")
    private boolean sortProperties;
    
    /**
     * Should the Maven pom sub modules be sorted alphabetically. 
     */
    @Parameter(property = "sort.sortModules", defaultValue = "false")
    private boolean sortModules;

    /**
     * Encoding for the files.
     */
    @Parameter(property = "sort.encoding", defaultValue = "UTF-8")
    private String encoding;

    /**
     * What should happen if verification fails. Can be either 'sort', 'warn' or 'stop'
     */
    @Parameter(property = "sort.verifyFail", defaultValue = "sort")
    private String verifyFail;


    /**
     * Should a backup copy be created for the sorted pom.
     */
    @Parameter(property = "sort.createBackupFile", defaultValue = "true")
    private boolean createBackupFile;

    /**
     * Name of the file extension for the backup file.
     */
    @Parameter(property = "sort.backupFileExtension", defaultValue = ".bak")
    private String backupFileExtension;

    /**
     * Saves the verification failure to an external xml file, recommended filename is 'target/sortpom_reports/violation.xml'.
     */
    @Parameter(property = "sort.violationFilename")
    private String violationFilename;

    /**
     * Line separator for sorted pom. Can be either \n, \r or \r\n
     */
    @Parameter(property = "sort.lineSeparator", defaultValue = "${line.separator}")
    private String lineSeparator;

    /**
     * Should empty xml elements be expanded or not. Example:
     * &lt;configuration&gt;&lt;/configuration&gt; or &lt;configuration/&gt;
     */
    @Parameter(property = "sort.expandEmptyElements", defaultValue = "true")
    private boolean expandEmptyElements;

    /**
     * Should blank lines in the pom-file be preserved. A maximum of one line is preserved between each tag.
     */
    @Parameter(property = "sort.keepBlankLines", defaultValue = "false")
    private boolean keepBlankLines;

    /**
     * Number of space characters to use as indentation. A value of -1 indicates
     * that tab character should be used instead.
     */
    @Parameter(property = "sort.nrOfIndentSpace", defaultValue = "2")
    private int nrOfIndentSpace;

    /**
     * Should blank lines (if preserved) have indentation.
     */
    @Parameter(property = "sort.indentBlankLines", defaultValue = "false")
    private boolean indentBlankLines;

    /**
     * Set this to 'true' to bypass sortpom plugin
     */
    @Parameter(property = "sort.skip", defaultValue = "false")
    private boolean skip;

    private final SortPomImpl sortPomImpl = new SortPomImpl();

    /**
     * Execute plugin.
     *
     * @throws org.apache.maven.plugin.MojoFailureException exception that will be handled by plugin framework
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoFailureException {
        if (skip) {
            getLog().info("Skipping Sortpom");
        } else {
            setup();
            sortPom();
        }
    }

    public void setup() throws MojoFailureException {
        new ExceptionConverter(() -> {

            PluginParameters pluginParameters = PluginParameters.builder()
                    .setPomFile(pomFile)
                    .setFileOutput(createBackupFile, backupFileExtension, violationFilename)
                    .setEncoding(encoding)
                    .setFormatting(lineSeparator, expandEmptyElements, keepBlankLines)
                    .setIndent(nrOfIndentSpace, indentBlankLines)
                    .setSortOrder(sortOrderFile, predefinedSortOrder)
                    .setSortDependencies(sortDependencies)
                    .setSortPlugins(sortPlugins)
                    .setSortProperties(sortProperties)
                    .setSortModules(sortModules)
                    .setVerifyFail(verifyFail)
                    .setPrioritizedDependencyGroups(dependencyPriorityGroups)
                    .setPrioritizedPluginGroups(pluginPriorityGroups)
                    .build();

            sortPomImpl.setup(new MavenLogger(getLog()), pluginParameters);
            
        }).executeAndConvertException();
    }

    private void sortPom() throws MojoFailureException {
        new ExceptionConverter(sortPomImpl::verifyPom).executeAndConvertException();
    }

}
