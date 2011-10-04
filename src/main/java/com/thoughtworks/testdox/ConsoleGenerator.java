package com.thoughtworks.testdox;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * 2003-08-12 out.flush() added at the suggestion of Mike Mason
 */
public class ConsoleGenerator extends AbstractGenerator {
    private PrintWriter out;

    public ConsoleGenerator() {
        this(System.out);
    }

    public ConsoleGenerator(PrintStream out) {
        this.out = new PrintWriter(out);
    }

    public ConsoleGenerator(PrintWriter out) {
        this.out = out;
    }

    public void startClass(String name) {
    	super.startClass(name);
        out.println(name);
        out.flush();
    }
    public void onTest(String name) {
    	super.onTest(name);
        out.println("    - " + name);
        out.flush();
    }

    public void endClass(String name) {
    }

    public void startRun() {

    }

    public void endRun() {

    }

    public void startPackage(String name) {
    	super.startPackage(name);
        System.out.println("<"+name+">");
    }

    public void endGeneration() {
        System.out.println("Total number of test classes: "+getNumberOfTestClasses());
        System.out.println("Total number of test casses: "+getNumberOfTestCasses());
        System.out.println("Finished generation...");
    }
}
