package ch14;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ChatClientAWT3 extends MFrame implements ActionListener, Runnable {

	Button bt1, bt2, bt3, bt4;
	TextField tf1, tf2, tf3;
	TextArea area;
	List list;
	Socket sock;
	BufferedReader in;
	PrintWriter out;
	String title = "ChatClient 3.0";
	String listTitle = "*******대화자명단*******";
	boolean flag = false;
	String id;
	String label[] = {"SAVE", "MESSAGE", "SEND"};
	
	public ChatClientAWT3(BufferedReader in, PrintWriter out, String id) {
		super(450, 500);
		this.in = in;
		this.out = out;
		this.id = id;
		setTitle(title + "-" + id + "님 반갑습니다.");
		area = new TextArea("ChatClient 3.0");
		area.setBackground(Color.DARK_GRAY);
		area.setForeground(Color.PINK);
		area.setEditable(false);
		add(BorderLayout.CENTER, area);
		// /////////////////////////////////////////////////////////////////////////////////////////
		Panel p2 = new Panel();
		p2.setLayout(new BorderLayout());
		list = new List();
		list.add(listTitle);
		p2.add(BorderLayout.CENTER, list);
		Panel p3 = new Panel();
		p3.setLayout(new GridLayout(1, 2));
		bt2 = new Button("Save");
		bt2.addActionListener(this);
		bt3 = new Button("Message");
		bt3.addActionListener(this);
		p3.add(bt2);
		p3.add(bt3);
		p2.add(BorderLayout.SOUTH, p3);
		add(BorderLayout.EAST, p2);
		// ///////////////////////////////////////////////////////////////////////////////////////////
		Panel p4 = new Panel();
		tf3 = new TextField("", 50);
		tf3.addActionListener(this);
		bt4 = new Button("send");
		bt4.addActionListener(this);
		p4.add(tf3);
		p4.add(bt4);
		add(BorderLayout.SOUTH, p4);
		new Thread(this).start();
		validate();
	}

	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				else
					routine(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// --run

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == bt2/* save */) {
			String content = area.getText();
			long fileName = System.currentTimeMillis();
			try {
				FileWriter fw = new FileWriter("ch14/" + fileName + ".txt");
				fw.write(content);
				fw.close();
				area.setText("");
				new MDialog(this, "Save", "대화내용을 저장하였습니다.");
			} catch (Exception e2) {
				// TODO: handle exception
			}
		} else if (obj == bt3/* message */) {
			int i = list.getSelectedIndex();
			if (i == -1 || i == 0) {
				new MDialog(this, "경고", "아이디를 선택하세요.");
			} else {
				new Message("TO:");
			}
		} else if (obj == bt4/* send */ || obj == tf3) {
			String str = tf3.getText();
			if (filterMgr(str)) {
				new MDialog(this, "경고", "입력하신 글자는 금지어입니다.");
				return; // 메소드 중간에 빠져나가는 결과. 즉 밑에 소스는 의미가 없어짐.
			}

			int i = list.getSelectedIndex();
			if (i == -1 || i == 0) { // 전체 채팅
				sendMessage(ChatProtocol3.CHATALL + ":" + str);
			} else {// 귀속말 채팅
				String id = list.getSelectedItem();
				sendMessage(ChatProtocol3.CHAT + ":" + id + ";" + str);
			}
			tf3.setText("");
			tf3.requestFocus();

		}

	}// --actionPerformed

	public void routine(String line) {
		int idx = line.indexOf(":");
		String cmd = line.substring(0, idx);
		String data = line.substring(idx + 1);
		if (cmd.equals(ChatProtocol3.CHATLIST)) {
			list.removeAll();
			list.add(listTitle);
			StringTokenizer st = new StringTokenizer(data, ";");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
		} else if (cmd.equals(ChatProtocol3.CHAT) || cmd.equals(ChatProtocol3.CHATALL)) {
			area.append(data + "\n");
		} else if (cmd.equals(ChatProtocol3.MESSAGE)) {
			idx = data.indexOf("l");
			cmd = data.substring(0, idx);
			data = data.substring(idx);
			new Message("FROM", cmd, data);
		}
	}// --routine

	public void sendMessage(String msg) {
		out.println(msg);
	}

	public boolean filterMgr(String msg) {
		boolean flag = false;// false이면 금지어 아님
		String str[] = { "바보", "개새끼", "새끼", "자바", "java" };
		StringTokenizer st = new StringTokenizer(msg); // 생략하면 구분자는 공백
		String msgs[] = new String[st.countTokens()];
		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = st.nextToken();
		}
		for (int i = 0; i < str.length; i++) {
			if (flag)
				break; // 첫번째 for문 빠져나감.
			for (int j = 0; j < msgs.length; j++) {
				if (str[i].equalsIgnoreCase(msgs[j])) {
					flag = true;
					break; // 두번째 for문 빠져나감.
				} // if
			} // for2
		} // for1
		return flag;
	}

	class Message extends Frame implements ActionListener {

		Button send, close;
		TextField name;
		TextArea ta;
		String mode;// to/from
		String id;

		public Message(String mode) {
			setTitle("쪽지보내기");
			this.mode = mode;
			id = list.getSelectedItem();
			layset("");
			validate();
		}

		public Message(String mode, String id, String msg) {
			setTitle("쪽지읽기");
			this.mode = mode;
			this.id = id;
			layset(msg);
			validate();
		}

		public void layset(String msg) {
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			Panel p1 = new Panel();
			p1.add(new Label(mode, Label.CENTER));
			name = new TextField(id, 20);
			p1.add(name);
			add(BorderLayout.NORTH, p1);

			ta = new TextArea("");
			add(BorderLayout.CENTER, ta);
			ta.setText(msg);
			Panel p2 = new Panel();
			if (mode.equals("TO:")) {
				p2.add(send = new Button("send"));
				send.addActionListener(this);
			}
			p2.add(close = new Button("close"));
			close.addActionListener(this);
			add(BorderLayout.SOUTH, p2);

			setBounds(200, 200, 250, 250);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == send) {
				sendMessage(ChatProtocol3.MESSAGE + ":" + id + ";" + ta.getText());
			}
			setVisible(false);
			dispose();
		}
	}

	class MDialog extends Dialog implements ActionListener {

		Button ok;
		ChatClientAWT3 ct2;

		public MDialog(ChatClientAWT3 ct2, String title, String msg) {
			super(ct2, title, true);
			this.ct2 = ct2;
			//////////////////////////////////////////////////////////////////////////////////////////
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			/////////////////////////////////////////////////////////////////////////////////////////
			setLayout(new GridLayout(2, 1));
			Label label = new Label(msg, Label.CENTER);
			add(label);
			add(ok = new Button("확인"));
			ok.addActionListener(this);
			layset();
			setVisible(true);
			validate();
		}

		public void layset() {
			int x = ct2.getX();
			int y = ct2.getY();
			int w = ct2.getWidth();
			int h = ct2.getHeight();
			int w1 = 150;
			int h1 = 100;
			setBounds(x + w / 2 - w1 / 2, y + h / 2 - h1 / 2, 200, 100);
		}

		public void actionPerformed(ActionEvent e) {
			tf3.setText("");
			dispose();
		}
	}

}
