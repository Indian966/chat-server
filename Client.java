package ChatClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame implements ActionListener, KeyListener{
	//�α��� GUI����
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_Pane;
	private JTextField ip_tf;// ������ �ؽ�Ʈ �ʵ�
	private JTextField port_tf;//��Ʈ �ؽ�Ʈ �ʵ�
	private JTextField id_tf;// ���̵� �ؽ�Ʈ �ʵ�
	private JButton login_btn = new JButton("����");
	
	
	//����GUI����
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_btn = new JButton("����������");
	private JButton joinroom_btn = new JButton("ä�ù� ����");
	private JButton createroom_btn = new JButton("�游���");
	private JButton send_btn = new JButton("����");
	
	private JList USer_list = new JList();//��ü������ ����Ʈ
	private JList Room_list = new JList();//���ϸ���Ʈ
	
	private JTextArea Chatt_area = new JTextArea();//ä��â����
	
	//��Ʈ��ũ������ �ڿ�����
	
	private Socket socket;
	private String ip = "";//127.0.0.1�� �ڱ��ڽ�
	private int port;
	private String id = "";
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	//�� �� ������
	Vector user_list = new Vector();
	Vector room_list = new Vector();
	StringTokenizer st;
	private String My_Room;//���� �ִ� ���̸�
	
//-------------------------------------------------------------	
	
	Client()
	{
		Login_init();//�α���â���� �޼ҵ�
		main_init();
		start();
	}
//-------------------------------------------------------------	
	private void start()
	{
		login_btn.addActionListener(this);//�α��θ�����
		notesend_btn.addActionListener(this);//�����������ư������
		joinroom_btn.addActionListener(this);//ä�ù�������ư������
		createroom_btn.addActionListener(this);//ä�ù游����ư������
		send_btn.addActionListener(this);//ä�����۹�ư������
		message_tf.addKeyListener(this);
	}
//-------------------------------------------------------------	
	private void main_init()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 586, 474);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("�� ü �� �� ��");
		label.setBounds(22, 10, 93, 15);
		contentPane.add(label);
		
		
		USer_list.setBounds(22, 35, 93, 167);
		contentPane.add(USer_list);
		
		
		notesend_btn.setBounds(18, 212, 101, 23);
		contentPane.add(notesend_btn);
		
		
		Room_list.setBounds(22, 270, 93, 101);
		contentPane.add(Room_list);
		
		JLabel lblNewLabel = new JLabel("ä�ù� ���");
		lblNewLabel.setBounds(22, 245, 93, 15);
		contentPane.add(lblNewLabel);
		
		
		joinroom_btn.setBounds(18, 381, 101, 23);
		contentPane.add(joinroom_btn);
		
		
		createroom_btn.setBounds(18, 414, 101, 23);
		contentPane.add(createroom_btn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(127, 32, 431, 339);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(Chatt_area);
		Chatt_area.setEditable(false);
		
		message_tf = new JTextField();
		message_tf.setBounds(126, 382, 366, 21);
		contentPane.add(message_tf);
		message_tf.setColumns(10);
		message_tf.setEnabled(false);
		
		send_btn.setBounds(492, 381, 66, 23);
		contentPane.add(send_btn);
		send_btn.setEnabled(false);
		
		
		
		this.setVisible(false);
	}
//-------------------------------------------------------------
	private void Login_init()
	{
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Login_GUI.setBounds(100, 100, 277, 300);
		Login_Pane = new JPanel();
		Login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_Pane);
		Login_Pane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(12, 138, 57, 15);
		Login_Pane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server port");
		lblNewLabel_1.setBounds(12, 163, 71, 15);
		Login_Pane.add(lblNewLabel_1);
		
		ip_tf = new JTextField();
		ip_tf.setBounds(93, 135, 116, 21);
		Login_Pane.add(ip_tf);
		ip_tf.setColumns(10);
		
		port_tf = new JTextField();
		port_tf.setBounds(93, 160, 116, 21);
		Login_Pane.add(port_tf);
		port_tf.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("ID");
		lblNewLabel_2.setBounds(12, 200, 57, 15);
		Login_Pane.add(lblNewLabel_2);
		
		id_tf = new JTextField();
		id_tf.setBounds(93, 197, 116, 21);
		Login_Pane.add(id_tf);
		id_tf.setColumns(10);
		
		
		login_btn.setBounds(32, 229, 177, 23);
		Login_Pane.add(login_btn);
		
		Login_GUI.setVisible(true);
	}
