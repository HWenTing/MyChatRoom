
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
/**
 * 登录界面窗口
 * 实现用户登录功能
 * @author dinghao
 *
 */
public class CatLogin extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JLabel lblNewLabel;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	public static HashMap<String, ClientBean> onlines;
	boolean loginCheck = false;
	boolean exist = false;
	boolean valid = false;
	boolean regsuccess = false;
	// MySQL URL declaration
	final String URL = "jdbc:mysql://localhost:3306/sys?useSSL=true";
	final String USERNAME = "root";
	final String PASSWORD = "123456";
	private Connection conn = null;
	private Statement state = null;
	
	/**
	 * Launch the application.
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// 启动登陆界面
					CatLogin frame = new CatLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CatLogin() {
		
		setTitle("聊天室登录界面");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(472, 235, 967, 681);

		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override//铺设背景图片
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(
						"images/图片1.png").getImage(), 0,
						0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//用户名输入框
		textField = new JTextField();
		textField.setFont(new Font( "Serif", 1, 25));//设置字体
		textField.setBounds(197, 376, 190, 50);
		textField.setOpaque(false);//此方法是设置控件是否透明的。true表示不透明，false表示透明。文本区域设为透明
		contentPane.add(textField);
		textField.setColumns(10);
		
		//密码输入框
		passwordField = new JPasswordField();
		passwordField.setFont(new Font( "Serif", 1, 25));//设置字体
		passwordField.setEchoChar('*');//设置显示字符
		passwordField.setOpaque(false);//文本区域设为透明
		passwordField.setBounds(197, 464, 190, 50);
		contentPane.add(passwordField);
		
		//登录按钮
		btnNewButton = new JButton();
		btnNewButton.setIcon(new ImageIcon("images\\登录1.png"));//设置图标背景
		btnNewButton.setBounds(459, 537, 108, 54);
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//设置鼠标在该区域显示状态变化
		getRootPane().setDefaultButton(btnNewButton);//设置回车响应
		contentPane.add(btnNewButton);
		
		//注册按钮
		btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\图片2.png"));//设置图标背景
		btnNewButton_1.setBounds(773, 276, 111, 56);
		btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//设置鼠标在该区域显示状态变化
		contentPane.add(btnNewButton_1);

		// 提示信息
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(220, 521, 190, 30);
		lblNewLabel.setFont(new Font("Dialog", 1,  15));
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);

		// 监听登陆按钮
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			String u_name = textField.getText();//获取用户输入的用户名	
			String u_pwd = new String(passwordField.getPassword());//获取用户输入的密码
			try {
				//调用用户登录方法
				LoginDatabase(u_name, u_pwd);
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
				
			}
		});

		//注册按钮监听
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton_1.setEnabled(false);
				CatResign frame = new CatResign();
				frame.setVisible(true);// 显示注册界面
				setVisible(false);// 隐藏掉登陆界面
				
			}
		});
	}
/**
 * 登录数据库方法，传入用户名和用户密码
 * @param u_name 
 * @param u_pwd
 * @throws SQLException
 */
	protected void LoginDatabase(String u_name, String u_pwd) throws SQLException {
		ResultSet result = null;
		try {
			// 连接到MySQL
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			state = conn.createStatement();
			// 从MySQL中读取数据
			result = state.executeQuery("SELECT * FROM test WHERE `name` =" + "'" + u_name + "'");
			// 检查用户名是否存在
			if (result.next()) {
				// 进行数据比对
				if (result.getString(1).equals(u_name)) {
					if (result.getString(2).equals(u_pwd)) {
						
						try {
							Socket client = new Socket("localhost", 8520);//建立端口号连接

							btnNewButton.setEnabled(false);
							CatChatroom frame = new CatChatroom(u_name,
									client);
							frame.setVisible(true);// 显示聊天界面
							this.setVisible(false);// 隐藏掉登陆界面

						} catch (UnknownHostException e1) {
							
							errorTip("The connection with the server is interrupted, please login again");
						} catch (IOException e1) {

							errorTip("The connection with the server is interrupted, please login again");
						}
						
					}else{
						//给出用户提示信息，并清空信息栏
						lblNewLabel.setText("您输入的密码有误！");
						textField.setText("");
						passwordField.setText("");
						textField.requestFocus();
					}
				}else{
					//给出用户提示信息，并清空信息栏
					lblNewLabel.setText("您输入昵称不存在！");
					textField.setText("");
					passwordField.setText("");
					textField.requestFocus();
				}
			}

		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex.getMessage());
			System.out.println("SQLState:" + ex.getSQLState());
			System.out.println("VendorError:" + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			System.out.println("Not Found:" + ex.toString());
		}finally {
			
		}
	}
	
	protected void errorTip(String str) {
		// TODO Auto-generated method stub
		//显示错误信息，清空信息栏
		JOptionPane.showMessageDialog(contentPane, str, "Error Message",
				JOptionPane.ERROR_MESSAGE);
		textField.setText("");
		passwordField.setText("");
		textField.requestFocus();
	}
}