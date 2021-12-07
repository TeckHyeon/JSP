package ch14;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient1 extends MFrame
implements ActionListener, Runnable{
	
	Button btn1, btn2;
	TextField tf1, tf2;
	TextArea ta;
	Panel p1, p2;
	BufferedReader in;
	PrintWriter out;
	int port = 8001;
	String id;
	
	public ChatClient1() {
		super(350,400);
		setTitle("MyChat 1.0");
		p1 = new Panel();
		p1.setBackground(new Color(100,200,100));
		p1.add(new Label("HOST ",Label.CENTER));
		p1.add(tf1 = new TextField("127.0.0.1",25));
		p1.add(btn1 = new Button("Connect"));
		
		p2 = new Panel();
		p2.setBackground(new Color(100,200,100));
		p2.add(new Label("CHAT ",Label.CENTER));
		p2.add(tf2 = new TextField("",25));
		p2.add(btn2 = new Button("SEND"));	
		
		tf1.addActionListener(this);
		tf2.addActionListener(this);
		btn1.addActionListener(this);
		btn2.addActionListener(this);
		
		add(p1,BorderLayout.NORTH);
		add(ta=new TextArea());
		add(p2,BorderLayout.SOUTH);
		validate();//����
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		이벤트를 발생시킨 이벤트 소스(컴포넌트)를 리턴
		Object obj = e.getSource();
		if(obj==tf1||obj==btn1) {
			//서버에 연결을 시도
			connect(tf1.getText().trim());
			//tf1, btn1은 비활성화
			tf1.setEnabled(false);
			btn1.setEnabled(false);
		}else if(obj==tf2||obj==btn2) {
			if(id==null) {
				id = tf2.getText();
				setTitle(getTitle() + "["+id+"]");
				ta.setText("채팅을 시작합니다.\n");
			}
//			tf2에 입력된 text를 서버에 보낸다.
			out.println(tf2.getText());
			tf2.setText("");
			tf2.requestFocus();
		}
		
	}//actionPerformed
	
	@Override
	public void run() {
		//서버로부터 전송되는 메시지를 ta에 추가
		try {
			while(true) {
			ta.append(in.readLine()+"\n");
			tf2.requestFocus();
			}
		} catch (Exception e) {
			System.err.println("Error in run");
			e.printStackTrace();
			//비정상적인 종료
			System.exit(1);
		}
	}//run
	
	public void connect(String host){
		try {
			Socket sock = new Socket(host, port);
			in = new BufferedReader(
					new InputStreamReader(
							sock.getInputStream()));
			out = new PrintWriter(
					sock.getOutputStream(), true/* auto flush */);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Thread �����Ѵ�.
/*		Thread t = new Thread(this);
		t.start();*/
		Thread t = new Thread(this);
		t.start();
	}
	
	public static void main(String[] args) {
		new ChatClient1();
	}
}