//---------------------------------------------------------------------
	private void Network()
	{
		try {
			socket = new Socket(ip, port);
			
			if(socket != null)
			{	
			Connection();
			}
			
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
	}
	
	private void Connection()//�������� �޼ҵ忬��κ�
	{
		try{
		is = socket.getInputStream();
		dis = new DataInputStream(is);
		
		os = socket.getOutputStream();
		dos = new DataOutputStream(os);
		}
		catch(IOException e)//����ó��
		{
			JOptionPane.showMessageDialog(null, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}//Stream������
		
		this.setVisible(true);//main uiǥ��
		this.Login_GUI.setVisible(false);;
		
		//ó�����ӽ� ���̵�����
		Send_message(id);
		
		
		//User_list�� ����� �߰�
		user_list.add(id);
		USer_list.setListData(user_list);
		
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true)
				{
					try {
						String msg = dis.readUTF();//�޽�������
						
						System.out.println("�����κ��� ���ŵ� �޽��� : "+msg);
						
						inmessage(msg);
						
					} catch (IOException e) {
						
						try{
						os.close();
						is.close();
						dos.close();
						dis.close();
						socket.close();
						JOptionPane.showMessageDialog(null, "������ ���� ������", "�˸�", JOptionPane.ERROR_MESSAGE);
						}
						catch(IOException e1){}
						break;
						
					}
					
				}
				
			}
		});
		
		th.start();

	}
	
	private void inmessage(String str)//�����κ��� ������ ��� �޽���
	{
		st = new StringTokenizer(str, "/");
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("�������� : "+protocol);
		System.out.println("���� : "+Message);
		
		
		if(protocol.equals("NewUser"))//���ο�������
		{
			user_list.add(Message);
			USer_list.setListData(user_list);
		}
		else if(protocol.equals("OldUser"))
		{
			user_list.add(Message);
			USer_list.setListData(user_list);
		}
		else if(protocol.equals("Note"))
		{

			String note = st.nextToken();
			
			System.out.println(Message+"����ڷκ��Ϳ� ����"+note);
			
			
			JOptionPane.showMessageDialog(null, note, Message+"������ ���� ����", JOptionPane.CLOSED_OPTION);
		}
		else if(protocol.equals("CreateRoom"))//���� ���������
		{
			My_Room = Message;
			message_tf.setEnabled(true);
			send_btn.setEnabled(true);
			Chatt_area.setEditable(true);
			joinroom_btn.setEnabled(false);
			createroom_btn.setEnabled(false);
		}
		else if(protocol.equals("CreateRoomFail"))//�游��� ����������
		{
			JOptionPane.showMessageDialog(null, "�� ����� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
		}
		else if(protocol.equals("New_Room"))//���ο�������������
		{
			room_list.add(Message);
			Room_list.setListData(room_list);
		}
		else if(protocol.equals("Chatting"))
		{
			String msg = st.nextToken();
			
			
			Chatt_area.append(Message+" : "+msg+"\n");
		}
		else if(protocol.equals("OldRoom"))
		{
			room_list.add(Message);
			Room_list.setListData(room_list);
		}
		else if(protocol.equals("JoinRoom"))
		{
			
			My_Room = Message;
			message_tf.setEditable(true);
			send_btn.setEnabled(true);
			joinroom_btn.setEnabled(false);
			createroom_btn.setEnabled(false);
			JOptionPane.showMessageDialog(null, "ä�ù濡 �����߽��ϴ�.", "�˸�", JOptionPane.INFORMATION_MESSAGE);
			
		}
		else if(protocol.equals("User_out"))
		{
			user_list.remove(Message);
			USer_list.setListData(user_list);
		}

		
		
	}
	
	private void Send_message(String str)//�������� �޼����� ������ �κ�
	{
		try {
			dos.writeUTF(str);
		} catch (IOException e) {//����ó��
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client();

	}
//----------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource()==login_btn)
		{
			System.out.println("�α��ι�ưŬ��");
			
			if(ip_tf.getText().length()==0)
			{
				ip_tf.setText("IP�� �Է��� �ּ���");
				ip_tf.requestFocus();
			}
			else if(port_tf.getText().length()==0)
			{
				port_tf.setText("Port��ȣ�� �Է��� �ּ���");
				port_tf.requestFocus();
			}
			else if(id_tf.getText().length()==0)
			{
				id_tf.setText("ID�� �Է��� �ּ���");
				id_tf.requestFocus();
			}
			else
			{
				ip = ip_tf.getText().trim();
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			id = id_tf.getText().trim();//���̵� �޾ƿ��� �κ�
			
			Network();
			}
			
		}
		else if(e.getSource()==notesend_btn)
		{
			System.out.println("�����������ưŬ��");
			String user = (String)USer_list.getSelectedValue();
			
			String note = JOptionPane.showInputDialog("�����޽���");
			
			if(note!=null)
			{
				Send_message("Note/"+user+"/"+note);
				//ex = Note/User2/ ���� User1�̾�
			}
			System.out.println("�޴»�� : "+user+"|�������� : "+note);
			
		}
		else if(e.getSource()==joinroom_btn)
		{
			String JoinRoom = (String)Room_list.getSelectedValue();
			Send_message("JoinRoom/"+JoinRoom);
			
			
			
			System.out.println("ä�ù�������ưŬ��");
		}
		else if(e.getSource()==createroom_btn)
		{
			String roomname = JOptionPane.showInputDialog("���̸�");
			if(roomname!=null)
			{
				Send_message("CreateRoom/"+roomname);
			}
			System.out.println("ġ�ù游����ưŬ��");
			
			
			
			
		}
		else if(e.getSource()==send_btn)
		{
			Send_message("Chatting/"+My_Room+"/"+message_tf.getText().trim());
			message_tf.setText("");
			message_tf.requestFocus();
			
			//Chatting+���̸�+����
			
			System.out.println("���۹�ưŬ��");
		}
	}
@Override
public void keyPressed(KeyEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyReleased(KeyEvent e) {
	System.out.println(e);
	if(e.getKeyCode()==10)
	{
		Send_message("Chatting/"+My_Room+"/"+message_tf.getText().trim());
	}
	message_tf.setText("");
	message_tf.requestFocus();
	// TODO Auto-generated method stub
	
}
@Override
public void keyTyped(KeyEvent e) {
	// TODO Auto-generated method stub
	
}

}

