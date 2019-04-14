import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
/**
 * ע�����
 * @author HP
 *
 */

public class CatResign extends JFrame {
	//����������
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;
	private JButton btnNewButton_1;
	private JButton btnNewButton;
	
	//���ݿ���Ϣ
	final String URL = "jdbc:mysql://localhost:3306/sys?useSSL=true";
	final String USERNAME = "root";
	final String PASSWORD = "123456";
	private Connection conn = null;
	private Statement state = null;
	private PreparedStatement ptmt = null;
/**
 * ��ʼ��ע�����
 */
	public CatResign() {
		setTitle("������ע�����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(560, 300, 700,500);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				//���ر���ͼƬ
				g.drawImage(new ImageIcon("images\\ע��ҳ��2.png").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//�û��������
		textField = new JTextField();
		textField.setBounds(260, 162, 170, 38);
		textField.setOpaque(false);//�ı�������Ϊ͸��
		contentPane.add(textField);
		textField.setColumns(10);
		
		//���������
		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);//�ı�������Ϊ͸��
		passwordField.setBounds(257, 238, 174, 37);
		contentPane.add(passwordField);
		
		//����ȷ�������
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(255, 301, 180, 41);
		passwordField_1.setOpaque(false);//�ı�������Ϊ͸��
		contentPane.add(passwordField_1);

		//ע�ᰴť
		btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\ע��1.jpg"));//��ͼ��
		btnNewButton_1.setBounds(446, 376, 86, 46);
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//��������ڸ�������ʾ״̬�仯
		contentPane.add(btnNewButton_1);

		//���ذ�ť
		btnNewButton = new JButton("");
		btnNewButton.setBounds(311, 374, 83, 48);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//��������ڸ�������ʾ״̬�仯
		contentPane.add(btnNewButton);

		//��ʾ��Ϣ
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(260, 125, 170, 38);
		lblNewLabel.setFont(new Font("SimSun", Font.BOLD, 17));
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);
		
		//���ذ�ť����
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				//���ص�½����
				CatLogin frame = new CatLogin();
				frame.setVisible(true);//��ʾ��¼����
				setVisible(false);//����ע�����
			}
		});
		
		//ע�ᰴť����
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//��ȡ�û��������Ϣ
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());
				try {
					//����ע�᷽�������û�ע��
					Registerer(u_name ,u_pwd,u_pwd_ag);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}}
		);
	}
	/**
	 * ע�᷽��
	 * @param username
	 * @param password
	 * @param confirm
	 * @throws SQLException
	 */
	protected void Registerer(String username, String password, String confirm) throws SQLException {
		ResultSet result = null;
		try {
			// ���ӵ����ݿ�
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			state = conn.createStatement();
			// Check if password is correctly set
			if (username.length() != 0) {
				//�ж�������������Ƿ�һ��
				if (password.equals(confirm)) {
					
					result = state.executeQuery("SELECT * FROM test WHERE `name` =" + "'" + username + "'");
					if (result.next()) {
						//����Ƿ��û����Ѵ���
						if (result.getString(1).equals(username)) {
							lblNewLabel.setText("�û����Ѵ��ڣ�");
						}
					} else {
						// ����Ϣд�뵽���ݿ�
						ptmt = conn.prepareStatement("INSERT INTO test ( name , keyword ) VALUES ( ? , ? )");
						ptmt.setString(1, username);
						ptmt.setString(2, password);
						int check = ptmt.executeUpdate();
						// ������ݿ�ע���Ƿ�ɹ�
						if (check > 0){
							btnNewButton_1.setEnabled(false);
							//���ص�½����
							CatLogin frame = new CatLogin();
							frame.setVisible(true);//��ʾ��¼����
							setVisible(false);//����ע�����
						}
					}
				}else{
					lblNewLabel.setText("���벻һ�£�");
					}
			}else{
				lblNewLabel.setText("���벻��Ϊ�գ�");
			}
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex.getMessage());
			System.out.println("SQLState:" + ex.getSQLState());
			System.out.println("VendorError:" + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			System.out.println("Not Found:" + ex.toString());
		}
	}
}
