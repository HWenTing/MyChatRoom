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
 * 注册界面
 * @author HP
 *
 */

public class CatResign extends JFrame {
	//界面基础组件
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;
	private JButton btnNewButton_1;
	private JButton btnNewButton;
	
	//数据库信息
	final String URL = "jdbc:mysql://localhost:3306/sys?useSSL=true";
	final String USERNAME = "root";
	final String PASSWORD = "123456";
	private Connection conn = null;
	private Statement state = null;
	private PreparedStatement ptmt = null;
/**
 * 初始化注册界面
 */
	public CatResign() {
		setTitle("聊天室注册界面");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(560, 300, 700,500);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				//加载背景图片
				g.drawImage(new ImageIcon("images\\注册页面2.png").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//用户名输入框
		textField = new JTextField();
		textField.setBounds(260, 162, 170, 38);
		textField.setOpaque(false);//文本区域设为透明
		contentPane.add(textField);
		textField.setColumns(10);
		
		//密码输入框
		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);//文本区域设为透明
		passwordField.setBounds(257, 238, 174, 37);
		contentPane.add(passwordField);
		
		//密码确认输入框
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(255, 301, 180, 41);
		passwordField_1.setOpaque(false);//文本区域设为透明
		contentPane.add(passwordField_1);

		//注册按钮
		btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\注册1.jpg"));//加图标
		btnNewButton_1.setBounds(446, 376, 86, 46);
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//设置鼠标在该区域显示状态变化
		contentPane.add(btnNewButton_1);

		//返回按钮
		btnNewButton = new JButton("");
		btnNewButton.setBounds(311, 374, 83, 48);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//设置鼠标在该区域显示状态变化
		contentPane.add(btnNewButton);

		//提示信息
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(260, 125, 170, 38);
		lblNewLabel.setFont(new Font("SimSun", Font.BOLD, 17));
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);
		
		//返回按钮监听
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				//返回登陆界面
				CatLogin frame = new CatLogin();
				frame.setVisible(true);//显示登录界面
				setVisible(false);//隐藏注册界面
			}
		});
		
		//注册按钮监听
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//获取用户输入的信息
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());
				try {
					//调用注册方法进行用户注册
					Registerer(u_name ,u_pwd,u_pwd_ag);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}}
		);
	}
	/**
	 * 注册方法
	 * @param username
	 * @param password
	 * @param confirm
	 * @throws SQLException
	 */
	protected void Registerer(String username, String password, String confirm) throws SQLException {
		ResultSet result = null;
		try {
			// 连接到数据库
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			state = conn.createStatement();
			// Check if password is correctly set
			if (username.length() != 0) {
				//判断两输入次密码是否一致
				if (password.equals(confirm)) {
					
					result = state.executeQuery("SELECT * FROM test WHERE `name` =" + "'" + username + "'");
					if (result.next()) {
						//检测是否用户名已存在
						if (result.getString(1).equals(username)) {
							lblNewLabel.setText("用户名已存在！");
						}
					} else {
						// 将信息写入到数据库
						ptmt = conn.prepareStatement("INSERT INTO test ( name , keyword ) VALUES ( ? , ? )");
						ptmt.setString(1, username);
						ptmt.setString(2, password);
						int check = ptmt.executeUpdate();
						// 检查数据库注册是否成功
						if (check > 0){
							btnNewButton_1.setEnabled(false);
							//返回登陆界面
							CatLogin frame = new CatLogin();
							frame.setVisible(true);//显示登录界面
							setVisible(false);//隐藏注册界面
						}
					}
				}else{
					lblNewLabel.setText("密码不一致！");
					}
			}else{
				lblNewLabel.setText("密码不能为空！");
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
