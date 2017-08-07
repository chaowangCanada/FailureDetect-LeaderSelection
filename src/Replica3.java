import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Timer;

import FailureDetect.FailureChecker;
import FailureDetect.FailureDetection;
import HeartBeat.Heartbeater;
import Msg.HeartbeatAcknowledge;
import Msg.HeartbeatMessage;
import Msg.Message;
import util.Config;
import util.DatagramHelper;


public class Replica3 {
	
	public int port;
	public HashMap<Integer, Boolean> falureFlagptr;
	
	
	public Replica3(){
		falureFlagptr = FailureDetection.falureFlag;
	}
    
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		Replica3 r3 = new Replica3();
		DatagramSocket psock = new DatagramSocket(Config.SERVER_PORT_REPLICA2);
        //DatagramSocket psock = DatagramHelper.getDatagramSocket();
        r3.port = psock.getLocalPort();
        MulticastSocket sock = new MulticastSocket(Config.GROUP_PORT);
        InetAddress    addr  = InetAddress.getByName("224.0.0.4");
        sock.joinGroup(addr);
        
		r3.sendHeartBeat(sock, psock, addr);

		
        Timer timer = new Timer();
        FailureChecker fcheck = new FailureChecker();
        timer.schedule(fcheck, 2000, 4000);
        
		r3.receiveHeartBeatMsg(sock, psock, addr);
    	
        sock.leaveGroup(addr);
    }
	
    public void sendHeartBeat(MulticastSocket mSock, DatagramSocket dSock, InetAddress groupAddr) throws IOException, ClassCastException, ClassNotFoundException {
        Timer timer = new Timer();
//        FailureDetection.falureFlag.put(port, false);
        Heartbeater beater = new Heartbeater(dSock, groupAddr, port);
        timer.schedule(beater, 1000, 4000);
    }
    
    public void receiveHeartBeatMsg(MulticastSocket mSock, DatagramSocket dSock, InetAddress groupAddr) throws IOException, ClassNotFoundException{
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
            mSock.receive(packet);
            Message message       = DatagramHelper.decodeMessage(packet);
            if (message instanceof HeartbeatMessage) {
                //System.out.printf("receive %s: %s\n", packet.getSocketAddress(), message);
                if(packet.getPort() != this.port){
                	synchronized(FailureDetection.falureFlag){
                    	FailureDetection.falureFlag.put(packet.getPort(), true);
                	}
                }
            }
        }
    }

}
