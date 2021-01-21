import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.awt.event.ActionEvent;

public class Peer {

	private JFrame frmPiuChat;
	private JTextField txtAddress;
	private JTextField txtMessage;

	private Socket connection;
	private JLabel lblStatus;
	private JList lstMessages;
	DefaultListModel<String> listModel;

	private JComboBox cmbVehicle;
	private JLabel lblVehicle;
	private JLabel lblVehicle2;
	private Boolean isRemote;
	private Params params2;
	private Params params1;

	private String zozImg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Peer window = new Peer();
					window.frmPiuChat.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Peer() {
		initialize();
		listModel = new DefaultListModel<>();
		lstMessages.setModel(listModel);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPiuChat = new JFrame();
		frmPiuChat.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				moveVehicle(e.getX(), e.getY());
			}
		});
		frmPiuChat.setTitle("PIU Chat");
		frmPiuChat.setBounds(100, 100, 446, 800);
		frmPiuChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPiuChat.getContentPane().setLayout(null);

		txtAddress = new JTextField();
		txtAddress.setBounds(27, 24, 130, 26);
		frmPiuChat.getContentPane().add(txtAddress);
		txtAddress.setColumns(10);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectToHost();
			}
		});
		btnConnect.setBounds(159, 24, 117, 29);
		frmPiuChat.getContentPane().add(btnConnect);

		JLabel lblOr = new JLabel("Or");
		lblOr.setHorizontalAlignment(SwingConstants.CENTER);
		lblOr.setBounds(273, 29, 29, 16);
		frmPiuChat.getContentPane().add(lblOr);

		JButton btnHost = new JButton("Host");
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				hostANetwork();

			}
		});
		btnHost.setBounds(300, 24, 117, 29);
		frmPiuChat.getContentPane().add(btnHost);

		lstMessages = new JList();
		lstMessages.setBounds(27, 113, 390, 180);
		frmPiuChat.getContentPane().add(lstMessages);

		lblStatus = new JLabel("Status: none");
		lblStatus.setBounds(27, 69, 249, 16);
		frmPiuChat.getContentPane().add(lblStatus);

		cmbVehicle = new JComboBox();
		cmbVehicle.setModel(new DefaultComboBoxModel(new String[] { "Car", "Motorbike" }));
		cmbVehicle.setSelectedIndex(0);
		cmbVehicle.setBounds(27, 300, 120, 27);
		cmbVehicle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeVehicle();
			}
		});
		frmPiuChat.getContentPane().add(cmbVehicle);

		lblVehicle = new JLabel("");
		lblVehicle.setIcon(new ImageIcon("img/car.png"));
		lblVehicle.setBounds(27, 340, 71, 32);
		frmPiuChat.getContentPane().add(lblVehicle);

		lblVehicle2 = new JLabel("");
		lblVehicle2.setIcon(new ImageIcon("img/car.png"));
		params2 = new Params("27", "340", "Car");
		params1 = new Params("27", "340", "Car");
		lblVehicle2.setBounds(27, 340, 71, 32);
		frmPiuChat.getContentPane().add(lblVehicle2);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartRemote();
			}
		});
		btnStart.setBounds(300, 300, 117, 29);
		frmPiuChat.getContentPane().add(btnStart);
	}

	private void changeVehicle() {
		String vehicle = cmbVehicle.getSelectedItem().toString();
		params1.setImgName(vehicle);

		if (vehicle.equals("Car")) {
			lblVehicle.setIcon(new ImageIcon("img/car.png"));
		} else {
			lblVehicle.setIcon(new ImageIcon("img/motorbike.png"));
		}
	}

	private void moveVehicle(int x, int y) {
		lblVehicle.setBounds(x, y, lblVehicle.getWidth(), lblVehicle.getHeight());
		String iX = Integer.toString(x);
		String iY = Integer.toString(y);
		params1.setXY(iX, iY);
	}

	private void moveVehicle2(Params params) {
		Integer x = params.getX();
		Integer y = params.getY();

		lblVehicle2.setBounds(x, y, lblVehicle.getWidth(), lblVehicle.getHeight());
	}

	private void changeVehicle2(Params params) {
		String oldImgName = params2.getImgName();
		String newImgName = params.getImgName();
		if (oldImgName.equals(newImgName)) {
			return;
		} else {
			if (newImgName.equals("Car")) {
				lblVehicle2.setIcon(new ImageIcon("img/car.png"));
			} else {
				lblVehicle2.setIcon(new ImageIcon("img/motorbike.png"));
			}
		}
	}

	private void hostANetwork() {
		Thread thread = new NetworkHostingThread();
		thread.start();
	}

	private void connectToHost() {

		Thread thread = new ConnectionThread();
		thread.start();

	}

	private void StartRemote() {
		isRemote = true;
		Thread thread = new StartRemoteThread();
		thread.start();
	}

	private void StopRemote() {
		isRemote = false;
		// Thread thread = new StopRemoteThread();
		// thread.start();
	}

	private class NetworkHostingThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				ServerSocket serverSocket = new ServerSocket(9999);
				lblStatus.setText("Wait...");
				connection = serverSocket.accept();
				lblStatus.setText("Connected");
				Thread thread = new ChatReaderThread();
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ConnectionThread extends Thread {
		@Override
		public void run() {
			super.run();

			String hostAddress = txtAddress.getText();
			try {
				lblStatus.setText("Connecting ...");
				connection = new Socket(hostAddress, 9999);
				if (connection.isConnected()) {
					lblStatus.setText("Connected to ");
				}
				Thread thread = new ChatReaderThread();
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ChatReaderThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				while (true) {
					InputStream inputStream = connection.getInputStream();
					Scanner scanner = new Scanner(inputStream);
					while (true) {
						String rawString = scanner.nextLine();
						listModel.addElement(rawString);
						Params params = Params.fromRawString(rawString);
						params1 = params;
						changeVehicle2(params);
						moveVehicle2(params);
						System.out.println("[ChatReaderThread] Received : " + params.toRawString());
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private class StartRemoteThread extends Thread {
		@Override
		public void run() {
			super.run();
			Integer n = 0;
			try {
				while (isRemote) {
					OutputStream outputStream = connection.getOutputStream();
					PrintWriter printWriter = new PrintWriter(outputStream);
					String rawParams = params1.toRawString();
					printWriter.write(rawParams + "\n");
					printWriter.flush();
					n++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
