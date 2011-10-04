package com.thoughtworks.testdox.ant;

import com.thoughtworks.testdox.ConsoleGenerator;
import com.thoughtworks.testdox.HtmlDocumentGenerator;
import com.thoughtworks.testdox.Main;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author Chris Stevenson skizz@codehaus.org
 * @created 10-Dec-2003 20:20:45
 */
public class TestDoxTask extends Task {

    private File dir;
    private File output;
    private String propertyName;
    private String format;

    public void setProject(Project project) {
        this.project = project;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void execute() {
        log("Processing files from " + dir);
        Main main = new Main();
        try {
            main.setInputFile(dir);
            PrintWriter out = new PrintWriter(new FileWriter(output));
            if(format==null) {
            	main.addDocumentGenerator(new XDocGenerator(out));
            } else {
            	if(format.indexOf("html")>-1) {
                	main.addDocumentGenerator(new HtmlDocumentGenerator(out));
            	}
            	if(format.indexOf("xdoc")>-1) {
                	main.addDocumentGenerator(new XDocGenerator(out));
            	}
            	if(format.indexOf("text")>-1) {
                	main.addDocumentGenerator(new ConsoleGenerator(out));
            	}
            }
            main.generate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

}
