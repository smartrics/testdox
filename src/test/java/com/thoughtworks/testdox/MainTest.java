package com.thoughtworks.testdox;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class MainTest {
    private MockDocumentGenerator gen;
    private Main main;

    @Before
    public void setUp() throws Exception {

        gen = new MockDocumentGenerator();

        main = new Main();
        main.setTestDirectory("src/test");
        main.addDocumentGenerator(gen);
    }

    @Test
    public void testMainParsesThisTest() {
        main.parse();
        try {
            main.parse();
        } catch (Exception e) {
            fail("Unexpected exception " + e.getLocalizedMessage());
        }

        List descriptions = gen.getTestDescriptions("Main");
        assertNotNull(descriptions);
        assertTrue(descriptions.contains("Main parses this test"));

    }

    @Test
    public void testMainHandlesMultipleDocumentGenerators() {

        MockDocumentGenerator gen1 = new MockDocumentGenerator();
        MockDocumentGenerator gen2 = new MockDocumentGenerator();

        main.addDocumentGenerator(gen1);
        main.addDocumentGenerator(gen2);

        try {
            main.parse();
        } catch (Exception e) {
            fail("Unexpected exception " + e.getLocalizedMessage());
        }

        assertNotNull(gen1.getTestDescriptions("Main"));
        assertNotNull(gen2.getTestDescriptions("Main"));

    }

    @Test
    public void testIncludesCommonTestFileNamePatterns() {
        assertTrue(main.isTestClass("FooTest"));
        assertTrue(main.isTestClass("FooTestCase"));
        assertTrue(main.isTestClass("TestFoo"));
    }

    @Test
    public void testIgnoreNonTestClasses() {
        assertTrue(!main.isTestClass("Foo"));
        assertTrue(!main.isTestClass("FooTestBlah"));
    }

    @Test
    public void testIgnoreSetUpMethod() {
        main.parse();
        try {
            main.parse();
        } catch (Exception e) {
            fail("Unexpected exception " + e.getLocalizedMessage());
        }
        List descriptions = gen.getTestDescriptions("Main");
        assertNotNull(descriptions);
        assertFalse(descriptions.contains("p"));
    }

    @Test
    public void testMainShowsUsageIfCalledWithHelpParameter() {
        assertContains(runAndRecordSysError(new String[] { "--help" }), "Usage");
        assertContains(runAndRecordSysError(new String[] { "-h" }), "Usage");
    }

    @Test
    public void testIfNoArgumentsShowGui() {
        Main.main(new String[] {});
        Gui frame = (Gui) Main.gui;
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertNotNull(frame.gen);
    }

    @Test
    public void testSpecifyTextOutputFile() throws IOException {
        Main main = new Main();
        main.processArguments(new String[] { "-txt", "foo.txt", "src" });
        assertTrue(new File("foo.txt").exists());
    }

    @Test
    public void testMainShowsUsageIfNoParameters() {
        PrintStream oldErr = System.err;
        String result = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            Main.main(new String[] {});
            result = out.toString();
        } finally {
            System.setErr(oldErr);
        }
        assertNotNull(result);
        assertTrue(result.indexOf(Main.class.getName()) >= 0);
    }

    @Test
    public void testSpecifyHtmlOutputFile() throws IOException {
        Main main = new Main();
        main.processArguments(new String[] { "-html", "foo.html", "src" });
        assertTrue(new File("foo.html").exists());
    }

    private void assertContains(String result, String testString) {
        assertNotNull(result);
        assertTrue(result.indexOf(testString) >= 0);
    }

    private String runAndRecordSysError(String[] args) {
        PrintStream oldErr = System.err;
        String result = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            Main.main(args);
            result = out.toString();
        } finally {
            System.setErr(oldErr);
        }
        return result;
    }

}
