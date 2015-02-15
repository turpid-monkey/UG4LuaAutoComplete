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

import edu.gcsc.vrl.BufferedLineReader;

/**
 * 
 * @author Martin Rupp
 */
public class RegFunctionDescription implements
		Comparable<RegFunctionDescription> {
	private String name;
	private String returntype;
	private String signature;
	private String html;
	private int line;

	private int _paramCount;
	private String[] _paramNames;

	public static RegFunctionDescription read(BufferedLineReader f)
			throws IOException {
		RegFunctionDescription func = new RegFunctionDescription();
		func.line = f.getLineCounter();
		func.name = f.readLine();
		func.returntype = f.readLine();
		func.signature = f.readLine();
		func.html = f.readLine();

		func._paramCount = func.parameterCount();
		func._paramNames = func.parameterNames();
		return func;
	}

	public int getLine() {
		return line;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	private int parameterCount() {
		if (signature.contains(",")) {
			return this.signature.split(",").length;
		}
		if (signature.contains("()")) {
			return 0;
		}
		return 1;
	}

	public int getParameterCount() {
		return _paramCount;
	}

	public String getParameterName(int param) {
		return _paramNames[param];
	}

	private String[] parameterNames() {
		String[] paramNames = new String[_paramCount];
		String paramList = signature.substring(signature.indexOf("(") + 1,
				signature.indexOf(")"));
		for (int i = 0; i < _paramCount; i++) {
			paramNames[i] = paramList.split(",")[i].trim();
		}
		return paramNames;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public int compareTo(RegFunctionDescription o) {
		if (this.name.equals(o.name)) {
			return signature.compareTo(o.signature);
		}
		return name.compareTo(o.name);
	}
}