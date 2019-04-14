

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * 用户列表中每个用户显示状态
 * @author HP
 *
 */

class CellRenderer extends JLabel implements ListCellRenderer {
	CellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// 加入宽度为5的空白边框

		if (value != null) {
			setText(value.toString());
			setIcon(new ImageIcon("images//tou1.png"));//设置头像
		}
		//设置用户被选中和为被选中两种前景与背景颜色状态表示
		if (isSelected) {
			//被选中时
			setBackground(new Color(255, 255, 153));// 设置背景色
			setForeground(Color.black);//设置字体色
		} else {
			//未被选中
			setBackground(Color.white); // 设置背景色
			setForeground(Color.black);//设置字体色
		}
		setEnabled(list.isEnabled());
		setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
		setOpaque(true);
		return this;
	}
}


class UUListModel extends AbstractListModel{
	
	private Vector vs;
	
	public UUListModel(Vector vs){
		this.vs = vs;
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return vs.get(index);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return vs.size();
	}
	
}


public class CatChatroom extends JFrame {

	private static final long serialVersionUID = 6129126482250125466L;

	private static JPanel contentPane;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	private static JTextArea textArea;
	private static AbstractListModel listmodel;
	private static JList list;
	private static String filePath;
	private static JLabel lblNewLabel;
	private static JProgressBar progressBar;
	private static Vector onlines;
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;

	// 声音
	private static File file, file2;
	private static URL cb, cb2;
	private static AudioClip aau, aau2;
	private File contentFile;
	/**
	 * Create the frame.
	 */

