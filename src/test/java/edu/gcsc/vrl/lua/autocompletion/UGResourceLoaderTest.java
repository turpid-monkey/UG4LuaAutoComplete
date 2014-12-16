package edu.gcsc.vrl.lua.autocompletion;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.junit.Test;
import org.mism.forfife.LuaResource;

import static org.junit.Assert.*;

public class UGResourceLoaderTest {

	@Test
	public void setUG4RootTest() {
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		assertEquals(System.getProperty("java.io.tmpdir"), UGResourceLoader.getUg4Root()
				.getAbsolutePath() + "/");
		scripts.delete();
	}

	@Test
	public void loadResource() throws Exception {
		UGResourceLoader loader = new UGResourceLoader();
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		File temp = new File(scripts, "temp.txt");
		Writer out = new FileWriter(temp);
		out.write("test");
		out.close();
		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		loader.setResource(new LuaResource("ug:" + temp.getName()));
		assertEquals("test", loader.load().trim());
		temp.delete();
		scripts.delete();
	}

}
