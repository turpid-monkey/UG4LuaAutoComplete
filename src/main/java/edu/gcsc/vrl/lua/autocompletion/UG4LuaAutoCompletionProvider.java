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
}
