/***************************************************************
*	Gamer Edit v 1.0 beta
*	Programmers: DemonX & AshuGoDhoom
*	You're free to modify and redistribute Gamer Edit,
*	though Gamer Edit is not released under a GPL license.
*	Lol.. I am talking as if its a great project.. 
*	Take a chill pill fella, bang the Code..
*                 LOL :P : P :P
***************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.datatransfer.*;


public class GamerEdit extends JFrame implements ActionListener
{
	JMenu fopen, edit, build, help;
	JMenuItem newfile, open, save, saveas, exit;
	JMenuItem cut, copy, paste, undo, redo;
	JMenuItem compile, run;
	JMenuItem helptopics, about;
	JTextArea codetextarea;
	JPanel codepanel;
	JMenuBar menubar;
	JScrollPane codepane;
	File fileopen, filesave, workingfile;
	String content, defaultcontent = null, filename;
	String modified_flag = null;
	Clipboard clipboard_data;
	public boolean filemodified = false;
	public boolean fileexists = false;
	
	public GamerEdit()
	{
		// Create the JFrame, set icon, set size and default properties.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //Do Nothing on Close and Allow us to handle it through a WindowEvent
		// Make the application call the gamerEditExit function on WindowEvent, windowClosing
		WindowAdapter w = new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				gamerEditExit();
			}
		};
		this.addWindowListener(w);
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent key)
			{
				System.out.println("Hah ha");
			}
		});
		// Set Position in screen
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		final int WIDTH = toolkit.getScreenSize().width;
		final int HEIGHT = toolkit.getScreenSize().height;		
		final int APP_WIDTH = WIDTH / 2;
		final int APP_HEIGHT = HEIGHT * 2 / 3;
		setLocation((WIDTH - APP_WIDTH)/2, (HEIGHT - APP_HEIGHT)/2);
		setSize(APP_WIDTH, APP_HEIGHT);
		
		setResizable(true);
		setIconImage(getToolkit().getImage("icons/icon.png"));
		setTitle("GamerEdit : A Simple Text Editor");
		
		// Get the System Clipboard Data
		clipboard_data = getToolkit().getSystemClipboard();
		
		// Design of GUI using swing components
		fopen = new JMenu("Open");
		edit = new JMenu("Edit");
		build = new JMenu("Build");
		help = new JMenu("Help");
		
		// Open Menu
		newfile = new JMenuItem("New File", new ImageIcon("icons/newfile.png"));
		fopen.add(newfile);
		open = new JMenuItem("Open", new ImageIcon("icons/open.png"));
		fopen.add(open);
		save = new JMenuItem("Save", new ImageIcon("icons/save.png"));
		fopen.add(save);
		saveas = new JMenuItem("Save As..", new ImageIcon("icons/saveas.png"));
		fopen.add(saveas);
		exit = new JMenuItem("Exit", new ImageIcon("icons/exit.png"));
		fopen.add(exit);
		
		// Edit Menu
		cut = new JMenuItem("Cut", new ImageIcon("icons/cut.png"));
		edit.add(cut);
		copy = new JMenuItem("Copy", new ImageIcon("icons/copy.png"));
		edit.add(copy);
		paste = new JMenuItem("Paste", new ImageIcon("icons/paste.png"));
		edit.add(paste);
		/* Undo, Redo - (Will be added in later Versions)
		undo = new JMenuItem("Undo", new ImageIcon("icons/undo.png"));
		edit.add(undo);
		redo = new JMenuItem("Redo", new ImageIcon("icons/redo.png"));
		edit.add(redo);  */
		
		// Build Menu
		compile = new JMenuItem("Compile", new ImageIcon("icons/compile.png"));
		build.add(compile);
		run = new JMenuItem("Run", new ImageIcon("icons/run.png"));
		build.add(run);
		
		// Help Menu
		helptopics = new JMenuItem("Help Topics", new ImageIcon("icons/help.png"));
		help.add(helptopics);
		about = new JMenuItem("About GamerEdit", new ImageIcon("icons/about.png"));
		help.add(about);
		
		
		menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        menubar.add(fopen);
        menubar.add(edit);
        menubar.add(build);
        menubar.add(help);
        
        // Dividing the GUI into panels :)
        codepanel = new JPanel();
        codepanel.setLayout(new GridLayout(1,1));
        codepanel.setBackground(Color.white);
        getContentPane().add(codepanel);
        codetextarea = new JTextArea();
        codetextarea.setEditable(true);
        codetextarea.addKeyListener(new MyKeyListener());
        codepane = new JScrollPane(codetextarea);
        //Scroll Properties
        codepane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        codepane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);  //Word-Wrap Settings
        codepanel.add(codepane);
      
        this.setVisible(true);
        
        // Listeners
        newfile.addActionListener(this);
        open.addActionListener(this);        
        save.addActionListener(this);        
        saveas.addActionListener(this);
        exit.addActionListener(this);
        
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        
        
	} // End of Constructor
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		// Start Action Handler for New File
		if(source == newfile)
		{
			if(filemodified == false)
				initializeNewFile();
			if(filemodified == true)
			{
				String msg = "Do you want to save changes to the file: " + filename + "?";
				if(filename == null)
					msg = "Do you want to save the Unnamed file?";
				int reply = JOptionPane.showConfirmDialog(null, msg);
				if(reply == JOptionPane.YES_OPTION)
				{
					content = codetextarea.getText();
					if(fileexists == false)
					{
						JFileChooser fs = new JFileChooser();
						fs.setDialogType(JFileChooser.SAVE_DIALOG);
						fs.setDialogTitle("Save File");
						if(fs.showSaveDialog(GamerEdit.this) == JFileChooser.APPROVE_OPTION)
						{
							filesave = fs.getSelectedFile();
							if(filesave.exists())
							{
								if(JOptionPane.showConfirmDialog(null, "File already exists! Replace?") == JOptionPane.YES_OPTION)
								{
									writeFile(filesave, content);
									initializeNewFile();
								}
							}
							else
							{
								writeFile(filesave, content);
								initializeNewFile();
							}
						}
					}
					else
					{
						writeFile(workingfile, content);
						initializeNewFile();
					}
				}
				if(reply == JOptionPane.NO_OPTION)
					initializeNewFile();
			}
		}
		// End ActionListener For New File
		
		
		//  Start ActionHandler For Open
		if(source == open)
		{
			if(filemodified == false)
			{
				gamerEditOpenFile();
			}
			else
			{
				String msg = "Do you want to save changes to the file: " + filename + "?";
				if(filename == null)
					msg = "Do you want to save the Unnamed file?";
				int reply = JOptionPane.showConfirmDialog(null, msg);
				if(reply == JOptionPane.YES_OPTION)
				{
					content = codetextarea.getText();
					if(fileexists == false)
					{
						JFileChooser fs = new JFileChooser();
						fs.setDialogType(JFileChooser.SAVE_DIALOG);
						fs.setDialogTitle("Save File");
						if(fs.showSaveDialog(GamerEdit.this) == JFileChooser.APPROVE_OPTION)
						{
							filesave = fs.getSelectedFile();
							if(filesave.exists())
							{
								if(JOptionPane.showConfirmDialog(null, "File already exists! Replace?") == JOptionPane.YES_OPTION)
								{
									writeFile(filesave, content);
									this.gamerEditOpenFile();
								}
							}
							else
							{
								writeFile(filesave, content);
								this.gamerEditOpenFile();
							}
						}
					}
					else
					{
						writeFile(workingfile, content);
						this.gamerEditOpenFile();
					}
				}
				if(reply == JOptionPane.NO_OPTION)
					this.gamerEditOpenFile();
			}
		}
		// End ActionHandler For Open
		
		// Start ActionHandler For Save
		if(source == save)
		{
			content = codetextarea.getText();
			if(fileexists == false)
			{
				JFileChooser fs = new JFileChooser();
				fs.setDialogType(JFileChooser.SAVE_DIALOG);
				fs.setDialogTitle("Save File");
				if(fs.showSaveDialog(GamerEdit.this) == JFileChooser.APPROVE_OPTION)
				{
					filesave = fs.getSelectedFile();
					workingfile = filesave;
	            	filename = workingfile.getName();
					if(filesave.exists())
					{
						if(JOptionPane.showConfirmDialog(null, "File already exists! Replace?") == JOptionPane.YES_OPTION)
						{
							writeFile(filesave, content);	
							fileexists = true;
							defaultcontent = content;
							filemodified = false;
							setStarFlag(false);
						}
					}
					else
					{
						writeFile(filesave, content);
						fileexists = true;
						defaultcontent = content;
						filemodified = false;
						setStarFlag(false);
					}
				}
			}
			else
			{
				writeFile(workingfile, content);
				fileexists = true;
				defaultcontent = content;
				filemodified = false;
				setStarFlag(false);
				
			}
		}
		// Start ActionHandler For Save
		
		// Start ActionHandler For SaveAs
		if(source == saveas)
		{
			JFileChooser fs = new JFileChooser();
			fs.setDialogType(JFileChooser.SAVE_DIALOG);
			fs.setDialogTitle("Save File As..");
			if(fs.showSaveDialog(GamerEdit.this) == JFileChooser.APPROVE_OPTION)
			{
				filesave = fs.getSelectedFile();
				workingfile = filesave;
            	filename = workingfile.getName();
				if(filesave.exists())
				{
					if(JOptionPane.showConfirmDialog(null, "File already exists! Replace?") == JOptionPane.YES_OPTION)
					{
						writeFile(filesave, content);	
						fileexists = true;
						defaultcontent = content;
						filemodified = false;
						setStarFlag(false);
					}
				}
				else
				{
					writeFile(filesave, content);
					fileexists = true;
					defaultcontent = content;
					filemodified = false;
					setStarFlag(false);
				}
			}
		}
		// End ActionHandler For SaveAs
		
		// Start ActionHandler For Exit
		if(source == exit)
		{
			gamerEditExit();
		}
		// End ActionHandler for Exit
		
		if(source == cut)
		{
			String selected_text = codetextarea.getSelectedText();
			StringSelection cutdata = new StringSelection(selected_text);
			clipboard_data.setContents(cutdata, cutdata);
			codetextarea.replaceSelection("");
			filemodified = true;
			setStarFlag(true);
		}
		
		if(source == copy)
		{
			String selected_text = codetextarea.getSelectedText();
			StringSelection copydata = new StringSelection(selected_text);
			clipboard_data.setContents(copydata, copydata);
		}
		
		if(source ==  paste)
		{
			Transferable clipboard_text_transferable = clipboard_data.getContents(clipboard_data);
			try 
			{
				String clipboard_text = (String)clipboard_text_transferable.getTransferData(DataFlavor.stringFlavor);
				codetextarea.replaceSelection(clipboard_text);
			} 
			catch (UnsupportedFlavorException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public class MyKeyListener extends KeyAdapter
	{
		public void keyTyped(KeyEvent ke)
		{
			if(codetextarea.getText() != defaultcontent)
			{
				if(!ke.isControlDown())
				{
					filemodified = true;
					setStarFlag(true);
				}
			}
		}
	}

	

	public String readFile(File f)
	{
		StringBuffer strbuffer;
		String filecontent = null;
		String linestring;
		try
		{
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			strbuffer = new StringBuffer();
			while((linestring = br.readLine()) != null)
			{
				strbuffer.append(linestring + "\n");
			}
			fr.close();
			filecontent = strbuffer.toString();
		}
		catch (IOException io)
		{
			codetextarea.setText(io.getClass().getName() + ": " + io.getMessage());
		}
		return filecontent;
	}
	
	public boolean writeFile (File file, String fcontent)
	{
		try 
		{
	        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	        pw.print (fcontent);
	        pw.flush ();
	        pw.close ();
	    }
	    catch (IOException io) 
	    {
	        codetextarea.setText(io.getClass().getName() + ": " + io.getMessage());
	    }
	    return true;
	}
	public void gamerEditExit()
	{
		if(filemodified == false)
			GamerEdit.this.dispose();
		if(filemodified == true)
		{
			String msg = "Do you want to save changes to the file: " + filename + "?";
			if(filename == null)
				msg = "Do you want to save the Unnamed file?";
			int reply = JOptionPane.showConfirmDialog(null, msg);
			if(reply == JOptionPane.YES_OPTION)
			{
				content = codetextarea.getText();
				if(fileexists == false)
				{
					JFileChooser fs = new JFileChooser();
					fs.setDialogType(JFileChooser.SAVE_DIALOG);
					fs.setDialogTitle("Save File");
					if(fs.showSaveDialog(GamerEdit.this) == JFileChooser.APPROVE_OPTION)
					{
						filesave = fs.getSelectedFile();
						if(filesave.exists())
						{
							if(JOptionPane.showConfirmDialog(null, "File already exists! Replace?") == JOptionPane.YES_OPTION)
							{
								writeFile(filesave, content);
								GamerEdit.this.dispose();
							}
						}
						else
						{
							writeFile(filesave, content);
							GamerEdit.this.dispose();
						}
					}
				}
				else
				{
					writeFile(workingfile, content);
					GamerEdit.this.dispose();
				}
			}
			if(reply == JOptionPane.NO_OPTION)
				GamerEdit.this.dispose();
		}
	}
	
	public boolean initializeNewFile()
	{
		codetextarea.setText("");
		workingfile = null;
		defaultcontent = null;
		filemodified = false;
		setStarFlag(false);
		fileexists = false;
		filename = null;
		GamerEdit.this.setTitle("GamerEdit : A Simple Text Editor");
		return true;
		
	}
	public void gamerEditOpenFile()
	{
		JFileChooser fo = new JFileChooser();
        fo.setDialogType(JFileChooser.OPEN_DIALOG);
        fo.setDialogTitle("Open File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Java, PHP, C, C++ and Text Files", "txt", "php", "c", "cpp", "java");
		fo.setFileFilter(filter);
        int file_chooser_return = fo.showOpenDialog(GamerEdit.this);
        if(file_chooser_return == JFileChooser.APPROVE_OPTION) // File Opens Successfully
        {
        	fileopen = fo.getSelectedFile();
        	workingfile = fileopen;
        	filename = workingfile.getName();
        	fileexists = true;
        	String fcontent = readFile(fileopen);
            codetextarea.setText(fcontent);
            defaultcontent = fcontent;
            filemodified = false;
            setStarFlag(false);
            GamerEdit.this.setTitle(fileopen.getName() + " - Gamer Edit");
            codetextarea.setCaretPosition(0);
        }
	}
	
	public void setStarFlag(boolean starflag)
	{
		if(starflag == true)
		{
			if(workingfile == null)
			{
				GamerEdit.this.setTitle("Unnamed* : GamerEdit");
			}
			else
			{
				GamerEdit.this.setTitle(workingfile.getName() + "* : Gamer Edit");
			}
		}
		else
		{
			if(workingfile == null)
			{
				GamerEdit.this.setTitle("Unnamed : GamerEdit");
			}
			else
			{
				GamerEdit.this.setTitle(workingfile.getName() + ": Gamer Edit");
			}
		}
	}
	
	public static void main(String args[])
	{
		new GamerEdit();
	}
}

