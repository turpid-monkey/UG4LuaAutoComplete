package edu.gcsc.vrl.lua.autocompletion;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import edu.gcsc.vrl.BufferedLineReader;

public class RegFunctionDescriptionTest {

	@Test
	public void testReadFromStream() throws IOException {
		String str = "foo\n" + "void\n" + "void foo (size_t bar)\n"
				+ "<p>some function level html\n" + ";";

		RegFunctionDescription descr = RegFunctionDescription
				.read(new BufferedLineReader(new StringReader(str)));

		assertEquals("food", descr.getName());

		assertEquals("void", descr.getReturntype());
		assertEquals("void foo (size_t bar)", descr.getSignature());
		assertEquals("<p>some function level html", descr.getHtml());
	}

}
