package edu.gcsc.vrl.lua.autocompletion;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.gcsc.vrl.BufferedLineReader;

public class RegClassDescriptionTest {

	@Test
	public void testReadFromStream() throws IOException {
		String str = "ClassName\n" + "ParentClass1 ParentClass2\n"
				+ "<p>some class-level Html\n" + "constructor\n"
						+ "create\n"
						+ "void\n"
						+ "void create (size_t init)\n"
						+ "<p>some constructor level html\n"
						+ "memberfunction\n" + "foo\n"
				+ "void\n" + "void foo (size_t bar)\n"
				+ "<p>some function level html\n" + ";";

		RegClassDescription descr = RegClassDescription
				.read(new BufferedLineReader(new StringReader(str)));
		
		assertEquals("ClassName", descr.getName());

		assertEquals(2, descr.getClassHierachyStr().length);
		assertEquals("ParentClass1", descr.getClassHierachyStr()[0]);
		assertEquals("ParentClass2", descr.getClassHierachyStr()[1]);
		assertEquals("<p>some class-level Html", descr.getHtml());
		assertEquals(1, descr.getMemberfunctions().size());
		assertEquals(1, descr.getConstructors().size());
	}

}
