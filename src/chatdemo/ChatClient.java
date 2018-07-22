package chatdemo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClient extends Frame {
	
	Socket socket = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	boolean connected = false;

	TextField textField = new TextField();
	TextArea textArea = new TextArea();
	
	public static void main(String[] args) {
		new ChatClient().launch();
	}
	
	public void launch() {
		setBounds(600, 250, 500, 500);
		add(textArea, BorderLayout.NORTH);
		add(textField, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
			
		});
		textField.addActionListener(new TFMonitor());
		pack();
		setVisible(true);
		connect();
		new Thread(new RescThread()).start();
	}
	
	public void connect() {
		try {
			socket = new Socket("localhost", 8888);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
System.out.println("connected");
			connected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class TFMonitor implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String string = textField.getText().trim();
//			textArea.setText(string);
			textField.setText("");
			try {
//				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(string);
				dos.flush();
//				dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	
	class RescThread implements Runnable{

		@Override
		public void run() {
			try {
				while (connected) {
					String string = dis.readUTF();
					textArea.setText(textArea.getText() + string + "\n");
				}
			} catch (SocketException e) {
				System.out.println("ÒÑÍË³ö");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
