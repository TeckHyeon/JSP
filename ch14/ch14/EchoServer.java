package ch14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
//for문 : 일반적으로 크기 정해진 것들, While문 : 일반적으로 크기가 무한인 것

	public EchoServer() {
		// java.net, java.io 대부분의 클래스 및 메소드 예외처리 - try catch
		try {
			int port = 8000;
			int cnt = 0; // Client의 접속개수
			ServerSocket server = new ServerSocket(port);
			System.out.println("ServerSocket Start");
			while(true) {
				//Client가 접속할 때까지 대기 상태
				Socket sock = server.accept(); //wait
				EchoThread et = new EchoThread(sock);
				et.start(); //결론적으로 run 메소드 호출
				cnt++;
				System.out.println("Client "+cnt+"Socket");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//내부 클래스로 Client의 대응을 각각 하기 위해 Thread 기능의 클래스 생성 (
	
	class EchoThread extends Thread{
		
		Socket sock;
		BufferedReader in;	//Client가 보내는 메세지 받는 스트림
		PrintWriter out;	//Client로 메세지 보내는 스트림
		
		public EchoThread(Socket sock) {
			try {
				this.sock = sock; // 
				in = new BufferedReader(
						new InputStreamReader(
								sock.getInputStream()));
				out = new PrintWriter(
						sock.getOutputStream(),true/*auto flush*/);		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			try {
				//Client가 접속하면 처음으로 받는 메세지
				out.println("Hello Enter BYB to exit");
				while(true) {
					//Client가 메세지 보낼 때까지 대기
					String line = in.readLine();
					if(line==null) {//Client가 접속을 끊을 때
						break;  //while문 탈출
					} else {
						//Client에서 온 메세지 앞에 Echo 붙여서 반사
						out.println("Echo : " + line);
						if(line.equalsIgnoreCase("BYB")) {
							break;
						}
					}
				} //while
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	public static void main(String[] args) {
		new EchoServer();
	}

}
