package sortpom.util;

import sortpom.parameter.PluginParameters;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

public class SortPomImplUtil {

    private TestHandler testHandler;

    private String defaultOrderFileName = "default_0_4_0.xml";
    private String sortDependencies = "";
    private String sortPlugins = "";
    private boolean sortProperties = false;
    private boolean sortModules = false;
    private String predefinedSortOrder = "";
    private String lineSeparator = "\r\n";
    private String testPomFileName = "src/test/resources/testpom.xml";
    private String testPomBackupExtension = ".testExtension";

    private int nrOfIndentSpace = 2;
    private boolean keepBlankLines = false;
    private boolean ignoreLineSeparators = true;
    private boolean indentBLankLines = false;
    private String verifyFail = "SORT";
    private String encoding = TestHandler.UTF_8;
    private File testpom;
    private String violationFile;
    private String groupId;

    private SortPomImplUtil() {
    }

    public static SortPomImplUtil create() {
        return new SortPomImplUtil();
    }

    public void testFiles(final String inputResourceFileName, final String expectedResourceFileName)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, expectedResourceFileName, getPluginParameters());
        testHandler.performTest();
    }

    public List<String> testFilesAndReturnLogs(final String inputResourceFileName, final String expectedResourceFileName)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, expectedResourceFileName, getPluginParameters());
        testHandler.performTest();
        return testHandler.getInfoLogger();
    }

    public void testNoSorting(final String inputResourceFileName)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, inputResourceFileName, getPluginParameters());
        testHandler.performNoSortTest();
        assertEquals("[INFO] Pom file is already sorted, exiting", testHandler.getInfoLogger().get(1));
    }

    public void testVerifyXmlIsOrdered(final String inputResourceFileName)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, getPluginParameters());
        XmlOrderedResult xmlOrderedResult = testHandler.performVerify();
        assertEquals("Expected that xml is ordered, ", true, xmlOrderedResult.isOrdered());
    }

    public void testVerifyXmlIsNotOrdered(final String inputResourceFileName, CharSequence warningMessage)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, getPluginParameters());
        XmlOrderedResult xmlOrderedResult = testHandler.performVerify();
        assertEquals("Expected that xml is not ordered, ", false, xmlOrderedResult.isOrdered());
        assertEquals(warningMessage, xmlOrderedResult.getErrorMessage());
    }

    public void testVerifySort(final String inputResourceFileName, final String expectedResourceFileName, String warningMessage, boolean outputToViolationFile)
            throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, expectedResourceFileName, getPluginParameters());
        testHandler.performTestThatSorted();
        int index = assertStartOfMessages(warningMessage, outputToViolationFile);
        assertThat(testHandler.getInfoLogger().get(index), startsWith("[INFO] The file "));
        assertThat(testHandler.getInfoLogger().get(index++), endsWith(" is not sorted"));
        assertThat(testHandler.getInfoLogger().get(index), startsWith("[INFO] Sorting file "));
    }

    public void testVerifyFail(String inputResourceFileName, Class<?> expectedExceptionClass, String warningMessage, boolean outputToViolationFile) {
        setup();
        testHandler = new TestHandler(inputResourceFileName, getPluginParameters());
        try {
            testHandler.performTestThatDidNotSort();
            fail();
        } catch (Exception e) {
            assertEquals(expectedExceptionClass, e.getClass());
            int index = assertStartOfMessages(warningMessage, outputToViolationFile);
            assertThat(testHandler.getInfoLogger().get(index), startsWith("[ERROR] The file "));
            assertThat(testHandler.getInfoLogger().get(index), endsWith(" is not sorted"));
        }
    }

    public void testVerifyWarn(String inputResourceFileName, String warningMessage, boolean outputToViolationFile) throws Exception {
        setup();
        testHandler = new TestHandler(inputResourceFileName, inputResourceFileName, getPluginParameters());
        testHandler.performTestThatDidNotSort();

        int index = assertStartOfMessages(warningMessage, outputToViolationFile);
        assertThat(testHandler.getInfoLogger().get(index), startsWith("[WARNING] The file "));
        assertThat(testHandler.getInfoLogger().get(index), endsWith(" is not sorted"));
    }

    private int assertStartOfMessages(String warningMessage, boolean outputToViolationFile) {
        int index = 0;
        assertThat(testHandler.getInfoLogger().get(index++), startsWith("[INFO] Verifying file "));
        assertEquals(warningMessage, testHandler.getInfoLogger().get(index++));

        if (outputToViolationFile) {
            assertThat(testHandler.getInfoLogger().get(index++), startsWith("[INFO] Saving violation report to "));
        }
        return index;
    }

    public SortPomImplUtil setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public SortPomImplUtil nrOfIndentSpace(int indent) {
        nrOfIndentSpace = indent;
        return this;
    }

    public SortPomImplUtil keepBlankLines() {
        keepBlankLines = true;
        return this;
    }

    public SortPomImplUtil indentBLankLines() {
        indentBLankLines = true;
        return this;
    }

    public SortPomImplUtil sortDependencies(String sortOrder) {
        sortDependencies = sortOrder;
        return this;
    }

    public SortPomImplUtil sortPlugins(String sortOrder) {
        sortPlugins = sortOrder;
        return this;
    }

    public SortPomImplUtil sortProperties() {
        sortProperties = true;
        return this;
    }

    public SortPomImplUtil sortModules() {
        sortModules = true;
        return this;
    }

    public SortPomImplUtil defaultOrderFileName(String defaultOrderFileName) {
        this.defaultOrderFileName = defaultOrderFileName;
        return this;
    }

    public SortPomImplUtil predefinedSortOrder(String predefinedSortOrder) {
        this.predefinedSortOrder = predefinedSortOrder;
        this.defaultOrderFileName = null;
        return this;
    }

    public SortPomImplUtil lineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    public SortPomImplUtil ignoreLineSeparators(boolean ignoreLineSeparators) {
        this.ignoreLineSeparators = ignoreLineSeparators;
        return this;
    }

    public SortPomImplUtil verifyFail(String verifyFail) {
        this.verifyFail = verifyFail;
        return this;
    }

    public SortPomImplUtil backupFileExtension(String backupFileExtension) {
        this.testPomBackupExtension = backupFileExtension;
        return this;
    }

    public SortPomImplUtil encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public SortPomImplUtil testPomFileNameUniqueNumber(int uniqueNumber) {
        this.testPomFileName = "src/test/resources/testpom" +
                uniqueNumber + ".xml";
        return this;
    }

    public SortPomImplUtil violationFile(String violationFile) {
        this.violationFile = violationFile;
        return this;
    }

    private void setup() {
        testpom = new File(testPomFileName);
    }

    private PluginParameters getPluginParameters() {
        return PluginParameters.builder()
                .setPomFile(testpom)
                .setFileOutput(true, testPomBackupExtension, violationFile)
                .setEncoding(encoding)
                .setFormatting(lineSeparator, true, keepBlankLines)
                .setIndent(nrOfIndentSpace, indentBLankLines)
                .setSortDependencies(sortDependencies)
                .setSortPlugins(sortPlugins)
                .setSortProperties(sortProperties)
                .setSortModules(sortModules)
                .setSortOrder(defaultOrderFileName, predefinedSortOrder)
                .setVerifyFail(verifyFail)
                .setTriggers(ignoreLineSeparators)
                .setPrioritizedDependencyGroups(groupId)
                .build();
    }

}
