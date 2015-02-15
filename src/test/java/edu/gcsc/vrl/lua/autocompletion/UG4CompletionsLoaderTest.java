package edu.gcsc.vrl.lua.autocompletion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

import edu.gcsc.vrl.BufferedLineReader;

public class UG4CompletionsLoaderTest {

	@Test
	public void testLoadClass() throws Exception {
		UG4CompletionsLoader loader = new UG4CompletionsLoader();
		String str = "UG4COMPLETER VERSION 1\n" + "class\n" + "ClassName\n"
				+ "ParentClass1 ParentClass2\n" + "<p>some class-level Html\n"
				+ "memberfunction\n" + "foo\n" + "void\n"
				+ "void foo (size_t bar)\n" + "<p>some function level html\n"
				+ ";";
		loader.load(new BufferedLineReader(new StringReader(str)));
		assertEquals(3, loader.getClasses().size());

	}

	@Test
	public void testResolve() throws Exception {
		UG4CompletionsLoader loader = new UG4CompletionsLoader();
		String str = "UG4COMPLETER VERSION 1\n" + "class\n" + "ClassName\n"
				+ "ParentClass1 ParentClass2\n" + "<p>some class-level Html\n"
				+ "memberfunction\n" + "foo\n" + "void\n"
				+ "void foo (size_t bar)\n" + "<p>some function level html\n"
				+ ";";
		loader.load(new BufferedLineReader(new StringReader(str)));
		RegClassDescription cls = loader.resolveClass("ClassName");
		assertNotNull(cls);
	}

//	@Test
//	public void testSampleFile() throws Exception {
//		UG4CompletionsLoader loader = new UG4CompletionsLoader();
//		loader.load(new BufferedLineReader(
//				new InputStreamReader(
//						UG4CompletionsLoader.class
//								.getClassLoader()
//								.getResourceAsStream(
//										"edu/gcsc/vrl/lua/autocompletion/ugCompletions.txt"))));
//		assertEquals(1150, loader.getClasses().size());
//		assertEquals(313, loader.getFunctions().size());
//
//	}
}
