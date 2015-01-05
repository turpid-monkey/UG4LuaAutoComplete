/*
 * Copyright (c) 2014, Goethe University, Goethe Center for Scientific Computing (GCSC), gcsc.uni-frankfurt.de
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.gcsc.vrl.lua.autocompletion;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;
import org.mism.forfife.res.FileResourceLoader;

public class UGResourceLoader implements LuaResourceLoader {

	static File ug4Root;
	static File currentDir;
	static File ug4RootScripts;

	LuaResource resource;
	long lastModified;
	File file;

	@Override
	public void setResource(LuaResource resource) {
		this.resource = resource;
		String fileName = resource.getResourceLink().substring(3);
		file = new File(ug4RootScripts, fileName);
		if (!file.exists()) {
			file = new File(currentDir, fileName);
		}
	}

	@Override
	public LuaResource getResource() {
		return resource;
	}

	public static void setUg4Root(String path) {
		File f = new File(path);
		File scripts = new File(f, "scripts");
		if (!f.exists() || !scripts.exists())
			throw new IllegalArgumentException("Invalid UG root folder: " + f);
		ug4Root = f;
		ug4RootScripts = scripts;
	}

	public static void setCurrentDir(String dir) {
		File f = new File(dir);
		if (!f.exists() || !f.isDirectory())
			throw new IllegalArgumentException("Invalid working dir: " + f);
		currentDir = f;

	}

	public static List<Completion> createUGLoadScriptCompletions(
			CompletionProvider prov) {
		List<Completion> comps = new ArrayList<Completion>();

		LuaFilenameFilter lff = new LuaFilenameFilter();
		List<File> files = search(lff, ug4RootScripts, currentDir);

		for (File f : files) {
			comps.add(new BasicCompletion(prov, "ug_load_script(\""
					+ getRelativePath(f) + "\")"));

		}

		return comps;
	}

	static String getRelativePath(File f) {
		if (f.getAbsolutePath().startsWith(ug4RootScripts.getAbsolutePath())) {
			return f.getAbsolutePath().substring(
					ug4RootScripts.getAbsolutePath().length() + 1);
		} else {
			return f.getAbsolutePath().substring(
					currentDir.getAbsolutePath().length() + 1);
		}
	}

	/**
	 * 
	 * @param ff
	 *            a filename filter implementation
	 * @param dirs
	 *            a list of directories to be searched recursively for files
	 * @return a list of matching files
	 */
	public static List<File> search(final FilenameFilter ff, final File... dirs) {
		final List<File[]> work = new LinkedList<File[]>();
		for (File dir : dirs) {
			if (!dir.isDirectory()) {
				throw new IllegalArgumentException(
						"Only directories are valid parameters, file '"
								+ dir.getAbsolutePath()
								+ "' isn't a directory.");
			}
		}
		work.add(dirs);
		final List<File> found = new LinkedList<File>();
		while (!work.isEmpty()) {
			File[] flist = work.remove(0);
			for (File f : flist) {
				if (f.isDirectory()) {
					work.add(f.listFiles(ff));
				} else {
					found.add(f);
				}
			}
		}
		return found;
	}

	public static class LuaFilenameFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith("lua") || new File(dir, name).isDirectory();
		}
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
		lastModified = file.lastModified();
		return FileResourceLoader.load(file);
	}

	@Override
	public boolean hasModifications() {
		return lastModified != file.lastModified();
	}

}
