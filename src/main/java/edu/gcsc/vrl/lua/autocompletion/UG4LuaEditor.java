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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mism.forfife.LuaFoldParser;

import edu.gcsc.vrl.StateFile;

/**
 *
 * @author tr1nergy
 */
public class UG4LuaEditor implements ActionListener {

	JMenuItem open, save, ug4CompletionTxt, ug4Root;
	JFrame frame;
	JFileChooser fileChooser;
	RSyntaxTextArea textArea;
	RTextScrollPane pane;
	StateFile<UG4EditorProfile> profile = new StateFile<UG4EditorProfile>(
			UG4EditorProfile.class);
	FileNameExtensionFilter luaFilefilter = new FileNameExtensionFilter(
			"LUA Source files", "lua");
	UG4LuaAutoCompletionProvider prov;

	private void createSwingContent() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(luaFilefilter);

		frame = new JFrame("UG 4 LUA Editor V0.1a");
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent paramWindowEvent) {
				profile.save();
			}

		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display the window.

		FoldParserManager.get().addFoldParserMapping(
				SyntaxConstants.SYNTAX_STYLE_LUA, new LuaFoldParser());
		textArea = new RSyntaxTextArea(40, 80);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);

		prov = new UG4LuaAutoCompletionProvider();
		try {
			prov.loadUg4CompletionsTxt(profile.getState().getUg4CompletionTxt());
		} catch (Exception e) {
			System.out
					.println("Settings for ugCompletion.txt are missing or contain errors: "
							+ e.getMessage());
		}

		try {
			prov.setUg4Root(profile.getState().getUg4Root());
		} catch (Exception e) {
			System.out
					.println("Settings for UG_ROOT are missing or contain errors: "
							+ e.getMessage());
		}
		AutoCompletion ac = new AutoCompletion(prov);
		ac.setShowDescWindow(true);
		ac.install(textArea);
		ac.setListCellRenderer(new CompletionCellRenderer());

		pane = new RTextScrollPane(textArea);
		pane.setFoldIndicatorEnabled(true);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		open = new JMenuItem("Open...");
		save = new JMenuItem("Save");
		ug4CompletionTxt = new JMenuItem("Set ugCompletion.txt");
		ug4Root = new JMenuItem("Set UG root folder");
		menu.add(open);
		menu.add(save);
		menu.addSeparator();
		menu.add(ug4CompletionTxt);
		menu.add(ug4Root);
		open.addActionListener(this);
		save.addActionListener(this);
		ug4CompletionTxt.addActionListener(this);
		ug4Root.addActionListener(this);
		frame.add(menuBar, BorderLayout.NORTH);

		frame.add(pane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				postInit();
			}
		});
	}

	private void postInit() {
		if (profile.getState().getLastFile() != null) {
			File file = new File(profile.getState().getLastFile());
			load(file);
			fileChooser.setSelectedFile(file);
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new UG4LuaEditor().createSwingContent();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		if (src == open) {

			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				load(fileChooser.getSelectedFile());
			}
		}
		if (src == save) {
			int returnVal = fileChooser.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				save(fileChooser.getSelectedFile());
			}
		}
		if (src == ug4CompletionTxt) {
			FileNameExtensionFilter ff = new FileNameExtensionFilter(
					"UG4 completion text format", "txt");
			fileChooser.setFileFilter(ff);
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					prov.loadUg4CompletionsTxt(fileChooser.getSelectedFile()
							.getAbsolutePath());
					profile.getState().setUg4CompletionTxt(
							fileChooser.getSelectedFile().getAbsolutePath());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							frame,
							"Could not load or parse selected file: "
									+ e.getMessage());
				}

			}
			fileChooser.setFileFilter(luaFilefilter);
		}
		if (src == ug4Root) {
			JFileChooser dirChooser = new JFileChooser();
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = dirChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String file = dirChooser.getSelectedFile().getAbsolutePath();
				try {
					prov.setUg4Root(file);
					profile.getState().setUg4Root(file);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame,
							"Could not set UG_ROOT: " + e.getMessage());
				}

			}
		}
	}

	void load(File file) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			StringWriter str = new StringWriter();
			PrintWriter out = new PrintWriter(str);
			String line;
			while ((line = in.readLine()) != null) {
				out.println(line);
			}
			in.close();
			textArea.setText(str.toString());
			str.close();
			frame.setTitle(file.getName());
			profile.getState().setLastFile(file.getAbsolutePath());
			prov.setCurrentDir(file.getParent());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Could not load file.\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	void save(File file) {
		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			out.print(textArea.getText());
			out.close();
			frame.setTitle(file.getName());
			profile.getState().setLastFile(file.getAbsolutePath());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Could not save file.\n" + e.getMessage());
			e.printStackTrace();
		}
	}
}
