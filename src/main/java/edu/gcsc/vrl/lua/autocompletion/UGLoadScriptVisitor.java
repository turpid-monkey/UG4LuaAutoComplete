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

import static org.mism.forfife.LuaParseTreeUtil.next;
import static org.mism.forfife.LuaParseTreeUtil.start;

import org.mism.forfife.LuaResource;
import org.mism.forfife.lua.LuaParser.FunctioncallContext;
import org.mism.forfife.visitors.LuaCompletionVisitor;

public class UGLoadScriptVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitFunctioncall(FunctioncallContext ctx) {
		if (start(ctx).equals("ug_load_script")) {
			String resourceLink = "ug:"
					+ next(ctx).replace("(", "").replace(")", "")
							.replace("\"", "").trim();
			if (!resourceLink.contains("..")) {
				LuaResource res = new LuaResource(resourceLink);
				info.getIncludedResources().add(res);
			}
		}
		return super.visitFunctioncall(ctx);
	}

}
