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

public class Peer{

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
	private String vehicleName;
	private Boolean isConnected;
	private Boolean isRemote;
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
		isConnected=false;
		isRemote=false;
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
				if(isConnected){
					moveVehicle(e.getX(), e.getY());
				}
				
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
		lblVehicle2.setBounds(27, 340, 71, 32);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isRemote = true;
			}
		});
		btnStart.setBounds(300, 300, 117, 29);
		frmPiuChat.getContentPane().add(btnStart);
	}

	private void changeVehicle() {
		String vehicle = cmbVehicle.getSelectedItem().toString();

		if (vehicle.equals("Car")) {
			lblVehicle.setIcon(new ImageIcon("img/car.png"));
			vehicleName="Car";
		} else {
			vehicleName="motorbike";
			lblVehicle.setIcon(new ImageIcon("img/motorbike.png"));
		}
	}

	private void moveVehicle(int x, int y) {
		lblVehicle.setBounds(x, y, lblVehicle.getWidth(), lblVehicle.getHeight());
		if(isConnected){
			Thread thread = new RemoteUpdateThread(x, y, vehicleName);
            thread.start();
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

	private class RemoteUpdateThread extends Thread {
        private int x;
        private int y;
        private String imgName;

        public RemoteUpdateThread(int x, int y, String imgName) {
            super();
            this.x = x;
            this.y = y;
            this.imgName = imgName;
        }

        @Override
        public void run() {
            super.run();

            try {
				if(isRemote){
					OutputStream outputStream = connection.getOutputStream();
					PrintWriter printWriter = new PrintWriter(outputStream);
					String message = x + "##" + y + "##" + imgName;
					printWriter.write(message + "\n");
					printWriter.flush();
				}
              
				
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	private class NetworkHostingThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				ServerSocket serverSocket = new ServerSocket(9999);
				lblStatus.setText("Wait...");
				while(true){
					connection = serverSocket.accept();
					lblStatus.setText("Connected");
					if(connection.isConnected()){
						isConnected=true;
						Thread thread = new RemoteReaderThread();
						thread.start();
					}
				}
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
					isConnected=true;
					Thread thread = new RemoteReaderThread();
					thread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Connection fail. " + e.getMessage());
                lblStatus.setText("Fail To connect...");
			}
		}
	}

	private class RemoteReaderThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
					InputStream inputStream = connection.getInputStream();
					Scanner scanner = new Scanner(inputStream);
					while (true) {
							String rawString = scanner.nextLine();
							listModel.addElement("Other Peer is moving");
							Params params = Params.fromRawString(rawString);
							lblVehicle2.setBounds(params.getX(),params.getY(),lblVehicle2.getWidth(), lblVehicle2.getHeight());
							
							if (params.getImgName().equals("motorbike")) {
								lblVehicle2.setIcon(new ImageIcon("img/motorbike.png"));
							} else {
								lblVehicle2.setIcon(new ImageIcon("img/car.png"));
							}
							frmPiuChat.getContentPane().add(lblVehicle2);
					}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
