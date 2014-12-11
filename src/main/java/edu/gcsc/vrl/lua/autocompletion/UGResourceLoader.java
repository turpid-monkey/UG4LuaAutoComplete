package edu.gcsc.vrl.lua.autocompletion;

import java.io.File;

import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;
import org.mism.forfife.res.FileResourceLoader;

public class UGResourceLoader implements LuaResourceLoader {

	File ug4Root;

	public void setUg4Root(String ug4Root) {
		File f = new File(ug4Root);
		if (!f.exists() || !new File(f, "scripts").exists())
			throw new IllegalArgumentException("Invalid UG root folder: " + f);
		this.ug4Root = f;
	}

	public File getUg4Root() {
		return ug4Root;
	}

	@Override
	public boolean canLoad(LuaResource res) {
		return res.getResourceLink().startsWith("ug:");
	}

	@Override
	public String load(LuaResource res) throws Exception {
		File file = new File(new File(ug4Root,"scripts"), res.getResourceLink().substring(3));
		return FileResourceLoader.load(file);
	}

}
