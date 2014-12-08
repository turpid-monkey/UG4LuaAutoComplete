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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gcsc.vrl.BufferedLineReader;

public class UG4CompletionsLoader {

	public RegClassDescription resolveClass(String name) {
		return classes.get(name);
	}

	private Map<String, RegClassDescription> classes = new HashMap<>();
	private List<RegFunctionDescription> functions = new ArrayList<>();

	public Map<String, RegClassDescription> getClasses() {
		return classes;
	}

	public List<RegFunctionDescription> getFunctions() {
		return functions;
	}

	public void load(InputStream in) throws IOException {
		load(new BufferedLineReader(new InputStreamReader(in)));
	}

	public void load(BufferedLineReader f) throws IOException {
		String line = f.readLine();
		if (!(line.equals("UG4COMPLETER VERSION 1")))
			throw new IOException("Wrong file header: " + line);
		while ((line = f.readLine()) != null) {
			if (line.equals("class")) {
				RegClassDescription regCls = RegClassDescription.read(f);
				classes.put(regCls.getName(), regCls);
			} else if (line.equals("function")) {
				functions.add(RegFunctionDescription.read(f));
			} else {
				throw new IOException("Error at line " + f.getLineCounter()
						+ ": unknown line " + line);
			}
		}
		for (RegClassDescription c : classes.values()) {
			c.setup(this);
		}
	}
}
