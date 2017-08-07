package HeartBeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

import FailureDetect.FailureDetection;
import Msg.HeartbeatMessage;
import util.Config;
import util.DatagramHelper;

public class Heartbeater extends TimerTask {
    private DatagramSocket sock;
    private InetAddress addr;
    private long lastSentMessage = 0;
    private int port;
    
    public Heartbeater(DatagramSocket dSock, InetAddress groupAddr, int port) {
        this.sock = dSock;
        this.addr = groupAddr;
        this.port = port;
    }

    public void run() {
        //System.out.println("Sending out heartbeat");
        synchronized(FailureDetection.falureFlag){
            FailureDetection.falureFlag.put(this.port, false);
        }
        HeartbeatMessage msg = new HeartbeatMessage();
        msg.num = lastSentMessage++;
        msg.msg = "HEY";
        try {
            DatagramPacket pkt = DatagramHelper.encodeMessage(msg, addr, Config.GROUP_PORT);
            sock.send(pkt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
