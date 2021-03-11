import com.virtenio.commander.toolsets.preon32.Preon32Helper;
import com.virtenio.commander.io.DataConnection;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Project;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.AWTException;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JButton;

import javax.swing.JScrollPane;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public class GUI {

	private JFrame frame;
	private static JPanel panelDraw;
	private static JTextArea log;
	private static JButton btnRestart;
	private static JButton btnExport;
	private static JComboBox <Object> comboBox;
	private static JTable tableNeighbor;
	private static String[] nama;
	private static int num;
	private static String[][] dataTable;
	private static String[][] nodeList;	
	private static int[][] tempPosisi;
	private static boolean show = false;
	private static int[][] status;   

	/**
	 * Launch the application.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		GUI.context_set("context.set.1");
		GUI.time_synchronize();
		init();
	}
	
	public static String[] nameList()  {
		ArrayList<String> name = new ArrayList<>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader("C:/Users/USER/eclipse-workspace/CMD/src/nodeList.txt"));
			String line = null;
			while((line = bf.readLine()) != null) {
				line = line.trim();
				if(!line.contentEquals("")) {
					name.add(line);
				}
				if(name.size() == 50) {
					break;
				}
			}
			bf.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		String[] node_name = name.toArray(new String[name.size()]);
		return node_name;
	}

	/**
	 * Create the application.
	 */
	public GUI() throws Exception {
		nama = nameList();
		status = new int[nama.length][nama.length];
//		for(int i=0; i<status.length; i++) {
//			for(int j=0; j<status.length; j++) {
//				status[i][j] = 0;			
//			}
//		}
		nodeList = new String[nama.length][nama.length];
		for(int i=0; i<nama.length; i++) {
			nodeList[i][0] = nama[i];
		}
		tempPosisi = new int[nama.length][nama.length];
		initialize();
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() throws Exception{
		frame = new JFrame();
		frame.setBounds(100, 100, 1075, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		BufferedImage imageNode = ImageIO.read(new File("C:\\Users\\USER\\eclipse-workspace\\CMD\\src\\basestation.png"));
		BufferedImage imageBS = ImageIO.read(new File("C:\\Users\\USER\\eclipse-workspace\\CMD\\src\\node.png"));
		
		panelDraw = new javax.swing.JPanel() {
				private static final int SIZE = 250;
				private int a = SIZE / 2;
				private int b = a;
				private int r = 4 * SIZE / 5;
				
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					a = getWidth() / 2;
					b = getHeight() / 2;
					int m = Math.min(a, b);
					r = 4 * m / 5;
					int r2 = Math.abs(m - r) / 2;
					for (int i = 0; i < nodeList.length; i++) {
						if(i==0) {
							double t = 2 * Math.PI * i / nodeList.length - 1;
							int x = (int) Math.round(a + r * Math.cos(t));
							int y = (int) Math.round(b + r * Math.sin(t));
							g.drawImage(imageBS, x - r2, y - r2, 50, 50, this);
							g.drawString("BASE", x - r2 , y - r2);
							tempPosisi[0][0] = x - r2;
							tempPosisi[0][1] = y - r2;
						} else {
						    double t = 2 * Math.PI * i / nodeList.length - 1;
							int x = (int) Math.round(a + r * Math.cos(t));
							int y = (int) Math.round(b + r * Math.sin(t));
				            g.drawImage(imageNode, x - r2, y - r2, 50, 50, this);
					        g.drawString(dataTable[i][0] + "", x - r2 , y - r2);
					        tempPosisi[i][0] = x - r2;
					        tempPosisi[i][1] = y - r2;            
						 }      	 
					 }	      
				if(show) {	
					g.setColor(Color.GREEN);
					for(int i=0; i<status.length; i++) {
						for(int j=0; j<status[i].length; j++) {
							if(status[i][j]==1&&status[j][i]==1) {
								g.drawLine(tempPosisi[i][0], tempPosisi[i][1], tempPosisi[j][0], tempPosisi[j][1]);
							}
						}
					}
				} 
			}
		};
		springLayout.putConstraint(SpringLayout.NORTH, panelDraw, 10, SpringLayout.NORTH, frame.getContentPane());
		frame.getContentPane().add(panelDraw);
		panelDraw.setBackground(Color.WHITE);
		
		comboBox = new JComboBox<Object>(nama);
		frame.getContentPane().add(comboBox);
		
		btnRestart = new JButton("Restart");
		springLayout.putConstraint(SpringLayout.SOUTH, comboBox, -26, SpringLayout.NORTH, btnRestart);
		springLayout.putConstraint(SpringLayout.WEST, btnRestart, 0, SpringLayout.WEST, comboBox);
		frame.getContentPane().add(btnRestart);
		
		btnExport = new JButton("Export");
		springLayout.putConstraint(SpringLayout.SOUTH, btnExport, -53, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, btnRestart, 0, SpringLayout.NORTH, btnExport);
		frame.getContentPane().add(btnExport);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.EAST, panelDraw, -81, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -272, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 154, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, panelDraw, -20, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, btnExport, 16, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, panelDraw, 0, SpringLayout.WEST, scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 613, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, frame.getContentPane());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollPane);
		
		log = new JTextArea();
		DefaultCaret caret = (DefaultCaret)log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(log);
		
		String[] columnNames = {"Nama", "Tetangga"};
		dataTable = new String[nama.length][2];
		
		for(int i=0; i<nama.length; i++) {
			dataTable[i][0] = nama[i];
		}
		
		tableNeighbor = new JTable(dataTable, columnNames);
		springLayout.putConstraint(SpringLayout.NORTH, tableNeighbor, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tableNeighbor, 728, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tableNeighbor, -20, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, tableNeighbor, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(tableNeighbor);
	}
	
	/**
	 * Method untuk mengeluarkan log yang dijalankan ant script pada console.
	 */
	private static DefaultLogger getConsoleLogger() {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		return consoleLogger;
	}
	
	/**
	 * Method untuk memasuki salah satu context sesuai dengan parameter.
	 * @param context merupakan nomor context yang ingin dipanggil.
	 */
	private static void context_set(String target) throws Exception{
		DefaultLogger consoleLogger = getConsoleLogger();
		File buildFile = new File("D:\\WSN-NDP\\Sandbox\\buildUser.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);
		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			antProject.addReference("ant.ProjectHelper", helper);
			helper.parse(antProject, buildFile);

			antProject.executeTarget(target);
			antProject.fireBuildFinished(null);
		} catch (BuildException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method untuk mensinkronisasikan waktu di node sensor root dengan perangkat yang terhubung.
	 */
	private static void time_synchronize() throws Exception{
		DefaultLogger consoleLogger = getConsoleLogger();
		File buildFile = new File("D:\\WSN-NDP\\Sandbox\\build.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);

		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			antProject.addReference("ant.ProjectHelper", helper);
			helper.parse(antProject, buildFile);
			String target = "cmd.time.synchronize";
			antProject.executeTarget(target);
			antProject.fireBuildFinished(null);
		} catch (BuildException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method untuk menginisiasi hubungan antara antarmuka dengan root.
	 * Nilai port harus sesuai dengan yang terpasang oleh root.
	 * Setelah terhubungkan, user dapat memasukkan input yang diberikan melalui command line.
	 */
	public static void init() throws Exception{
		Preon32Helper nodeHelper = new Preon32Helper("COM4", 115200);
		DataConnection conn = nodeHelper.runModule("basestation");
		BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
			
		btnPressed();
		start(in, conn);	
	}

	public static void start(BufferedInputStream in, DataConnection conn) throws Exception {	
		Thread thread = new Thread() {
			public void run() {
				String result = "";
				num = 1;
				
				do {
					try{
						conn.flush();
						conn.write(num);
				
						Thread.sleep(1000);
						
							byte[] buffer = new byte[1024];
							
							if(num == 1) {
								while(in.available() > 0) {
									in.read(buffer);
									conn.flush();
									String s = new String(buffer);	
								
									log.append(s + "\n");
									
									System.out.println(s);

									String[] data = s.split("#");
									String data1 = data[0];//nama node pengirim 
									String data2 = data[1];//nama tetangga node pengirim 
									String[] neighbor = new String[nama.length];
									neighbor = data2.split(",");
									
//									String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
									
									result = "Node tetangga " + data1 + ": " + data2 + "\n";
									log.append(result);	
									
//									FileWriter myWriter = new FileWriter("C://Users//USER//Desktop//" + timeStamp + ".txt");
//									myWriter.write(result);
//									myWriter.close();
										
									neighborVisualTable(data1, neighbor);
								}
								Thread.sleep(100);
							} else if(num == 5) {
									BufferedImage imagebuf=null;
//									BufferedImage imagebuf2=null;
									String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
								    try {
								        imagebuf = new Robot().createScreenCapture(panelDraw.getBounds());
//								        imagebuf2 = new Robot().createScreenCapture(tableNeighbor.getBounds());
								    } catch (AWTException e1) {
								        // TODO Auto-generated catch block
								        e1.printStackTrace();
								    }  
								     Graphics2D graphics2D = imagebuf.createGraphics();
//								     Graphics2D graphics2D2 = imagebuf2.createGraphics();
								     panelDraw.paint(graphics2D);
//								     tableNeighbor.paint(graphics2D2);
								     try {
								        ImageIO.write(imagebuf,"jpeg", new File("C://Users//USER//Desktop//" + timeStamp + "image.jpeg"));
//								        ImageIO.write(imagebuf2,"jpeg", new File("C://Users//USER//Desktop//" + timeStamp + "imageTable.png"));
								        log.append("Image saved succesfully \n");
								    } catch (Exception e) {
								        // TODO Auto-generated catch block
								        System.out.println("error");
								    }
								    num = 1 ;
							}
							else if(num >= 50) {	
								dataTable[num - 50][1] = null;
								
								for(int i=0; i<nama.length;i++) {
									status[num - 50][i] = 0;
									dataTable[num - 50][1] = "";
								}
								show = false;
								panelDraw.repaint();
								tableNeighbor.repaint();
									
								result = "Node: " + dataTable[num - 50][0] + " is " + "restarting \n";
								log.append(result);
								
								Thread.sleep(1000);
								num = 1;
							} 
						
					}catch (Exception e) {}
				} while(true);
			}
		};
		thread.start();
	}
	
	public static void neighborVisualTable(String str, String[] arrStr) {
		Thread t = new Thread() {
			public void run() {
				try {
					if(str.equalsIgnoreCase("babe")&&arrStr[0].equalsIgnoreCase(null)) {
						for(int i=0; i<nama.length; i++) {
							for(int j=0; j<nama.length; j++) {
								status[i][j] = 0;
								status[j][i] = 0;
							}
						}
					}
					for(int i=0; i<nama.length; i++) {
						if(str.equalsIgnoreCase(nama[i])){
							for(int x=0; x<nama.length; x++) {
								status[i][x] = 0;
								status[x][i] = 0;
							}
							for(int j=0; j<arrStr.length; j++) {
								System.out.println("panjang data=" + nama.length);
								System.out.println("node" + j + "=" + arrStr[j]);
								for(int k=0; k<nama.length; k++) {
									System.out.println(arrStr[j] + "=" + nama[k]);
									if(arrStr[j].equalsIgnoreCase(nama[k]) && status[i][k] == 0) {
										status[i][k] = 1;
										status[k][i] = 1;
										k = nama.length;
									}
								}
							}
						}
					}
					String tempNode = "";
					for(int i=0; i<status.length; i++) {
						for(int j=0; j<status[i].length; j++) {
							if(status[i][j]==1) {
								tempNode += nama[j].toLowerCase() +  " ";
							}
						}
						dataTable[i][1] = tempNode;
						tempNode = "";
					}
					show = true;
					tableNeighbor.repaint();
					panelDraw.repaint();
					Thread.sleep(100);
				} catch(Exception e) {}
			}
		};
		t.start();
	}
	
	public static void btnPressed() {
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {
						try {
							num = comboBox.getSelectedIndex() + 50;
							Thread.sleep(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		});
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {
						try {
							num = 5;
							Thread.sleep(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		});
	}
}

