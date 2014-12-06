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
package edu.gcsc.vrl;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Automatically loads a JAXB persisted object at initialization.
 * 
 * @author tr1nergy
 *
 * @param <State>
 *            the class of the persisted object.
 */
public class StateFile<State> {

	final protected State state;

	protected File stateFile;
	private Class<State> cls;

	public final State getState() {
		return state;
	}

	public StateFile(Class<State> cls) {
		this.cls = cls;
		this.stateFile = new File(System.getProperty("user.home"), "."
				+ cls.getSimpleName());
		state = load();
	}

	public void save() {
		try {
			JAXBContext ctx = JAXBContext.newInstance(cls);
			Marshaller marsh = ctx.createMarshaller();
			marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marsh.marshal(state, stateFile);
			System.out.println("Saving " + this.getClass().getSimpleName()
					+ " state to " + stateFile);
		} catch (JAXBException e) {
			System.out.println("Could not save "
					+ this.getClass().getSimpleName() + " state to "
					+ stateFile
					+ ". Old state might be loaded next time, or default.");
			e.printStackTrace();
		}
	}

	public State load() {
		State s;
		try {
			JAXBContext ctx = JAXBContext.newInstance(cls);
			Unmarshaller reader = ctx.createUnmarshaller();
			s = cls.cast(reader.unmarshal(stateFile));
			System.out.println("Loaded state for "
					+ this.getClass().getSimpleName() + " from " + stateFile);
		} catch (JAXBException e) {
			System.out.println("Could not load "
					+ this.getClass().getSimpleName() + " state from "
					+ stateFile + ". Using default.");
			try {
				s = cls.newInstance();
			} catch (Exception ex) {
				throw new RuntimeException(
						"Could not create default instance of class "
								+ cls.getName()
								+ ". Don't use anything else than simple beans for states.",
						e);
			}
		}
		return s;
	}

}
