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
	//로그인 GUI변수
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_Pane;
	private JTextField ip_tf;// 아이피 텍스트 필드
	private JTextField port_tf;//포트 텍스트 필드
	private JTextField id_tf;// 아이디 텍스트 필드
	private JButton login_btn = new JButton("접속");
	
	
	//메인GUI변수
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_btn = new JButton("쪽지보내기");
	private JButton joinroom_btn = new JButton("채팅방 참여");
	private JButton createroom_btn = new JButton("방만들기");
	private JButton send_btn = new JButton("전송");
	
	private JList USer_list = new JList();//전체접속자 리스트
	private JList Room_list = new JList();//방목록리스트
	
	private JTextArea Chatt_area = new JTextArea();//채팅창변수
	
	//네트워크를위한 자원변수
	
	private Socket socket;
	private String ip = "";//127.0.0.1은 자기자신
	private int port;
	private String id = "";
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	//그 외 변수들
	Vector user_list = new Vector();
	Vector room_list = new Vector();
	StringTokenizer st;
	private String My_Room;//내가 있는 방이름
	
//-------------------------------------------------------------	
	
	Client()
	{
		Login_init();//로그인창구성 메소드
		main_init();
		start();
	}
//-------------------------------------------------------------	
	private void start()
	{
		login_btn.addActionListener(this);//로그인리스터
		notesend_btn.addActionListener(this);//쪽지보내기버튼리스너
		joinroom_btn.addActionListener(this);//채팅방참여버튼리스너
		createroom_btn.addActionListener(this);//채팅방만들기버튼리스너
		send_btn.addActionListener(this);//채팅전송버튼리스너
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
		
		JLabel label = new JLabel("전 체 접 속 자");
		label.setBounds(22, 10, 93, 15);
		contentPane.add(label);
		
		
		USer_list.setBounds(22, 35, 93, 167);
		contentPane.add(USer_list);
		
		
		notesend_btn.setBounds(18, 212, 101, 23);
		contentPane.add(notesend_btn);
		
		
		Room_list.setBounds(22, 270, 93, 101);
		contentPane.add(Room_list);
		
		JLabel lblNewLabel = new JLabel("채팅방 목록");
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
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
	}
	
	private void Connection()//실질적임 메소드연결부분
	{
		try{
		is = socket.getInputStream();
		dis = new DataInputStream(is);
		
		os = socket.getOutputStream();
		dos = new DataOutputStream(os);
		}
		catch(IOException e)//에러처리
		{
			JOptionPane.showMessageDialog(null, "연결 실패", "알림", JOptionPane.ERROR_MESSAGE);
		}//Stream설정끝
		
		this.setVisible(true);//main ui표시
		this.Login_GUI.setVisible(false);;
		
		//처음접속시 아이디전송
		Send_message(id);
		
		
		//User_list에 사용자 추가
		user_list.add(id);
		USer_list.setListData(user_list);
		
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true)
				{
					try {
						String msg = dis.readUTF();//메시지수신
						
						System.out.println("서버로부터 수신된 메시지 : "+msg);
						
						inmessage(msg);
						
					} catch (IOException e) {
						
						try{
						os.close();
						is.close();
						dos.close();
						dis.close();
						socket.close();
						JOptionPane.showMessageDialog(null, "서버와 접속 끊어짐", "알림", JOptionPane.ERROR_MESSAGE);
						}
						catch(IOException e1){}
						break;
						
					}
					
				}
				
			}
		});
		
		th.start();

	}
	
	private void inmessage(String str)//서버로부터 들어오는 모든 메시지
	{
		st = new StringTokenizer(str, "/");
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("프로토콜 : "+protocol);
		System.out.println("내용 : "+Message);
		
		
		if(protocol.equals("NewUser"))//새로운접속자
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
			
			System.out.println(Message+"사용자로부터온 쪽지"+note);
			
			
			JOptionPane.showMessageDialog(null, note, Message+"님으로 부터 쪽지", JOptionPane.CLOSED_OPTION);
		}
		else if(protocol.equals("CreateRoom"))//방을 만들었을때
		{
			My_Room = Message;
			message_tf.setEnabled(true);
			send_btn.setEnabled(true);
			Chatt_area.setEditable(true);
			joinroom_btn.setEnabled(false);
			createroom_btn.setEnabled(false);
		}
		else if(protocol.equals("CreateRoomFail"))//방만들기 실패했을때
		{
			JOptionPane.showMessageDialog(null, "방 만들기 실패", "알림", JOptionPane.ERROR_MESSAGE);
		}
		else if(protocol.equals("New_Room"))//새로운방을만들었을때
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
			JOptionPane.showMessageDialog(null, "채팅방에 입장했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			
		}
		else if(protocol.equals("User_out"))
		{
			user_list.remove(Message);
			USer_list.setListData(user_list);
		}

		
		
	}
	
	private void Send_message(String str)//서버에게 메세지를 보내는 부분
	{
		try {
			dos.writeUTF(str);
		} catch (IOException e) {//예외처리
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
			System.out.println("로그인버튼클릭");
			
			if(ip_tf.getText().length()==0)
			{
				ip_tf.setText("IP를 입력해 주세요");
				ip_tf.requestFocus();
			}
			else if(port_tf.getText().length()==0)
			{
				port_tf.setText("Port번호를 입력해 주세요");
				port_tf.requestFocus();
			}
			else if(id_tf.getText().length()==0)
			{
				id_tf.setText("ID를 입력해 주세요");
				id_tf.requestFocus();
			}
			else
			{
				ip = ip_tf.getText().trim();
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			id = id_tf.getText().trim();//아이디 받아오는 부분
			
			Network();
			}
			
		}
		else if(e.getSource()==notesend_btn)
		{
			System.out.println("쪽지보내기버튼클릭");
			String user = (String)USer_list.getSelectedValue();
			
			String note = JOptionPane.showInputDialog("보낼메시지");
			
			if(note!=null)
			{
				Send_message("Note/"+user+"/"+note);
				//ex = Note/User2/ 나는 User1이야
			}
			System.out.println("받는사람 : "+user+"|보낼내용 : "+note);
			
		}
		else if(e.getSource()==joinroom_btn)
		{
			String JoinRoom = (String)Room_list.getSelectedValue();
			Send_message("JoinRoom/"+JoinRoom);
			
			
			
			System.out.println("채팅방참여버튼클릭");
		}
		else if(e.getSource()==createroom_btn)
		{
			String roomname = JOptionPane.showInputDialog("방이름");
			if(roomname!=null)
			{
				Send_message("CreateRoom/"+roomname);
			}
			System.out.println("치팅방만들기버튼클릭");
			
			
			
			
		}
		else if(e.getSource()==send_btn)
		{
			Send_message("Chatting/"+My_Room+"/"+message_tf.getText().trim());
			message_tf.setText("");
			message_tf.requestFocus();
			
			//Chatting+방이름+내용
			
			System.out.println("전송버튼클릭");
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

