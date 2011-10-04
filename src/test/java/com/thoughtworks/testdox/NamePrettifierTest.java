package com.thoughtworks.testdox;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.IndentBuffer;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

import junit.framework.TestCase;

public class NamePrettifierTest {
	private NamePrettifier namePrettifier;

	@Before
	public void setUp() throws Exception {
		namePrettifier = new NamePrettifier();
	}

    @Test
	public void testTitleHasSensibleDefaults() {
		assertEquals("Foo", namePrettifier.prettifyTestClass("FooTest"));
		assertEquals("Foo", namePrettifier.prettifyTestClass("TestFoo"));
		assertEquals("Foo", namePrettifier.prettifyTestClass("TestFooTest"));
	}

    @Test
	public void testCaterForUserDefinedSuffix() {
		namePrettifier.setSuffix("TestCase");
		namePrettifier.setPrefix(null);
		assertEquals("Foo", namePrettifier.prettifyTestClass("FooTestCase"));
		assertEquals("TestFoo", namePrettifier.prettifyTestClass("TestFoo"));
		assertEquals("FooTest", namePrettifier.prettifyTestClass("FooTest"));
	}

    @Test
	public void testCaterForUserDefinedPrefix() {
		namePrettifier.setSuffix(null);
		namePrettifier.setPrefix("XXX");
		assertEquals("Foo", namePrettifier.prettifyTestClass("XXXFoo"));
		assertEquals("TestXXX", namePrettifier.prettifyTestClass("TestXXX"));
		assertEquals("XXX", namePrettifier.prettifyTestClass("XXXXXX"));
	}

    @Test
	public void testTestNameIsConvertedToASentence() {
		assertEquals("This is a test",
				namePrettifier.prettifyTestMethod("testThisIsATest"));
		assertEquals(
				"Database_column_spec is set correctly",
				namePrettifier
						.prettifyTestMethod("testDatabase_column_specIsSetCorrectly"));
		assertEquals(
				"Database_column_spec is set correctly",
				namePrettifier
						.prettifyTestMethod("testdatabase_column_specIsSetCorrectly"));
		assertEquals("Should calculate foo",
				namePrettifier.prettifyTestMethod("shouldCalculateFoo"));
		assertEquals("Should coumn_spec be set to false",
				namePrettifier
						.prettifyTestMethod("shouldCoumn_specBeSetToFalse"));
		assertEquals("Should set coumn_spec be to false",
				namePrettifier
						.prettifyTestMethod("shouldSetCoumn_specBeToFalse"));
		assertEquals("Shouldcoumn_spec be set to false",
				namePrettifier
						.prettifyTestMethod("shouldcoumn_specBeSetToFalse"));
	}

    @Test
	public void testBadMethodName() {
		assertEquals("", namePrettifier.prettifyTestMethod("test"));
	}

    @Test
	public void testIsATestIsFalseForNonTestMethods() {
		assertFalse(namePrettifier.isATestMethod("setUp"));
		assertFalse(namePrettifier.isATestMethod("tearDown"));
		assertFalse(namePrettifier.isATestMethod("foo"));
	}

    @Test
	public void testIsATestIsFalseForNonAnnotatedTestMethods() {
		assertTrue(namePrettifier.isATestMethod(createTestAnnotatedJavaMethod("fooTest")));
		assertTrue(namePrettifier.isATestMethod(createTestAnnotatedJavaMethod("testFoo")));
		assertTrue(namePrettifier.isATestMethod(createTestAnnotatedJavaMethod("foo")));
	}

    @Test
	public void testIsATestIsTrueForTestMethods() {
		assertTrue(namePrettifier.isATestMethod("fooTest"));
		assertTrue(namePrettifier.isATestMethod("shouldCalculateBars"));
		assertTrue(namePrettifier.isATestMethod("testFooBars"));
	}

	private JavaMethod createTestAnnotatedJavaMethod(String name) {
		JavaDocBuilder builder = new JavaDocBuilder();
		String classString = "class SampleTest {\n";
		classString = classString + "@org.junit.Test public void " + name + "() {}";
		classString = classString + "}";
		builder.addSource(new StringReader(classString));
		JavaSource[] sources = builder.getSources();
		JavaSource javaSource = sources[0];
		JavaClass javaClass = javaSource.getClasses()[0];
		JavaMethod javaMethod = javaClass.getMethods()[0];
		return javaMethod;
	}
}
