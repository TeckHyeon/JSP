package ch14;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class LoginAWT3 extends MFrame implements ActionListener {

	TextField idTx, pwTx;
	Label logo, idl, pwl, msgl;
	Button logBtn;
	Socket sock;
	BufferedReader in;
	PrintWriter out;
	String id;
	String label[] = {"ID와 PASS을 입력하세요.",
			"ID와 PASS를 확인하세요.",
			"이중 접속입니다."};
	public LoginAWT3() {
		super(450, 400, new Color(100, 200, 100));
		setLayout(null);
		logo = new Label("MyChat3.0");
		logo.setFont(new Font("Dialog", Font.BOLD, 50));

		idl = new Label("ID");
		pwl = new Label("PASS");
		idTx = new TextField("aaa");
		pwTx = new TextField("1234");
		logBtn = new Button("로그인");
		msgl = new Label("ID와 PASS을 입력하세요.");
		logo.setBounds(100, 50, 250, 100);
		idl.setBounds(150, 200, 50, 20);
		idTx.setBounds(200, 200, 100, 20);
		pwl.setBounds(150, 230, 50, 20);
		pwTx.setBounds(200, 230, 100, 20);
		logBtn.setBounds(150, 260, 150, 40);
		msgl.setBounds(150, 320, 150, 40);
		logBtn.addActionListener(this);
		add(logo);
		add(idl);
		add(idTx);
		add(pwl);
		add(pwTx);
		add(logBtn);
		add(msgl);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		try {
			if (obj == logBtn) {
				if (sock == null) {
					sock = new Socket("127.0.0.1", 8003);
					in = new BufferedReader(new InputStreamReader(
							sock.getInputStream()));
					out = new PrintWriter(sock.getOutputStream(), true);
				}
				id = idTx.getText();
				out.println(ChatProtocol3.ID + ":" + id + ";"
						+ pwTx.getText());
				String line = in.readLine();
				int idx = line.indexOf(':');
				String cmd = line.substring(0, idx);
				String data = line.substring(idx + 1);
				if (cmd.equals(ChatProtocol3.ID)) {
					if (data.equals("F")) {
						msgl.setForeground(Color.red);
						msgl.setText(label[1]);
					}else if(data.equals("C")) {
						msgl.setForeground(Color.blue);
						msgl.setText(label[2]);
						} else if (data.equals("T")) {
						dispose();
//						setTitle("로그인 성공");
						new ChatClientAWT3(in, out, id);
					}
				} 
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LoginAWT3();
	}
}
