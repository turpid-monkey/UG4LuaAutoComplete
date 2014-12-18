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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.mism.forfife.CaretInfo;
import org.mism.forfife.IconLib;
import org.mism.forfife.LuaCompletionProvider;
import org.mism.forfife.LuaResourceLoader;
import org.mism.forfife.visitors.LuaCompletionVisitor;

public class UG4LuaAutoCompletionProvider extends LuaCompletionProvider {

	UG4CompletionsLoader ug4loader = new UG4CompletionsLoader();
	List<Completion> staticCompletions = new ArrayList<Completion>();

	public void setUg4Root(String ug4Root) {
		UGResourceLoader.setUg4Root(ug4Root);
	}

	public void setCurrentDir(String dir) {
		UGResourceLoader.setCurrentDir(dir);
	}

	protected void loadUg4CompletionsTxt(String file) {

		try {
			System.out.println("Loading UG4 completions file: " + file);
			ug4loader.load(new FileInputStream(file));
			for (RegFunctionDescription fd : ug4loader.getFunctions()) {
				FunctionCompletion fc = new FunctionCompletion(this,
						fd.getName(), fd.getReturntype());
				fc.setShortDescription(fd.getHtml());
				//fc.setShortDescription(fd.getSignature());
				fc.setRelevance(2000);
				fc.setIcon(IconLib.instance().getLibraryIcon());
				staticCompletions.add(fc);
			}
		} catch (Exception e) {
			System.out.println("Loading UG4 completions file failed: "
					+ e.getMessage());
		}
	}

	@Override
	protected void fillResourceLoaders(
			List<Class<? extends LuaResourceLoader>> loaders) {
		super.fillResourceLoaders(loaders);
		loaders.add(UGResourceLoader.class);
	}

	@Override
	protected void fillVisitors(List<LuaCompletionVisitor> visitors) {
		visitors.add(new UGLoadScriptVisitor());
		super.fillVisitors(visitors);
	}

	@Override
	protected void fillCompletions(List<Completion> completions,
			String alreadyEntered, CaretInfo info) {
		super.fillCompletions(completions, alreadyEntered, info);
		completions.addAll(staticCompletions);
		for (String var : getTypeMap().keySet()) {
			if (var.startsWith(alreadyEntered) || alreadyEntered.startsWith(var)) {
				String type = getTypeMap().get(var);
				if (ug4loader.getClasses().containsKey(type)) {
					RegClassDescription cd = ug4loader.getClasses().get(type);
					List<RegFunctionDescription> fds = new ArrayList<RegFunctionDescription>(
							cd.getMemberfunctions());
					for (RegClassDescription parent : cd.getClassHierachy()) {
						if (parent == null)
							System.out
									.println("Broken type hierarchy in class description for "
											+ cd.getName());
						else
							fds.addAll(parent.getMemberfunctions());
					}
					for (RegFunctionDescription fd : fds) {
						FunctionCompletion fc = new FunctionCompletion(this,
								var + ":" + fd.getName(), fd.getReturntype());
						fc.setShortDescription(cd.getName() + ":"
								+ fd.getSignature());
						fc.setSummary(fd.getHtml());
						fc.setRelevance(8000);
						fc.setIcon(IconLib.instance().getMemberFunctionIcon());
						completions.add(fc);
					}
				}
			}
		}
	}
}
