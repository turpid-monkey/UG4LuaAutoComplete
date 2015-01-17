package edu.gcsc.vrl.lua.autocompletion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.gcsc.lua.LuaParseTreeUtil;
import edu.gcsc.lua.LuaSyntaxInfo;
import edu.gcsc.lua.grammar.LuaParser.ChunkContext;

public class VisitorTests {

	@Test
	public void testUgLoadVisitor() throws Exception {
		ChunkContext ctx = LuaParseTreeUtil
				.parse("ug_load_script(\"ug_util.lua\")");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		UGLoadScriptVisitor visitor = new UGLoadScriptVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(1, info.getIncludedResources().size());
		assertEquals("ug:ug_util.lua", info.getIncludedResources().iterator()
				.next().getResourceLink());
	}
	
	@Test
	public void testUgLoadVisitorIgnored() throws Exception
	{
		ChunkContext ctx = LuaParseTreeUtil
				.parse("ug_load_script(name..\".lua\")");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		UGLoadScriptVisitor visitor = new UGLoadScriptVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(0, info.getIncludedResources().size());
	}
}
