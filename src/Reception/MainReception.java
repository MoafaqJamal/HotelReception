package Reception;

import static java.awt.GraphicsDevice.WindowTranslucency.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

@SuppressWarnings("unused")
public class MainReception {

	public JPanel Inframe, Outframe, Buttonframe;
	public JTextField Input;
	public static JEditorPane Output,Info;
	public JLabel Inputlabel;
	public static JScrollPane Scroll;
	public static JScrollPane ScrollInfo;
	public String dialogout = "";
	public static Vector<String> dialoghistory = new Vector<String>();
	public String displaytext = "";
	String question = "";
	String answer = "";
	Reasoner myReasoner;

	public MainReception() {               // Constructor for an Instance of SimpleGUI

		myReasoner = new Reasoner(this);         // Instantiate a "brain", reference this GUI to it
										                      		
		myReasoner.initknowledge();              // fill "the brain" with knowledge
		
		Input = new JTextField(80);	
		Inputlabel = new JLabel("Question");
		Input.setBorder(null);
		//Input.setOpaque(false);
		
		
		Output = new JEditorPane("text/html","<b>Initial text</b>");
		Output.setEditable(false);                 // no one should be able to write in the display	
		Output.setToolTipText("<html>Your dialog with the machine.</html>");
		
		Scroll = new JScrollPane(Output);          									                
		Scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		Scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Scroll.setBorder(BorderFactory.createTitledBorder("Conversation:"));
		Scroll.getViewport().setPreferredSize(new Dimension(500,600));
		Scroll.getViewport().setOpaque(false);
		Scroll.setBorder(null);
		
		Info = new JEditorPane("text/html","html string");	    
		Info.setEditable(false);  			
		Info.setText("<font face=\"Verdana\">Background information about the conversations topic will be displayed in this window.");
		//Info.setOpaque(false);
		
	    ScrollInfo = new JScrollPane(Info);   									                
		ScrollInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ScrollInfo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ScrollInfo.setBorder(BorderFactory.createTitledBorder("Extended Information:"));
	    ScrollInfo.getViewport().setPreferredSize(new Dimension(500,600));
	    ScrollInfo.getViewport().setOpaque(false);	
	    ScrollInfo.setBorder(null);
		Inframe = new JPanel();                             // Frame for the Inputelements
		Outframe = new JPanel();                            // Frame for the Outputelements
					
		Inframe.add(Inputlabel);                            // adding the elements to the JPanels
		Inframe.add(Input);
		
		Outframe.add(Scroll);
		Outframe.add(ScrollInfo);		

		JFrame Main = new JFrame("Hotel Reception");   // our main frame
		
		try {
			
    		Main.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("BG.jpg")))));
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		Main.getContentPane().setLayout(new FlowLayout());
		Main.getContentPane().add(Outframe);
		Main.getContentPane().add(Inframe);
		
				
		Main.addWindowListener(new WindowAdapter() {        // implementing listeners we need
			public void windowClosing(WindowEvent e) {      // What to do if Window is closed
				System.exit(0);                             // Kill program
			}
		});
				
		Input.addActionListener(new ActionListener() {      // Action if "Enter" is pressed
			public void actionPerformed(ActionEvent e) {    // while "Input" has the Focus
				questionasked();
				Input.setText("");
			}
		});

		Main.pack();
		Main.setSize(1200, 800);
		Main.setVisible(true);                               // Don't forget
		
		dialoghistory.add("<H2><font face=\"Verdana\">Welcome to the Hotel Reception Helpdesk, please type your question.</H2> " +
				          "<H3><font face=\"Verdana\">I can inform you about: Available Rooms, Bookings, Checkin and Checkouts " +
				          "Just ask me.</H3><br>");
		
		Output.setText(dialoghistory.firstElement());
		Input.requestFocusInWindow();
	}                                                        // Constructor done

	public void checkbrain() {

		String yo = myReasoner.testit();                     // Testing the reasoner's knowledge
		System.out.println(yo);                              // Testing the reasoner's knowledge
	}

	public void questionasked() {                            // log questions in a String vector

		question = Input.getText();
		generateanswer();
	}

	public void generateanswer() {                           // generate an answer

		Vector<String> answers = new Vector<String>();
				
		answers = myReasoner.generateAnswer(question);
	
		displaytext = "<font color=\"red\" face=\"Verdana\">You: " + answers.get(0)+
				       "</font>"+"<br><font COLOR=\"green\" face=\"Verdana\">" +
				       answers.get(1) + "</font><br>" + "<br>";
		
		dialoghistory.add(displaytext);                      // write the string to show in the
										                     // dialoghistory
	
		String dialogdisplay = "";
		
		for(int i=0; i<dialoghistory.size(); i++){
			dialogdisplay=dialogdisplay+dialoghistory.get(i);
		}
		
		Output.setText(dialogdisplay);                       // write the dialoghistory to output
		displaytext = "";                                    // clear the "long string" for the next answer
		
	}

	public void setmytooltip(String inputstring) {

		Info.setToolTipText(inputstring);
		System.out.println("Done setting the Tooltiptext");

	}
	
	public void setmyinfobox(String inputstring) {

		 try {
		       Info.setPage(inputstring);
		     }
		    catch (IOException e) {
		       Info.setContentType("text/html");
		       Info.setText("<html>Could not load"+inputstring);
		     } 
		System.out.println("Done setting the Tooltiptext");

	}

	public static void main(String[] args) {                 // main Method (starts when
												             // class/instance is called)
		MainReception mygui = new MainReception();
		mygui.checkbrain();                                  // check if brain is there and knowledge loaded

	}
}
