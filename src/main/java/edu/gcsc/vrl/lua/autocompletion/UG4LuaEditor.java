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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mism.forfife.LuaFoldParser;

/**
 *
 * @author tr1nergy
 */
public class UG4LuaEditor implements ActionListener {
	
	JMenuItem open, save;
	JFrame frame;
	JFileChooser fileChooser;
	RSyntaxTextArea textArea;

    private void createSwingContent() {
    	fileChooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "LUA Source files", "lua");
	    fileChooser.setFileFilter(filter);
    	
        frame = new JFrame("UG 4 LUA Editor V0.1a");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        JPanel cp = new JPanel(new BorderLayout());

        FoldParserManager.get().addFoldParserMapping(SyntaxConstants.SYNTAX_STYLE_LUA, new LuaFoldParser());
        textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setPreferredSize(new Dimension(400,200));
        
        UG4LuaAutoCompletionProvider prov = new UG4LuaAutoCompletionProvider();
        AutoCompletion ac = new AutoCompletion(prov);
        ac.setShowDescWindow(true);
        ac.install(textArea);

        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setFoldIndicatorEnabled(true);
        cp.add(sp);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        open = new JMenuItem("Open...");
        save = new JMenuItem("Save");
        menu.add(open);
        menu.add(save);
        open.addActionListener(this);
        save.addActionListener(this);
        frame.add(menuBar, BorderLayout.NORTH);

        frame.add(cp);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param args the command line arguments
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
		if (src==open)
		{
			
		    int returnVal = fileChooser.showOpenDialog(frame);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileChooser.getSelectedFile())));
				    StringWriter str = new StringWriter();
				    PrintWriter out = new PrintWriter(str);
				    String line;
		    		while ((line = in.readLine())!=null)
		    		{
		    			out.println(line);
		    		}
		    		in.close();
		    		textArea.setText(str.toString());
		    		str.close();
		    		frame.setTitle(fileChooser.getSelectedFile().getName());
		    	}
		    	catch (Exception e)
		    	{
		    		JOptionPane.showMessageDialog(frame,
			   			    "Could not load file.\n" + e.getMessage());
			    	   e.printStackTrace();
		    	}
		       }
		}
		if (src==save)
		{
			int returnVal = fileChooser.showSaveDialog(frame);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       try {
		    	   PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileChooser.getSelectedFile())));
		           out.print(textArea.getText());
		           out.close();
		           frame.setTitle(fileChooser.getSelectedFile().getName());
		       } catch (Exception e)
		       {
		    	   JOptionPane.showMessageDialog(frame,
		   			    "Could not save file.\n" + e.getMessage());
		    	   e.printStackTrace();
		       }
		    }
		}
	}
}