	public CatChatroom(String u_name, Socket client) {
		// 赋值
		name = u_name;
		clientSocket = client;
		onlines = new Vector();
		
		SwingUtilities.updateComponentTreeUI(this);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//改变窗口显示风格
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		
		setTitle(name);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(440, 200, 896, 796);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override//加载背景图片
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\q1.jpg").getImage(), 0, 0,
						getWidth(), getHeight(), null);
			}
		};
		
		try {
		contentFile=new File(name);
		} catch (Exception e) {
		// TODO: handle exception
		}
	
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 聊天信息显示区域
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 490, 510);
		getContentPane().add(scrollPane);
		//聊天信息显示框
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);//激活自动换行功能 
		textArea.setWrapStyleWord(true);//激活断行不断字功能 
		textArea.setFont(new Font("sdf", Font.BOLD, 17));
		//将以往聊天记录显示在聊天信息显示框
		try {
		BufferedReader br=new BufferedReader(new FileReader(contentFile));
		String tempString = null;
		textArea.setFont(new Font("sdf", Font.BOLD, 13));
		while((tempString=br.readLine())!=null){
			textArea.append(tempString+"\n");
		}
		textArea.setFont(new Font("sdf", Font.BOLD, 17));

		br.close();
		
	} catch ( IOException e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
		
		scrollPane.setViewportView(textArea);
		
		// 打字区域
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 557, 498, 137);
		getContentPane().add(scrollPane_1);
		//输入信息显示框
		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);//激活自动换行功能 
		textArea_1.setWrapStyleWord(true);//激活断行不断字功能 
		textArea_1.setFont(new Font("sdf", Font.PLAIN, 18));
		scrollPane_1.setViewportView(textArea_1);

		// 关闭按钮
		final JButton btnNewButton = new JButton("关闭");
		btnNewButton.setBounds(270, 700, 100, 40);
		getContentPane().add(btnNewButton);

		// 发送按钮
		JButton btnNewButton_1 = new JButton("发送");
		btnNewButton_1.setBounds(224+180, 700, 100, 40);
		getRootPane().setDefaultButton(btnNewButton_1);
		getContentPane().add(btnNewButton_1);
		//贪吃蛇按钮
		JButton btnNewButton_2 = new JButton("贪吃蛇");
		btnNewButton_2.setBounds(344+230, 700, 100, 40);
		//贪吃蛇按钮增添事件监听
		btnNewButton_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 启动贪吃蛇小游戏
				GreedSnake gs;
				try {
					gs = new GreedSnake();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		getContentPane().add(btnNewButton_2);
		
		//截屏按钮
		JButton btnNewButton_3 = new JButton("截屏");
		btnNewButton_3.setBounds(466+255, 700, 100, 40);
		//截屏按钮增添事件监听
		btnNewButton_3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			Sreen ss=new Sreen();
			//启动截屏功能
			ss.doStart();
				
			}
		});
		getContentPane().add(btnNewButton_3);
		
		//清屏按钮
		JButton btnNewButton_4 = new JButton("清屏");
		btnNewButton_4.setBounds(414,523, 100, 34);
		//增加时间监听
		btnNewButton_4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
				
			}
		});
		getContentPane().add(btnNewButton_4);
		
		// 在线客户列表
		listmodel = new UUListModel(onlines) ;
		list = new JList(listmodel);
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
		Border etch = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etch, "<"+u_name+">"
				+ "在线客户:", TitledBorder.LEADING, TitledBorder.TOP, new Font(
				"sdf", Font.BOLD, 20), Color.green));//标题栏设置
		//在线用户滚动区域
		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(530, 10, 345,575);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		getContentPane().add(scrollPane_2);

		// 文件传输栏        progressBar       它可以 简单地输出进度的变化情况
		progressBar = new JProgressBar();
		progressBar.setBounds(530, 650, 345, 25);
		progressBar.setMinimum(1);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);

		// 文件传输提示
		lblNewLabel = new JLabel(
				"文件传输信息栏：");
		lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 15));
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(530, 610,345, 25);
		getContentPane().add(lblNewLabel);

		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// 记录上线客户的信息在catbean中，并发送给服务器
			CatBean bean = new CatBean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(CatUtil.getTimer());
			oos.writeObject(bean);
			oos.flush();

			// 消息提示声音
			file = new File("sounds\\消息.wav");
			cb = file.toURL();
			aau = Applet.newAudioClip(cb);
			// 上线提示声音
			file2 = new File("sounds\\上线.wav");
			cb2 = file2.toURL();
			aau2 = Applet.newAudioClip(cb2);

			// 启动客户接收线程
			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 发送按钮事件监听
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String info = textArea_1.getText();
				List to = list.getSelectedValuesList();//获取所选择对象
				//若未选择对象
				if (to.size() < 1) {
					JOptionPane.showMessageDialog(getContentPane(), "请选择聊天对象");
					return;
				}
				//若选择对象为自己
				if (to.toString().contains(name+"(我)")) {
					JOptionPane
							.showMessageDialog(getContentPane(), "不能向自己发送信息");
					return;
				}
				//若发送信息为空
				if (info.equals("")) {
					JOptionPane.showMessageDialog(getContentPane(), "不能发送空信息");
					return;
				}
				//正常情况下，建立连接，发送数据
				CatBean clientBean = new CatBean();
				clientBean.setType(1);
				clientBean.setName(name);
				String time = CatUtil.getTimer();
				clientBean.setTimer(time);
				clientBean.setInfo(info);
				HashSet set = new HashSet();
				set.addAll(to);
				clientBean.setClients(set);
				sendMessage(clientBean);

				// 自己发的内容也要现实在自己的屏幕上面
				textArea.append(time + " 我对" + to + "说:\r\n" + info + "\r\n");
				//将发送信息保存在本地文件中，作为聊天记录
				try {
					FileWriter fw=new FileWriter(contentFile,true);
					BufferedWriter bw=new BufferedWriter(fw);
					bw.write(time + "我对<" + to + ">说:\r\n" + info + "\r\n");
					
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//清空发送消息栏并重新获取焦点
				textArea_1.setText(null);
				textArea_1.requestFocus();
			}
		});

		// 关闭按钮事件监听
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//在文件传输时不能关闭窗口
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"正在传输文件中，您不能离开...",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
				//发送下线消息
				btnNewButton.setEnabled(false);
				CatBean clientBean = new CatBean();
				clientBean.setType(-1);
				clientBean.setName(name);
				clientBean.setTimer(CatUtil.getTimer());
				sendMessage(clientBean);
				}
			}
		});

		// 窗口事件监听    
		this.addWindowListener(new WindowAdapter() {
			@Override
			//离开
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"正在传输文件中，您不能离开...",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
				int result = JOptionPane.showConfirmDialog(getContentPane(),
						"您确定要离开聊天室");//取得用户选择
				//若用户选择确定离开，发送下线消息
				if (result == 0) {
					CatBean clientBean = new CatBean();
					clientBean.setType(-1);
					clientBean.setName(name);
					clientBean.setTimer(CatUtil.getTimer());
					sendMessage(clientBean);
				}
				}
			}
		});

		// 在线用户列表监听
		list.addMouseListener(new MouseAdapter() {

			@Override//鼠标事件监听
			public void mouseClicked(MouseEvent e) {
				List to = list.getSelectedValuesList();
				//双击事件代表发送文件
				if (e.getClickCount() == 2) {
					//如过发送对象为自己
					if (to.toString().contains(name+"(我)")) {
						JOptionPane
								.showMessageDialog(getContentPane(), "不能向自己发送文件");
						return;
					}
					
					// 双击打开文件文件选择框
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("选择文件框"); // 标题
					chooser.showDialog(getContentPane(), "选择"); // 按钮的名字

					// 判定是否选择了文件
					if (chooser.getSelectedFile() != null) {
						// 获取路径
						filePath = chooser.getSelectedFile().getPath();
						File file = new File(filePath);
						// 如果文件为空
						if (file.length() == 0) {
							JOptionPane.showMessageDialog(getContentPane(),
									filePath + "文件为空,不允许发送.");
							return;
						}
						//正常状态，建立连接，发送请求
						CatBean clientBean = new CatBean();
						clientBean.setType(2);// 请求发送文件
						clientBean.setSize(new Long(file.length()).intValue());
						clientBean.setName(name);
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(file.getName()); // 记录文件的名称
						clientBean.setInfo("请求发送文件");

						// 判断要发送给谁
						HashSet<String> set = new HashSet<String>();
						set.addAll(list.getSelectedValuesList());
						clientBean.setClients(set);
						sendMessage(clientBean);
					}
				}
			}
		});

	}
