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
import java.util.ArrayList;

import edu.gcsc.vrl.BufferedLineReader;

/**
 * 
 * @author Martin Rupp
 */
public class RegClassDescription implements Comparable<RegClassDescription> {
	private String name;
	private String html;
	private String[] classHierachyStr;
	private RegClassDescription[] classHierachy;
	private ArrayList<RegFunctionDescription> memberfunctions;
	private ArrayList<RegFunctionDescription> constructors;

	static RegClassDescription read(BufferedLineReader f) throws IOException {
		RegClassDescription descr = new RegClassDescription();
		descr.memberfunctions = new ArrayList<RegFunctionDescription>();
		descr.constructors = new ArrayList<RegFunctionDescription>();

		descr.name = f.readLine();
		String line = f.readLine();
		if (line.trim().length() == 0) {
			descr.classHierachyStr = new String[0];
		} else {
			descr.classHierachyStr = line.split(" ");
		}
		descr.html = f.readLine();

		while ((line = f.readLine()) != null) {
			if (";".equals(line)) {
				return descr;
			} else if ("memberfunction".equals(line)) {
				descr.memberfunctions.add(RegFunctionDescription.read(f));
			} else if ("constructor".equals(line)) {
				descr.constructors.add(RegFunctionDescription.read(f));
			} else if ("".equals(line.trim())) {
				// let's ignore empty lines gracefully for now
			} else {
				throw new IOException("error at line " + f.getLineCounter()
						+ ": unknown line '" + line + "' in class def");
			}
		}
		throw new IOException(
				"EOF reached, class def did not complete with semicolon.");
	}

	@Override
	public int compareTo(RegClassDescription paramT) {
		return name.compareTo(paramT.name);
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String[] getClassHierachyStr() {
		return classHierachyStr;
	}

	public void setClassHierachyStr(String[] classHierachyStr) {
		this.classHierachyStr = classHierachyStr;
	}

	public RegClassDescription[] getClassHierachy() {
		return classHierachy;
	}

	public void setClassHierachy(RegClassDescription[] classHierachy) {
		this.classHierachy = classHierachy;
	}

	public ArrayList<RegFunctionDescription> getMemberfunctions() {
		return memberfunctions;
	}

	public void setMemberfunctions(
			ArrayList<RegFunctionDescription> memberfunctions) {
		this.memberfunctions = memberfunctions;
	}

	public ArrayList<RegFunctionDescription> getConstructors() {
		return constructors;
	}

	public void setConstructors(ArrayList<RegFunctionDescription> constructors) {
		this.constructors = constructors;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}