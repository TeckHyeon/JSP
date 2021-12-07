package ch14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer2 {

	ServerSocket server;
	Vector<ClientThread2> vc;
	int port = 8002;

	public ChatServer2() {
		try {
			server = new ServerSocket(port);
			vc = new Vector<ClientThread2>();
		} catch (Exception e) {
			System.err.println("Error in Server");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("************ Server 2. 0 ************");
		System.out.println("클라이언트 접속을 기다리고 있습니다.");
		System.out.println("*************************************");
		try {
			while (true) {
				Socket sock = server.accept();
				ClientThread2 ct = new ClientThread2(sock);
				ct.start();
				vc.addElement(ct);
			}
		} catch (Exception e) {
			System.err.println("Error in Socket");
			e.printStackTrace();
		}
	}

	public void sendAllMessage(String msg) {
		for (int i = 0; i < vc.size(); i++) {
			// Vector에 있는 ClientThread1를 순차적으로 가져옴
			ClientThread2 ct = vc.get(i);
			// ClintThread1 가지고 있는 메시지 전송 메소드 호출
			ct.sendMessage(msg);
		}
	}

	public void removeClient(ClientThread2 ct) {
		vc.remove(ct);
	}

	class ClientThread2 extends Thread {

		Socket sock;
		BufferedReader in;
		PrintWriter out;
		String id = "익명";

		public ClientThread2(Socket sock) {
			try {
				this.sock = sock;
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(), true/* auto flush */);
				System.out.println(sock + "접속됨.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				// Client에게 최초 보내는 메시지
				out.println("사용하실 아이디를 입력하세요.");
				while (true) {
					String line = in.readLine();
					if (line == null)
						break;
					else
						routine(line);
				}
			} catch (Exception e) {
				// client 연결이 끊어질 떄
				// Vector에서 자신의 객체를 제거
				removeClient(this);
				System.err.println(sock + "[" + id + "]");
//				e.printStackTrace();
			}
		}

		// Client로부터 요청된 문자열 분석 메소드
		public void routine(String line) {
			System.out.println("line : " + line);
			// CHATALL:오늘은 월요일입니다.
			int idx = line.indexOf(":");
			String cmd = line.substring(0, idx);
			String data = line.substring(idx + 1);
			if (cmd.equals(ChatProtocol2.ID)) {
				// data = aaa
				if (data != null && data.length() > 0) {
					id = data;
					// 새로운 접속자가 추가 되었기 때문에 리스트 재전송
					sendAllMessage(ChatProtocol2.CHATLIST + ":" + getids());
					// 모든 접속자에게 입장 메시지 전송
					sendAllMessage(ChatProtocol2.CHATALL + ":" + "[" + id + "]님이 입장하였습니다.");
				}
			} else if (cmd.equals(ChatProtocol2.CHAT)) {

				idx = data.indexOf("l");
				
				cmd = data.substring(0, idx);
				
				data = data.substring(idx);
				
				ClientThread2 ct = findClient(cmd);
				if (ct != null) {
					ct.sendMessage(ChatProtocol2.CHAT + ":" + "[" + id + "(S)]" + data);
				} else
					sendMessage(ChatProtocol2.CHAT + ":" + "[" + cmd + "] 접속자가 아닙니다.");

			} else if (cmd.equals(ChatProtocol2.MESSAGE)) {
				
				idx = data.indexOf("l");
				
				cmd = data.substring(0, idx);
				
				data = data.substring(idx);
				
				ClientThread2 ct = findClient(cmd);
				if (ct != null) {
					ct.sendMessage(ChatProtocol2.MESSAGE + ":" + "[" + id + "(S)]" + data);
				} else
					sendMessage(ChatProtocol2.MESSAGE + ":" + "[" + cmd + "] 접속자가 아닙니다.");
				
			} else if (cmd.equals(ChatProtocol2.CHATALL)) {
				sendAllMessage(ChatProtocol2.CHATALL+":"+"["+id+"]"+data);
			}
		}

		// 매개변수로 받은 id값으로 ClientThread2를 검색
		public ClientThread2 findClient(String id) {
			ClientThread2 ct = null;
			for (int i = 0; i < vc.size(); i++) {
				ct = vc.get(i);
				if (id.equals(ct.id))
					break;
			}
			return ct;
		}

		public String getids() {
			String ids = "";
			for (int i = 0; i < vc.size(); i++) {
				ClientThread2 ct = vc.get(i);
				ids += ct.id + ";";
			}
			return ids;
		}

		public void sendMessage(String msg) {
			out.println(msg);
		}

	}

	public static void main(String[] args) {
		new ChatServer2();
	}
}
