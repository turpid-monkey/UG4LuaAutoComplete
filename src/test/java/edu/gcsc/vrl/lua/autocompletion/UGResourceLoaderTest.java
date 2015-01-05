package edu.gcsc.vrl.lua.autocompletion;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.junit.Test;
import org.mism.forfife.LuaResource;

import static org.junit.Assert.*;

public class UGResourceLoaderTest {

	@Test
	public void setUG4RootTest() {
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		scripts.deleteOnExit();

		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		assertEquals(System.getProperty("java.io.tmpdir"), UGResourceLoader
				.getUg4Root().getAbsolutePath() + "/");
		scripts.delete();
	}

	@Test
	public void loadResource() throws Exception {
		UGResourceLoader loader = new UGResourceLoader();
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		scripts.deleteOnExit();
		File temp = new File(scripts, "temp.txt");
		temp.deleteOnExit();

		Writer out = new FileWriter(temp);
		out.write("test");
		out.close();
		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		loader.setResource(new LuaResource("ug:" + temp.getName()));
		assertEquals("test", loader.load().trim());
		temp.delete();
		scripts.delete();
	}

	@Test
	public void hasModifications() throws Exception {
		UGResourceLoader loader = new UGResourceLoader();
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		scripts.deleteOnExit();
		File temp = new File(scripts, "temp.txt");
		temp.deleteOnExit();

		Writer out = new FileWriter(temp);
		out.write("test");
		out.close();
		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		loader.setResource(new LuaResource("ug:" + temp.getName()));
		assertEquals(true, loader.hasModifications());
		loader.load();
		assertEquals(true, temp.setLastModified(temp.lastModified() + 1));

		assertEquals(false, loader.hasModifications());
		temp.delete();
		scripts.delete();
	}

	@Test
	public void search() throws Exception {
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		scripts.deleteOnExit();

		File temp = new File(scripts, "temp.lua");
		temp.deleteOnExit();

		Writer out = new FileWriter(temp);
		out.write("test");
		out.close();

		List<File> files = UGResourceLoader.search(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
		}, scripts);

		temp.delete();
		scripts.delete();
		assertEquals(1, files.size());
		assertEquals("temp.lua", files.get(0).getName());
	}

	@Test
	public void createUGLoadScriptCompletions() throws Exception {
		File scripts = new File(System.getProperty("java.io.tmpdir"), "scripts");
		scripts.mkdir();
		scripts.deleteOnExit();

		UGResourceLoader.setUg4Root(System.getProperty("java.io.tmpdir"));
		UGResourceLoader.setCurrentDir(System.getProperty("java.io.tmpdir"));

		File temp = new File(scripts, "temp.lua");
		temp.deleteOnExit();

		Writer out = new FileWriter(temp);
		out.write("test");
		out.close();

		List<Completion> completions = UGResourceLoader
				.createUGLoadScriptCompletions(null);
		temp.delete();
		scripts.delete();
		assertEquals(2, completions.size());
		assertEquals("ug_load_script(\"temp.lua\")", completions.get(0)
				.getReplacementText());
	}
}
