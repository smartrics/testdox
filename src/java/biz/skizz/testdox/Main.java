package biz.skizz.testdox;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Simple usage:
 * 
 *
 */
public class Main {

    private static String usage = 
		"Generate simple test documentation from JUnit source files\n" +
	String directory;
    private MultiplexingGenerator gen = new MultiplexingGenerator();
    private NamePrettifier prettifier = new NamePrettifier();

    public Main() {
        gen.addGenerator( new ConsoleGenerator() );
    }

    public void setTestDirectory(String directory) {
        this.directory = directory;
    }

    public void addDocumentGenerator(DocumentGenerator generator) {
        gen.addGenerator(generator);
    }

    public void parse() {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(directory));
        doSources(builder.getSources());
    }

    private void doSources(JavaSource[] sources) {
        for (int i = 0; i < sources.length; i++) {
            doClasses(sources[i].getClasses());
        }
    }

    void doClasses(JavaClass[] classes) {
        for (int j = 0; j < classes.length; j++) {
            JavaClass aClass = classes[j];
            if ( isTestClass(aClass.getName())) {
	    	String prettyName = prettifier.prettifyTestClass(aClass.getName());
                gen.startClass(prettyName);
                doMethods(aClass.getMethods());
		gen.endClass(prettyName);
            }
        }
    }

    boolean isTestClass(String className) {
		return className.endsWith("Test") || 
			className.endsWith("TestCase") || 
			className.startsWith("Test");
	}

	private void doMethods(JavaMethod[] methods) {
        for (int k = 0; k < methods.length; k++) {

            String name = methods[k].getName();
            if (prettifier.isATestMethod(name)) {
                gen.onTest( prettifier.prettifyTestMethod( name ) );
            }
        }
    }

    public static void main(String[] args) {
    	if ( args.length == 0 ) {
    		String message = MessageFormat.format(usage, new String[] {Main.class.getName()} );
    		System.err.println(message);
    		return;	
    	}
    	
        File file = new File(args[0]);
        Main main = new Main();
        try {
            main.setTestDirectory(file.getCanonicalPath());
            main.parse();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}