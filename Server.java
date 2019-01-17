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
	private JButton start_btn = new JButton("서버실행");
	private JButton stop_btn = new JButton("서버중지");
	//네트워크자원
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector user_vector = new Vector();
	private Vector room_vector = new Vector();

	private StringTokenizer st;
	
	Server()//생성자
	{
		init();
		start();
	}
	
	private void start()
	{
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	//화면구성
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
		
		JLabel label = new JLabel("포트번호");
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
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트", "알림", JOptionPane.ERROR_MESSAGE);
		}//12345포트사용
		
		if(server_socket != null)//정상적으로 포트가 열렸을 경우
		{
			Connection();
		}
	}
	
	private void Connection()
	{
		
		
		//1가지의 스레드에서는 1가지의 일만 처리
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {// 스레드에서 처리할일을 기재	
				
				while(true)
					{
					try {
						textArea.append("사용자접속 대기중\n");
						socket = server_socket.accept();//사용자접속 대기
						textArea.append("사용자접속\n");
						
						UserInfo user = new UserInfo(socket);
						
						user.start();//객체의 스레드실행
						
					} catch (IOException e) {
						
						break;
					}
				}//while문끝
				
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
			System.out.println("스타트버튼클릭");
			port = Integer.parseInt(port_tf.getText().trim());
			
			Server_start();//소켓생성및 사용자접속대기
			
			start_btn.setEnabled(false);
			port_tf.setEditable(false);
			stop_btn.setEnabled(true);
			
		}
		else if(e.getSource()==stop_btn)
		{
			
			start_btn.setEnabled(true);
			port_tf.setEditable(true);
			stop_btn.setEnabled(false);
			//서버초기화
			try {
				server_socket.close();
				user_vector.removeAllElements();
				room_vector.removeAllElements();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			System.out.println("서버스탑버튼클릭");
		}
		
	}//액션 이벤트 끝
	
	class UserInfo extends Thread
	{
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private Socket user_socket;
		private String Nickname = "";
		
		private boolean RoomCh = true;
		
		UserInfo(Socket soc) //생성자 메소드
		{
			this.user_socket = soc;
			
			UserNetwork();
			
		}
		
		private void UserNetwork()//넽워크 자원 설정
		{
			try{
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			Nickname = dis.readUTF();//사용자의닉네임을 받음
			textArea.append(Nickname+" : 사용자접속");
			
			//기존사용자들에게 새로운 사용자알림
			System.out.println("기존사용자수 : " +user_vector.size());
			
			Broadcast("NewUser/"+Nickname);
			
			
			
			
			//자신에게 기존사용자를 알림
			for(int i=0; i<user_vector.size(); i++)
			{
				UserInfo u = (UserInfo)user_vector.elementAt(i);
				
				Send_message("OldUser/"+u.Nickname);
			}
			
			//자신에게 기존 방 목록을 받아오는부분
			for(int i=0; i<room_vector.size(); i++)
			{
				RoomInfo r = (RoomInfo)room_vector.elementAt(i);
				
				Send_message("OldRoom/"+r.Room_name);
			}
			
			user_vector.add(this);//사용자에게알린 후 Vector에 자신을 추가
			
			}
			catch(IOException e){
				JOptionPane.showMessageDialog(null, "Stream설정 에러", "알림", JOptionPane.ERROR_MESSAGE);
			}
			

		}
		
		public void run()//Thread에서 처리할 내용
		{
			while(true)
			{
				try {
					String msg = dis.readUTF();//메시지수신
					textArea.append(Nickname+" : 사용자로부터들어온 메시지 : "+msg);
					InMessage(msg);
					
				} catch (IOException e) {
					textArea.append(Nickname+"사용자접속 끊어짐\n");
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
				
		}//run메소드 끝
		
		private void InMessage(String str)//클라이언트로부터 들어온 메시지처리
		{
			st = new StringTokenizer(str,"/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("프로토콜 : "+protocol);
			System.out.println("메시지 : "+ message);
			
			if(protocol.equals("Note"))
			{
				//protocol = note(받는내용)
				//message = user2@~~~~~~
				
				String note = st.nextToken();
				
	
				System.out.println("보낼내용 : "+note);
				
				//벡터에서 해당사용자를 찾아 메시지 전송
				
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
				//현재 같은 방이 존재하는지 확인
				
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					
					if(r.Room_name.equals(message))//만들고자하는 방이 이미 조재할 때
					{
						Send_message("CreateRoomFail/ok");
						RoomCh = false;
						break;
					}
					
				}//for끝
				
				if(RoomCh)//방을만들수있을때
				{
					RoomInfo new_room = new RoomInfo(message, this);
					room_vector.add(new_room);//전체방벡ㅌ에 방을 추가
					
					Send_message("CreateRoom/"+message);
					
					Broadcast("New_Room/"+message);
					
				}
				
				RoomCh = true;
				
			}//else if문 끝
			
			else if(protocol.equals("Chatting"))
			{
				String msg = st.nextToken();
				
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					
					if(r.Room_name.equals(message))//해당방을 찾았을때
					{
						r.BroadCast_Room("Chatting/"+Nickname+"/"+msg);
					}
					
				}
			
			}//else if문 끝
			else if(protocol.equals("JoinRoom"))
			{
				for(int i=0; i<room_vector.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vector.elementAt(i);
					if(r.Room_name.equals(message))
					{
						//기존방 유저에세 새로운 참여자알링
						
						r.BroadCast_Room("Chatting/ /*******"+Nickname+"님이 입장하셨습니다.********");
						
						//사용자추가
						r.Add_User(this);
						Send_message("JoinRoom/"+message);
					}
				}
			}
			
			
		}
		
		private void Send_message(String str)//문자열을받아서 전송
		{
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void Broadcast(String str)//전체사용자에게 메세지 보내는 부분
		{
			for(int i=0; i<user_vector.size(); i++)
			{
				UserInfo u = (UserInfo)user_vector.elementAt(i);
				
				u.Send_message(str);
			}
		}
		
	}//UserInfo class끝
	
	class RoomInfo
	{
		private String Room_name;
		private Vector Room_user_vector = new Vector();
		
		public RoomInfo(String str, UserInfo u) 
		{
			this.Room_name = str;
			this.Room_user_vector.add(u);
			
		}
		
		public void BroadCast_Room(String str)//현재 방의 모든 사람에게 알린다.
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
