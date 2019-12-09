package program4;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class Client extends JFrame {

	private JPanel contentPane;
	private JLabel label_serverName;
	private JLabel label_errorMessage;
	private JTextField textField_serverName;
	private JTextArea textArea_errorMessage;
	private JButton button_connect;
	
	private int portNum = 5520;
	private String serverAddress;
	private Socket socket = null;
	private BufferedInputStream input;
	private DataOutputStream out;
	
	private File file = null;
	private String filename = null;
	private String filepath = null;
	private Integer filesize = null;
	private byte[] filecontent = new byte[1024];
	private boolean serverResponse = false;
	private String serverError = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 100, 750, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// Instantiate JFileChooser
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MSword", "doc", "docx");
		fileChooser.setFileFilter(filter);
		contentPane.add(fileChooser, BorderLayout.NORTH);
		
		// Instantiate JLabels, TextFields, TextArea and button
		label_serverName = new JLabel("Server Name:");
		label_errorMessage = new JLabel("Error Messages:");
		textField_serverName = new JTextField();
		textField_serverName.setPreferredSize( new Dimension(200, 24));
		textArea_errorMessage = new JTextArea();
		textArea_errorMessage.setPreferredSize( new Dimension(200, 200));
		contentPane.add(textArea_errorMessage);
		button_connect = new JButton("Connect and Upload");
		
		// Add items to contentPane
		Applet box1 = new Applet();
		box1.add(label_serverName);
		box1.add(textField_serverName);
		box1.add(button_connect);
		contentPane.add(box1, BorderLayout.EAST);
		contentPane.add(label_errorMessage, BorderLayout.CENTER);
		contentPane.add(textArea_errorMessage, BorderLayout.SOUTH);
		
		// Get file data from FileChooser
		int returnVal = fileChooser.showOpenDialog(getParent());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			// Log file selected
			file = fileChooser.getSelectedFile();
			filename = fileChooser.getSelectedFile().getName();
			filepath = fileChooser.getSelectedFile().getPath();
			filesize = (int) fileChooser.getSelectedFile().length();
			System.out.println("File selected");
		} else {
			// Error getting File
			file = null;
			System.out.println("File not selected");
		}		
				
		// Define ActionListener
		button_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String host = textField_serverName.getText();
					socket = new Socket(host, portNum);
					out = new DataOutputStream(socket.getOutputStream());
					//Do Something
					textArea_errorMessage.append("Connected.\n");
					textArea_errorMessage.append("Sent file name: " + filename + "\n");
					textArea_errorMessage.append("Sent file length: " + filesize + "\n");
					textArea_errorMessage.append("Sending file...\n");
					sendFile();
					textArea_errorMessage.append("File sent. Waiting for the server...\n");
					//getResponse();
					if (serverResponse == true) {
						textArea_errorMessage.append("Upload O.K.\n");
					} else {
						textArea_errorMessage.append("Upload Not O.K.\n");
						textArea_errorMessage.append(serverError + "\n");
					}
					textArea_errorMessage.append("Disconnected.\n");
				}
				catch (Exception ex) {
					// Fine tune this error statement
					System.out.println( "Error: " + ex );
					socket = null; // set to null so can check if it is open
				}
			}			
		});		
	}
	
	public void sendFile() {
		String output;
		try {
			String nullTerminator = "\0";
			byte[] nullterm = nullTerminator.getBytes();
			byte[] name = filename.getBytes();
			byte[] size = filesize.toString().getBytes();
			out.write(name);
			out.write(nullterm);
			out.write(size);
			out.write(nullterm);
			
			FileInputStream fis = new FileInputStream(file);
			int offset = 0;
			while (offset < filesize) {
				fis.read(filecontent);
				out.write(filecontent);
				offset += 1024;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean getResponse() {
		boolean result = false;
		try {
			input = new BufferedInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[1];
		String currentByte;
		try {
			input.read(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentByte = buffer.toString();
		if (currentByte.contains("@")) {
			return true;
		} else {
			return false;
		}
	}

}
