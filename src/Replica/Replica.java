package Replica;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import FailureDetect.FailureChecker;
import HeartBeat.Heartbeater;
import Msg.ConfirmShutDownMessage;
import Msg.HeartbeatMessage;
import Msg.Message;
import util.Config;
import util.DatagramHelper;

public class Replica {

	public int port;
	public HashMap<Integer, Boolean> failureFlag;
	public HashMap<Integer, Integer> receiveCount;
	private int countDif;
	private int resetPort = 0;
	private int talkPort= 0 ;
	
	public Replica(int portNum){
		failureFlag = new HashMap<Integer, Boolean>();
		receiveCount = new HashMap<Integer, Integer>();
		for(int i : Config.SERVER_PORT_ARR){
			failureFlag.put(i, false);
			receiveCount.put(i, 0);
		}
		port = portNum;
		countDif = 0;
	}
	
	public void resetFailureFlag(){
		for(int i : Config.SERVER_PORT_ARR){
			failureFlag.put(i, false);
		}
	}
	
	public void launch() throws IOException, ClassNotFoundException {
		DatagramSocket psock = new DatagramSocket(port);
//        DatagramSocket psock = DatagramHelper.getDatagramSocket();
        this.port = psock.getLocalPort();
        MulticastSocket sock = new MulticastSocket(Config.GROUP_PORT);
        InetAddress    addr  = InetAddress.getByName("224.0.0.4");
        sock.joinGroup(addr);
        
		this.sendHeartBeat(sock, psock, addr);
 
		//this.logFailureFlag();
				
		class FailureChecker extends TimerTask {
		    public void run() {
		    	countDif = 0;
		    	boolean isPlus = true;
		    	for(Map.Entry<Integer, Integer>pair : receiveCount.entrySet()){
	            	if(pair.getKey() != port){
	            		if(isPlus){
	            			countDif += pair.getValue();
	            			talkPort = pair.getKey();
	            			isPlus = false;
	            		}else{
	            			countDif -= pair.getValue();
	            			resetPort = pair.getKey();
	            			isPlus = true;
	            		}
	            	}
	            }
		    	System.out.println(talkPort + " - " +resetPort + " = "+countDif);
//	            for(Map.Entry<Integer, Integer>pair : receiveCount.entrySet()){
//	            	System.out.print(pair.getKey()+" : "+pair.getValue()+"; ");
//	            }
//	            System.out.println();
		    	if(countDif > 10){
		            try {
		                DatagramPacket pkt = DatagramHelper.encodeMessage(new ConfirmShutDownMessage(resetPort), addr, talkPort);
		                sock.send(pkt);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		    	}
		    	else if( countDif < -10)
		            try {
		                DatagramPacket pkt = DatagramHelper.encodeMessage(new ConfirmShutDownMessage(talkPort), addr, resetPort);
		                sock.send(pkt);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		    }
		    
		    
		}
		
		Timer timer = new Timer();
		timer.schedule(new FailureChecker(), 0, 5000);
    	
		receiveHeartBeatMsg(sock, psock, addr);

        sock.leaveGroup(addr);
    }



    public void logFailureFlag() {
        Timer timer = new Timer();
        FailureChecker checker = new FailureChecker(receiveCount);
        timer.schedule(checker, 2000, 4000);
    }

    
    public void sendHeartBeat(MulticastSocket mSock, DatagramSocket dSock, InetAddress groupAddr) throws IOException, ClassCastException, ClassNotFoundException {
        Timer timer = new Timer();
        Heartbeater beater = new Heartbeater(dSock, groupAddr, receiveCount);
        timer.schedule(beater, 1000, 5000);
    }
    
    public void receiveHeartBeatMsg(MulticastSocket mSock, DatagramSocket dSock, InetAddress groupAddr) throws IOException, ClassNotFoundException{
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
            mSock.receive(packet);
            Message message       = DatagramHelper.decodeMessage(packet);
            if (message instanceof HeartbeatMessage) {
                //System.out.printf("receive %s: %s\n", packet.getSocketAddress(), message);
            	int portTmp = packet.getPort();
                if(portTmp != this.port){
                	int count = receiveCount.get(portTmp);
                	receiveCount.put(packet.getPort(), ++count);
                }
            } 
            else if(message instanceof ConfirmShutDownMessage){
            	int talkPort = packet.getPort();
            	int talkPortCount = receiveCount.get(talkPort);
            	int diff = receiveCount.get(message.num) - talkPortCount;
            	if(diff >10 || diff < -10){
            		System.out.println("Confirm shut down of " + message.num);
            	}
            }
        }
    }
}
