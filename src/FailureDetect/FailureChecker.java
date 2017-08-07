package FailureDetect;

import java.util.Map;
import java.util.TimerTask;

public class FailureChecker  extends TimerTask {
	

    public void run() {
    	synchronized(FailureDetection.falureFlag){
            for(Map.Entry<Integer, Boolean>pair : FailureDetection.falureFlag.entrySet()){
            	System.out.print(pair.getKey()+" : "+pair.getValue()+"; ");
            }
            System.out.println();
    	}
    }

}
