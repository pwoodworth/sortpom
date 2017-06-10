package sortpom.parameter;

import java.io.File;

/** Contains all parameters that are sent to the plugin */
public class PluginParameters {

    public final String groupId;
    public final File pomFile;
    public final boolean createBackupFile;
    public final String backupFileExtension;
    public final String violationFilename;
    public final String encoding;
    public final LineSeparatorUtil lineSeparatorUtil;
    public final String indentCharacters;
    public final boolean expandEmptyElements;
    public final String predefinedSortOrder;
    public final String customSortOrderFile;
    public final DependencySortOrder sortDependencies;
    public final DependencySortOrder sortPlugins;
    public final boolean sortProperties;
    public final boolean sortModules;
    public final boolean keepBlankLines;
    public final boolean indentBlankLines;
    public final VerifyFailType verifyFailType;
    public final boolean ignoreLineSeparators;

    private PluginParameters(String groupId,
                             File pomFile,
                             boolean createBackupFile,
                             String backupFileExtension,
                             String violationFilename,
                             String encoding,
                             LineSeparatorUtil lineSeparatorUtil,
                             boolean expandEmptyElements,
                             boolean keepBlankLines,
                             String indentCharacters,
                             boolean indentBlankLines,
                             String predefinedSortOrder,
                             String customSortOrderFile,
                             DependencySortOrder sortDependencies,
                             DependencySortOrder sortPlugins,
                             boolean sortProperties,
                             boolean sortModules,
                             VerifyFailType verifyFailType,
                             boolean ignoreLineSeparators) {
        this.groupId = groupId;
        this.pomFile = pomFile;
        this.createBackupFile = createBackupFile;
        this.backupFileExtension = backupFileExtension;
        this.violationFilename = violationFilename;
        this.encoding = encoding;
        this.lineSeparatorUtil = lineSeparatorUtil;
        this.indentCharacters = indentCharacters;
        this.expandEmptyElements = expandEmptyElements;
        this.predefinedSortOrder = predefinedSortOrder;
        this.customSortOrderFile = customSortOrderFile;
        this.sortDependencies = sortDependencies;
        this.sortPlugins = sortPlugins;
        this.sortProperties = sortProperties;
        this.sortModules = sortModules;
        this.keepBlankLines = keepBlankLines;
        this.indentBlankLines = indentBlankLines;
        this.verifyFailType = verifyFailType;
        this.ignoreLineSeparators = ignoreLineSeparators;
    }

    /** Instantiate builder */
    public static Builder builder() {
        return new PluginParameters.Builder();
    }

    /** Builder for the PluginParameters class */
    public static class Builder {

        private String groupId;
        private File pomFile;
        private boolean createBackupFile;
        private String backupFileExtension;
        private String violationFilename;
        private String encoding;
        private LineSeparatorUtil lineSeparatorUtil;
        private String indentCharacters;
        private boolean indentBlankLines;
        private boolean expandEmptyElements;
        private String predefinedSortOrder;
        private String customSortOrderFile;
        private String sortDependencies;
        private String sortPlugins;
        private boolean sortProperties;
        private boolean sortModules;
        private boolean keepBlankLines;
        private VerifyFailType verifyFailType;
        private boolean ignoreLineSeparators;
        private String prioritizedDependencyGroups;
        private String prioritizedPluginGroups;

        private Builder() {
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        /** Sets pomFile location */
        public Builder setPomFile(final File pomFile) {
            this.pomFile = pomFile;
            return this;
        }

        /** Sets information regarding backup file */
        public Builder setFileOutput(final boolean createBackupFile, final String backupFileExtension, String violationFilename) {
            this.createBackupFile = createBackupFile;
            this.backupFileExtension = backupFileExtension;
            this.violationFilename = violationFilename;
            return this;
        }

        /** Sets which encoding should be used throughout the plugin */
        public Builder setEncoding(final String encoding) {
            this.encoding = encoding;
            return this;
        }

        /** Sets formatting information that is used when the pom file is sorted */
        public Builder setFormatting(final String lineSeparator,
                                     final boolean expandEmptyElements,
                                     final boolean keepBlankLines) {
            this.lineSeparatorUtil = new LineSeparatorUtil(lineSeparator);
            this.expandEmptyElements = expandEmptyElements;
            this.keepBlankLines = keepBlankLines;
            return this;
        }

        /** Sets indent information that is used when the pom file is sorted */
        public Builder setIndent(final int nrOfIndentSpace, final boolean indentBlankLines) {
            this.indentCharacters = new IndentCharacters(nrOfIndentSpace).getIndentCharacters();
            this.indentBlankLines = indentBlankLines;
            return this;
        }

        /** Sets which sort order that should be used when sorting */
        public Builder setSortOrder(final String customSortOrderFile, final String predefinedSortOrder) {
            this.customSortOrderFile = customSortOrderFile;
            this.predefinedSortOrder = predefinedSortOrder;
            return this;
        }

        /** Sets if dependency pom file elements should be sorted */
        public Builder setSortDependencies(String sortDependencies) {
            this.sortDependencies = sortDependencies;
            return this;
        }

        /** Sets if dependency pom file elements should be sorted */
        public Builder setPrioritizedDependencyGroups(String prioritizedDependencyGroups) {
            this.prioritizedDependencyGroups = prioritizedDependencyGroups;
            return this;
        }

        /** Sets if plugin pom file elements should be sorted */
        public Builder setSortPlugins(String sortPlugins) {
            this.sortPlugins = sortPlugins;
            return this;
        }

        /** Sets if dependency pom file elements should be sorted */
        public Builder setPrioritizedPluginGroups(String prioritizedPluginGroups) {
            this.prioritizedPluginGroups = prioritizedPluginGroups;
            return this;
        }

        /** Sets if properties pom file elements should be sorted */
        public Builder setSortProperties(boolean sortProperties) {
            this.sortProperties = sortProperties;
            return this;
        }

        /** Sets if module pom file elements should be sorted */
        public Builder setSortModules(boolean sortModules) {
            this.sortModules = sortModules;
            return this;
        }

        /** Sets the verify operation behaviour */
        public Builder setVerifyFail(String verifyFail) {
            this.verifyFailType = VerifyFailType.fromString(verifyFail);
            return this;
        }

        /** Sets triggers to decide when the pom should be sorted **/
        public Builder setTriggers(boolean ignoreLineSeparators) {
            this.ignoreLineSeparators = ignoreLineSeparators;
            return this;
        }

        /** Build the PluginParameters instance */
        public PluginParameters build() {
            return new PluginParameters(groupId, pomFile, createBackupFile, backupFileExtension, violationFilename,
                    encoding, lineSeparatorUtil, expandEmptyElements, keepBlankLines, indentCharacters, indentBlankLines,
                    predefinedSortOrder, customSortOrderFile,
                    new DependencySortOrder(sortDependencies, prioritizedDependencyGroups),
                    new DependencySortOrder(sortPlugins, prioritizedPluginGroups),
                    sortProperties, sortModules,
                    verifyFailType, ignoreLineSeparators);
        }
    }

}
