package ChatServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener{
	
	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	private JButton start_btn = new JButton("��������");
	private JButton stop_btn = new JButton("��������");
	//��Ʈ��ũ�ڿ�
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector user_vector = new Vector();
	private Vector room_vector = new Vector();

	private StringTokenizer st;
	
	Server()//������
	{
		init();
		start();
	}
	
	private void start()
	{
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	//ȭ�鱸��
	private void init()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 288, 316);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 248, 167);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		
		
		scrollPane.setViewportView(textArea);
		
		JLabel label = new JLabel("��Ʈ��ȣ");
		label.setBounds(12, 200, 57, 15);
		contentPane.add(label);
		
		port_tf = new JTextField();
		port_tf.setBounds(82, 197, 116, 21);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		
		start_btn.setBounds(12, 245, 97, 23);
		contentPane.add(start_btn);
		
		
		stop_btn.setBounds(149, 245, 97, 23);
		contentPane.add(stop_btn);
		stop_btn.setEnabled(false);
		
		this.setVisible(true);
	}
	
	private void Server_start()
	{
		try{
			server_socket = new ServerSocket(port);
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "�̹� ������� ��Ʈ", "�˸�", JOptionPane.ERROR_MESSAGE);
		}//12345��Ʈ���
		
		if(server_socket != null)//���������� ��Ʈ�� ������ ���
		{
			Connection();
		}
	}
	
	private void Connection()
	{
		
		
		//1������ �����忡���� 1������ �ϸ� ó��
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {// �����忡�� ó�������� ����	
				
				while(true)
					{
					try {
						textArea.append("��������� �����\n");
						socket = server_socket.accept();//��������� ���
						textArea.append("���������\n");
						
						UserInfo user = new UserInfo(socket);
						
						user.start();//��ü�� ���������
						
					} catch (IOException e) {
						
						break;
					}
				}//while����
				
			}
		});
		
		th.start();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Server();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==start_btn)
		{
			System.out.println("��ŸƮ��ưŬ��");
			port = Integer.parseInt(port_tf.getText().trim());
			
			Server_start();//���ϻ����� ��������Ӵ��
			
			start_btn.setEnabled(false);
			port_tf.setEditable(false);
			stop_btn.setEnabled(true);
			
		}
		else if(e.getSource()==stop_btn)
		{
			
			start_btn.setEnabled(true);
			port_tf.setEditable(true);
			stop_btn.setEnabled(false);
			//�����ʱ�ȭ
			try {
				server_socket.close();
				user_vector.removeAllElements();
				room_vector.removeAllElements();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			System.out.println("������ž��ưŬ��");
		}
		
	}//�׼� �̺�Ʈ ��
	
	class UserInfo extends Thread
	{
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private Socket user_socket;
		private String Nickname = "";
		
		private boolean RoomCh = true;
		
		UserInfo(Socket soc) //������ �޼ҵ�
		{
			this.user_socket = soc;
			
			UserNetwork();
			
		}
		
		private void UserNetwork()//����ũ �ڿ� ����
		{
			try{
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			Nickname = dis.readUTF();//������Ǵг����� ����
			textArea.append(Nickname+" : ���������");
			
			//��������ڵ鿡�� ���ο� ����ھ˸�
			System.out.println("��������ڼ� : " +user_vector.size());
			
			Broadcast("NewUser/"+Nickname);
			
			
			
			
			//�ڽſ��� ��������ڸ� �˸�
			for(int i=0; i<user_vector.size(); i++)
			{
				UserInfo u = (UserInfo)user_vector.elementAt(i);
				
				Send_message("OldUser/"+u.Nickname);
			}
			
			//�ڽſ��� ���� �� ����� �޾ƿ��ºκ�
			for(int i=0; i<room_vector.size(); i++)
			{
				RoomInfo r = (RoomInfo)room_vector.elementAt(i);
				
				Send_message("OldRoom/"+r.Room_name);
			}
			
			user_vector.add(this);//����ڿ��Ծ˸� �� Vector�� �ڽ��� �߰�
			
			}
			catch(IOException e){
				JOptionPane.showMessageDialog(null, "Stream���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
			

		}
		
		public void run()//Thread���� ó���� ����
		{
			while(true)
			{
				try {
					String msg = dis.readUTF();//�޽�������
					textArea.append(Nickname+" : ����ڷκ��͵��� �޽��� : "+msg);
					InMessage(msg);
					
				} catch (IOException e) {
					textArea.append(Nickname+"��������� ������\n");
					try{ 
						dos.close();
						dis.close();
						user_socket.close();
						user_vector.remove(this);
						Broadcast("User_out/"+Nickname);
					}
					catch(IOException e1){
						break;
					}
					
				}
			}
				
		}//run�޼ҵ� ��
		
		private void InMessage(String str)//Ŭ���̾�Ʈ�κ��� ���� �޽���ó��
		{
			st = new StringTokenizer(str,"/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("�������� : "+protocol);
			System.out.println("�޽��� : "+ message);
			
			if(protocol.equals("Note"))
			{
				//protocol = note(�޴³���)
				//message = user2@~~~~~~
				
				String note = st.nextToken();
				
	
				System.out.println("�������� : "+note);
				
				//���Ϳ��� �ش����ڸ� ã�� �޽��� ����
				
				for(int i=0; i<user_vector.size(); i++)
				{
					
					UserInfo u = (UserInfo)user_vector.elementAt(i);
					
					if(u.Nickname.equals(message))
					{
						u.Send_message("Note/"+Nickname+"/"+note);
						//ex = Note/User2@ ~~~~~~~
					}
						
				}
	
			}
			else if(protocol.equals("CreateRoom"))
			{
				//���� ���� ���� �����ϴ��� Ȯ��
				
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					
					if(r.Room_name.equals(message))//��������ϴ� ���� �̹� ������ ��
					{
						Send_message("CreateRoomFail/ok");
						RoomCh = false;
						break;
					}
					
				}//for��
				
				if(RoomCh)//���������������
				{
					RoomInfo new_room = new RoomInfo(message, this);
					room_vector.add(new_room);//��ü�溤���� ���� �߰�
					
					Send_message("CreateRoom/"+message);
					
					Broadcast("New_Room/"+message);
					
				}
				
				RoomCh = true;
				
			}//else if�� ��
			
			else if(protocol.equals("Chatting"))
			{
				String msg = st.nextToken();
				
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					
					if(r.Room_name.equals(message))//�ش���� ã������
					{
						r.BroadCast_Room("Chatting/"+Nickname+"/"+msg);
					}
					
				}
			
			}//else if�� ��
			else if(protocol.equals("JoinRoom"))
			{
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					if(r.Room_name.equals(message))
					{
						//������ �������� ���ο� �����ھ˸�
						
						r.BroadCast_Room("Chatting/ /*******"+Nickname+"���� �����ϼ̽��ϴ�.********");
						
						//������߰�
						r.Add_User(this);
						Send_message("JoinRoom/"+message);
					}
				}
			}
			
			
		}
		
		private void Send_message(String str)//���ڿ����޾Ƽ� ����
		{
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void Broadcast(String str)//��ü����ڿ��� �޼��� ������ �κ�
		{
			for(int i=0; i<user_vector.size(); i++)
			{
				UserInfo u = (UserInfo)user_vector.elementAt(i);
				
				u.Send_message(str);
			}
		}
		
	}//UserInfo class��
	
	class RoomInfo
	{
		private String Room_name;
		private Vector Room_user_vector = new Vector();
		
		public RoomInfo(String str, UserInfo u) 
		{
			this.Room_name = str;
			this.Room_user_vector.add(u);
			
		}
		
		public void BroadCast_Room(String str)//���� ���� ��� ������� �˸���.
		{
			
			for(int i=0; i<Room_user_vector.size(); i++)
			{
				UserInfo u = (UserInfo)Room_user_vector.elementAt(i);
				
				u.Send_message(str);
			}
			
		}
		
		private void Add_User(UserInfo u)
		{
			this.Room_user_vector.add(u);
		}
		
	}
	

}
