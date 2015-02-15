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

		assertEquals("foo", descr.getName());

		assertEquals("void", descr.getReturntype());
		assertEquals("void foo (size_t bar)", descr.getSignature());
		assertEquals("<p>some function level html", descr.getHtml());
	}
	
	@Test
	public void paramCountSimple() throws Exception
	{
		String str = "foo\n" + "void\n" + "void foo (size_t bar)\n"
				+ "<p>some function level html\n" + ";";

		RegFunctionDescription descr = RegFunctionDescription
				.read(new BufferedLineReader(new StringReader(str)));

		assertEquals(1, descr.getParameterCount());
	}

	@Test
	public void paramCountVoid() throws Exception
	{
		String str = "foo\n" + "void\n" + "void foo ()\n"
				+ "<p>some function level html\n" + ";";

		RegFunctionDescription descr = RegFunctionDescription
				.read(new BufferedLineReader(new StringReader(str)));

		assertEquals(0, descr.getParameterCount());
	}
	
	@Test
	public void paramCount() throws Exception
	{
		String str = "foo\n" + "void\n" + "void foo (size_t bar, size_t foobar)\n"
				+ "<p>some function level html\n" + ";";

		RegFunctionDescription descr = RegFunctionDescription
				.read(new BufferedLineReader(new StringReader(str)));

		assertEquals(2, descr.getParameterCount());
	}
	
	@Test
	public void paramName() throws Exception
	{
		String str = "foo\n" + "void\n" + "void foo (size_t bar, size_t foobar)\n"
				+ "<p>some function level html\n" + ";";

		RegFunctionDescription descr = RegFunctionDescription
				.read(new BufferedLineReader(new StringReader(str)));

		assertEquals("size_t bar", descr.getParameterName(0));
		assertEquals("size_t foobar", descr.getParameterName(1));
	}
}
