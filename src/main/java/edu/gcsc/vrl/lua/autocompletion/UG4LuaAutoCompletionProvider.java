package edu.gcsc.vrl.lua.autocompletion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.mism.forfife.LuaCompletionProvider;

public class UG4LuaAutoCompletionProvider extends LuaCompletionProvider {

	List<Completion> ugCompletions;

	List<Completion> loadCompletions() {
		try {
			List<Completion> ugCompletions = new ArrayList<>();
			JarFile jar = new JarFile("lib/VRL-UG-API.jar");
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory())
					continue;
				String cls = entry.getName();
				Completion c = createCompletionFromName(cls);
				if (c != null) {
					ugCompletions.add(c);
				}
			}
			return ugCompletions;

		} catch (Exception e) {
			// throw new RuntimeException("Init LuaCompletionProvider failed.", e);
			return new ArrayList<>();
		}
	}

	Completion createCompletionFromName(String jarEntry) {
		String name = jarEntry;
		if (name.equals("MANIFEST.MF"))
			return null;
		if (name.equals("UG_Classes.groovy"))
			return null;
		if (name.contains("/")) {
			int pos = name.lastIndexOf('/') + 1;
			name = name.substring(pos);
		}
		name = name.replace(".class", "");
		if (name.startsWith("C_")) {
			name = name.substring(2);
			return new BasicCompletion(this, name, "prefix C_", jarEntry);
		} else if (name.startsWith("I_")) {
			name = name.substring(2);
			return new BasicCompletion(this, name, "prefix I_", jarEntry);
		} else if (name.startsWith("F_")) {
			name = name.substring(2);
			return new BasicCompletion(this, name, "prefix F_", jarEntry);
		} else if (name.startsWith("Const__")) {
			name = name.substring(7);
			if (name.startsWith("I_")) {
				name = name.substring(2);
				return new BasicCompletion(this, name, "prefix Const__I_", jarEntry);
			}
			return new BasicCompletion(this, name, "prefix Const__", jarEntry);
		}
		return new BasicCompletion(this, name, "class name", jarEntry);
	}

	@Override
	protected void init() {
		super.init();
		ugCompletions = loadCompletions();
	}

	@Override
	protected void initStaticCompletions() {
		super.initStaticCompletions();
		super.completions.addAll(ugCompletions);
	}

}
