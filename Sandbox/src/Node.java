import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.RadioDriver;
import com.virtenio.radio.ieee_802_15_4.RadioDriverFrameIO;
//import com.virtenio.preon32.examples.common.RadioInit;

import java.util.HashMap;

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.device.at86rf231.AT86RF231RadioDriver;
import com.virtenio.misc.PropertyHelper;
//import com.virtenio.vm.Time;
import com.virtenio.preon32.cpu.CPUConstants;
import com.virtenio.preon32.cpu.CPUHelper;

public class Node {
	
//	private final static int ADDR_NODE = PropertyHelper.getInt("remote.addr",0xDAAA);
//	private final static int ADDR_NODE = PropertyHelper.getInt("remote.addr",0xDAAB);
	private final static int ADDR_NODE = PropertyHelper.getInt("remote.addr",0xDAAC);
//	private final static int ADDR_NODE = PropertyHelper.getInt("remote.addr",0xDAAD);
	
	private final static int BROADCAST = PropertyHelper.getInt("remote.addr",0xFFFF);
	
	private final static int COMMON_PANID = PropertyHelper.getInt("radio.panid",0xCACE);
	
//	static boolean bool = true;
	
	private final static int BASE = PropertyHelper.getInt("remote.addr",0xBABE);
	
	private static String hasil = Integer.toHexString(ADDR_NODE) + "#"; 
	
	static int counterNode = 0;
	
	private static HashMap<String, String> tempNode = new HashMap<String, String>();
		
	public static void main(String[] args) {
		run();
	}
	
	public static void run() {
		try {
			AT86RF231 t = com.virtenio.preon32.node.Node.getInstance().getTransceiver();
			t.open();
			t.setAddressFilter(COMMON_PANID, ADDR_NODE, ADDR_NODE, false);
			final RadioDriver radioDriver = new AT86RF231RadioDriver(t);
			final FrameIO fio = new RadioDriverFrameIO(radioDriver);
			send(fio);
			receive(fio);
			updateTable();
		} catch (Exception e) {}
	}

	public static void receive(final FrameIO fio) throws Exception {
		Thread thread = new Thread() {
			public void run() {
				Frame frame = new Frame();
				while (true) {
					try {					
						fio.receive(frame);
						byte[] dg = frame.getPayload();
						String str = new String(dg, 0, dg.length);
						
						String node = str.substring(0, 4);
						String cmd = str.substring(5);		
						
						if(cmd.substring(0,3).equalsIgnoreCase("ACK")) {
							send(Integer.toHexString(ADDR_NODE) + "#ACK", (int) frame.getSrcAddr(),fio);
							if(counterNode == 0) {
								if(!str.substring(0,4).equalsIgnoreCase(Integer.toHexString(ADDR_NODE))) {
									tempNode.put(node, node);
									hasil += tempNode.get(node);
									counterNode++;
								}
							} 
							else {
								if(!tempNode.containsValue(str.substring(0, 4))) {
									if(!str.substring(0,4).equalsIgnoreCase(Integer.toHexString(ADDR_NODE))){
										tempNode.put(node, node);
										hasil += "," + tempNode.get(node);
									}
								}
							}
							broadcastMSG(hasil, fio);
						}
						else if(cmd.equalsIgnoreCase("RESTART")) {
							tempNode.clear();
							hasil = Integer.toHexString(ADDR_NODE) + "#";
							counterNode=0;
							if(node.equalsIgnoreCase(Integer.toHexString(ADDR_NODE))) {
								CPUHelper.setPowerState(CPUConstants.V_POWER_STATE_OFF, 5000);
							}
						}  
						send(str, BASE, fio);
						broadcastMSG(str, fio);
						broadcastMSG(hasil, fio);
						Thread.sleep(800);
					} catch (Exception e) {}
				}
			}
		};
		thread.start();
	}
	
	public static void send(final FrameIO fio) throws Exception {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					try {		
					
						broadcastMSG(Integer.toHexString(ADDR_NODE) + "#ACK", fio);
						
						if(counterNode == 0) {
							Thread.sleep(10000);
							broadcastMSG(hasil, fio);
						} else {
							broadcastMSG(hasil, fio);
						}
						
						Thread.sleep(1000); 
						
					} catch(Exception e) {}
				}
			}
		};
		thread.start();
	}
	
	public static void updateTable() throws Exception{
		Thread thread = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(60000);
						tempNode.clear();
						hasil = Integer.toHexString(ADDR_NODE) + "#";
						counterNode=0;
					} catch (Exception e) {} 
				}
			}
		};
		thread.start();
	}
	
	private static void send(String msg, int dest, final FrameIO fio) throws Exception {
		int frame = Frame.TYPE_DATA | Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.ACK_REQUEST | Frame.SRC_ADDR_16;
		final Frame frameControl = new Frame(frame);
		frameControl.setDestPanId(COMMON_PANID);
		frameControl.setDestAddr(dest);
		frameControl.setSrcAddr(ADDR_NODE);
		frameControl.setPayload(msg.getBytes());
		
		try {
			fio.transmit(frameControl);
		} catch (Exception e) {}
	}
	
	private static void broadcastMSG(String msg, final FrameIO fio) throws Exception {
		int frame = Frame.TYPE_DATA | Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.ACK_REQUEST | Frame.SRC_ADDR_16;
		final Frame frameControl = new Frame(frame);
		frameControl.setDestPanId(COMMON_PANID);
		frameControl.setDestAddr(BROADCAST);
		frameControl.setSrcAddr(ADDR_NODE);
		frameControl.setPayload(msg.getBytes());
		
		try {
			fio.transmit(frameControl);
		} catch (Exception e) {}
	}
}
