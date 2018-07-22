package chatdemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	
	boolean started = false;
	ServerSocket ss = null;
	List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public void start() {
		try {
			ss = new ServerSocket(8888);
			started = true;
		}catch (BindException e) {
			System.out.println("端口使用中。。。请退出相关程序并重新运行服务器！");
			System.exit(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			while (started) {
				Socket socket = ss.accept();
System.out.println("a client connected");
				Client client = new Client(socket);
				if (clients != null) {
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.send(client.socket.getInetAddress() + ":" + client.socket.getPort() + "加入聊天");
					}
				}
				clients.add(client);
				new Thread(client).start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class Client implements Runnable{
		Socket socket = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		boolean connected = false;
		
		public Client(Socket socket) {
			this.socket = socket;
			connected = true;
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				while (connected) {
					String string = dis.readUTF();
System.out.println(string);
					for (int i = 0; i < clients.size(); i++) {
						Client client = clients.get(i);
						client.send(this.socket.getInetAddress() + ":" + this.socket.getPort() + "\n" + string);
					}
				}
			}catch (EOFException e) {
				System.out.println("client disconnect");
				clients.remove(this);
				for (int i = 0; i < clients.size(); i++) {
					Client client = clients.get(i);
					client.send(this.socket.getInetAddress() + ":" + this.socket.getPort() + "退出聊天");
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(dis != null) dis.close();
					if(dos != null) dos.close();
					if(socket != null) socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
