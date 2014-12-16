package edu.gcsc.vrl.lua.autocompletion;

import java.io.File;

import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;
import org.mism.forfife.res.FileResourceLoader;

public class UGResourceLoader implements LuaResourceLoader {

	static File ug4Root;
	static File currentDir;

	LuaResource resource;
	long lastModified;
	File file;

	@Override
	public void setResource(LuaResource resource) {
		this.resource = resource;
		String fileName = resource
				.getResourceLink().substring(3);
		file = new File(new File(ug4Root, "scripts"), fileName);
		if (!file.exists())
		{
			file = new File(currentDir, fileName);
		}
		lastModified = file.lastModified();
	}
	
	@Override
	public LuaResource getResource() {
		return resource;
	}

	public static void setUg4Root(String path) {
		File f = new File(path);
		if (!f.exists() || !new File(f, "scripts").exists())
			throw new IllegalArgumentException("Invalid UG root folder: " + f);
		ug4Root = f;
	}
	
	public static void setCurrentDir(String dir)
	{
		File f = new File(dir);
		if (!f.exists() || !f.isDirectory())
			throw new IllegalArgumentException("Invalid working dir: " + f);
		currentDir = f;
		
	}

	public static File getUg4Root() {
		return ug4Root;
	}

	@Override
	public boolean canLoad() {
		return resource.getResourceLink().startsWith("ug:");
	}

	@Override
	public String load() throws Exception {
		return FileResourceLoader.load(file);
	}

	@Override
	public boolean hasModifications() {
		return lastModified < file.lastModified();
	}

}