/**
 * 线程接收类
 * @author HP
 *
 */
	class ClientInputThread extends Thread {

		@Override
		public void run() {
			try {
				// 不停的从服务器接收信息
				while (true) {
					ois = new ObjectInputStream(clientSocket.getInputStream());
					final CatBean  bean = (CatBean) ois.readObject();
					//分析接受到catbean的类型
					switch (bean.getType()) {
					case 0: {
						// 更新列表
						onlines.clear();//清空列表
						HashSet<String> clients = bean.getClients();
						Iterator<String> it = clients.iterator();
						//重新加载
						while (it.hasNext()) {
							String ele = it.next();
							if (name.equals(ele)) {
								onlines.add(ele + "(我)");
							} else {
								onlines.add(ele);
							}
						}

						listmodel = new UUListModel(onlines);
						list.setModel(listmodel);
						aau2.play();//上线声音
						textArea.append(bean.getInfo() + "\r\n");
						textArea.selectAll();
						break;
					}
					case -1: {
						//直接下线
						return;
					}
					case 1: {
						//获取发送信息
						String info = bean.getTimer() + "  " +bean.getName()
								+ "对 " +bean.getClients() + "说:\r\n";
						//将对方发送消息中自己的名字替换成“我”
						if (info.contains(name) ) {
							info = info.replace(name, "我");
						}
						aau.play();
						textArea.append(info+bean.getInfo() + "\r\n");
						//将对方发送的消息写入聊天记录中
						try {
							FileWriter fw=new FileWriter(contentFile,true);
							BufferedWriter bw=new BufferedWriter(fw);
							bw.write(info+bean.getInfo() + "\r\n");
							
							bw.close();
							fw.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						textArea.selectAll();
						break;
					}
					case 2: {
						// 由于等待目标客户确认是否接收文件是个阻塞状态，所以这里用线程处理
						new Thread(){
							public void run() {
								//显示是否接收文件对话框
								int result = JOptionPane.showConfirmDialog(
										getContentPane(), bean.getInfo());
								//对用户做出的选择做出反应
								switch(result){
									case 0:{  //接收文件
										JFileChooser chooser = new JFileChooser();
										chooser.setDialogTitle("保存文件框"); // 标题
										//默认文件名称还有放在当前目录下
										chooser.setSelectedFile(new File(bean
												.getFileName()));
										chooser.showDialog(getContentPane(), "保存"); // 设置按钮名字
										//保存路径
										String saveFilePath =chooser.getSelectedFile().toString();
								
										//创建客户CatBean
										CatBean clientBean = new CatBean();
										clientBean.setType(3);
										clientBean.setName(name);  //接收文件的客户名字
										clientBean.setTimer(CatUtil.getTimer());
										clientBean.setFileName(saveFilePath);
										clientBean.setInfo("确定接收文件");

										// 判断要发送给谁
										HashSet<String> set = new HashSet<String>();
										set.add(bean.getName());
										clientBean.setClients(set);  //文件来源
										clientBean.setTo(bean.getClients());//给这些客户发送文件
									
									
									
										// 创建新的tcp socket 接收数据
										try {
											ServerSocket ss = new ServerSocket(0); // 0可以获取空闲的端口号
											clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
											clientBean.setPort(ss.getLocalPort());
											sendMessage(clientBean); // 先通过服务器告诉发送方, 你可以直接发送文件到我这里了
											isReceiveFile=true;
											//等待文件来源的客户，输送文件....目标客户从网络上读取文件，并写在本地上
											Socket sk = ss.accept();
											textArea.append(CatUtil.getTimer() + "  " + bean.getFileName()
													+ "文件保存中.\r\n");
											DataInputStream dis = new DataInputStream(  //从网络上读取文件
													new BufferedInputStream(sk.getInputStream()));
											DataOutputStream dos = new DataOutputStream(  //写在本地上
													new BufferedOutputStream(new FileOutputStream(
															saveFilePath)));
				
											int count = 0;
											int num = bean.getSize() / 100;
											int index = 0;
											while (count < bean.getSize()) {
												int t = dis.read();
												dos.write(t);
												count++;
											
												if(num>0){
													if (count % num == 0 && index < 100) {
														progressBar.setValue(++index);
													}
													lblNewLabel.setText("下载进度:" + count
															+ "/" + bean.getSize() + "  整体" + index
															+ "%");
												}else{
													lblNewLabel.setText("下载进度:" + count
															+ "/" + bean.getSize() +"  整体:"+new Double(new Double(count).doubleValue()/new Double(bean.getSize()).doubleValue()*100).intValue()+"%");
													if(count==bean.getSize()){
														progressBar.setValue(100);
													}
												}
											}
										
										//给文件来源客户发条提示，文件保存完毕
										PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
										out.println(CatUtil.getTimer() + " 发送给"+name+"的文件[" + bean.getFileName()+"]"
												+ "文件保存完毕.\r\n");
										out.flush();
										dos.flush();
										dos.close();
										out.close();
										dis.close();
										sk.close();
										ss.close();
										textArea.append(CatUtil.getTimer() + "  " + bean.getFileName()
												+ "文件保存完毕.存放位置为:"+saveFilePath+"\r\n");
										isReceiveFile = false;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									break;
								}default: {
									//用户选择取消接收
									CatBean clientBean = new CatBean();
									clientBean.setType(4);
									clientBean.setName(name);  //接收文件的客户名字
									clientBean.setTimer(CatUtil.getTimer());
									clientBean.setFileName(bean.getFileName());
									clientBean.setInfo(CatUtil.getTimer() + "  "
											+ name + "取消接收文件["
											+ bean.getFileName() + "]");
									// 判断要发送给谁
									HashSet<String> set = new HashSet<String>();
									set.add(bean.getName());
									clientBean.setClients(set);  //文件来源
									clientBean.setTo(bean.getClients());//给这些客户发送文件
									sendMessage(clientBean);
									break;
								}
							}
							};	
						}.start();
						break;
					}
					case 3: {  //目标客户愿意接收文件，源客户开始读取本地文件并发送到网络上
						textArea.append(bean.getTimer() + "  "+ bean.getName() + "确定接收文件" + ",文件传送中..\r\n");//在聊天面板显示信息
						new Thread(){
							public void run() {
								
								try {
									isSendFile = true;
									//创建要接收文件的客户套接字
									Socket s = new Socket(bean.getIp(),bean.getPort());
									DataInputStream dis = new DataInputStream(
											new FileInputStream(filePath));  //本地读取该客户刚才选中的文件
									DataOutputStream dos = new DataOutputStream(
											new BufferedOutputStream(s
													.getOutputStream()));  //网络写出文件
									
								
									int size = dis.available();
									
									int count = 0;  //读取次数
									int num = size / 100;
									int index = 0;
									while (count < size) {
										
										int t = dis.read();
										dos.write(t);
										count++;  //每次只读取一个字节
										//显示传输进度
										if(num>0){
											if (count % num == 0 && index < 100) {
												progressBar.setValue(++index);
	
											}
											
											lblNewLabel.setText("上传进度:" + count + "/"
															+ size + "  整体" + index
															+ "%");
										}else{
											lblNewLabel.setText("上传进度:" + count + "/"
													+ size +"  整体:"+new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"
													);
											if(count==size){
												progressBar.setValue(100);
											}
										}
									}
									dos.flush();
									dis.close();
								  //读取目标客户的提示保存完毕的信息
								    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
								    textArea.append( br.readLine() + "\r\n");
								    isSendFile = false;
									br.close();
								    s.close();
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							
							};
						}.start();
						break;
					}
					case 4: {
						textArea.append(bean.getInfo() + "\r\n");
						break;
					}
					default: {
						break;
					}
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
	}
/**
 * 传输信息方法
 * @param clientBean
 */
	private void sendMessage(CatBean clientBean) {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(clientBean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
