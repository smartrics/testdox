package com.thoughtworks.testdox;

import junit.framework.TestCase;

import java.util.List;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;

import com.thoughtworks.testdox.Gui;
import com.thoughtworks.testdox.Main;

public class MainTest extends TestCase {
    private MockDocumentGenerator gen;
    private Main main;

    protected void setUp() throws Exception {

        gen = new MockDocumentGenerator();

        main = new Main();
        main.setTestDirectory("src/test");
        main.addDocumentGenerator(gen);
    }

    public void testMainParsesThisTest() {

        main.parse();

        List descriptions = gen.getTestDescriptions("Main");
        assertNotNull(descriptions);
        assertTrue(descriptions.contains("Main parses this test"));

    }

    public void testMainHandlesMultipleDocumentGenerators() {

        MockDocumentGenerator gen1 = new MockDocumentGenerator();
        MockDocumentGenerator gen2 = new MockDocumentGenerator();

        main.addDocumentGenerator(gen1);
        main.addDocumentGenerator(gen2);

        main.parse();

        assertNotNull(gen1.getTestDescriptions("Main"));
        assertNotNull(gen2.getTestDescriptions("Main"));

    }

    public void testIncludesCommonTestFileNamePatterns() {
		assertTrue(main.isTestClass("FooTest"));
		assertTrue(main.isTestClass("FooTestCase"));
		assertTrue(main.isTestClass("TestFoo"));
    }

	public void testIgnoreNonTestClasses() {
		assertTrue(!main.isTestClass("Foo"));
		assertTrue(!main.isTestClass("FooTestBlah"));
	}

    public void testIgnoreSetUpMethod() {
        main.parse();
        List descriptions = gen.getTestDescriptions("Main");
        assertNotNull(descriptions);
        assertFalse(descriptions.contains("p"));
    }
    
    public void testMainShowsUsageIfCalledWithHelpParameter() {
        assertContains(runAndRecordSysError(new String[]{"--help"}), "Usage");
        assertContains(runAndRecordSysError(new String[]{"-h"}), "Usage");
    }

    public void testIfNoArgumentsShowGui() {
        Main.main(new String[] {});
        Gui frame = (Gui) Main.gui;
        assertNotNull( frame );
        assertTrue(frame.isVisible());
        assertNotNull(frame.gen);
    }

    public void testSpecifyTextOutputFile() throws IOException {
        Main main = new Main();
        main.processArguments(new String[] {"-txt", "foo.txt", "src"});
        assertTrue(new File("foo.txt").exists());
    }

    public void testSpecifyHtmlOutputFile() throws IOException {
        Main main = new Main();
        main.processArguments(new String[] {"-html", "foo.html", "src"});
        assertTrue(new File("foo.html").exists());
    }

    private void assertContains(String result, String testString) {
        assertNotNull(result);
        assertTrue(result.indexOf(testString) >= 0 );
    }

    private String runAndRecordSysError(String[] args) {
        PrintStream oldErr = System.err;
        String result = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            Main.main(args);
            result = out.toString();
        }
        finally {
            System.setErr(oldErr);
        }
        return result;
    }


}
