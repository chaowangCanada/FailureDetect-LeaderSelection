package FailureDetect;

import java.util.HashMap;
import java.util.Timer;



public class FailureDetection {

	public static HashMap<Integer, Boolean> falureFlag = new HashMap<Integer, Boolean>();
	
	public static void main(String args[]){
		
        Timer timer = new Timer();
        FailureChecker fcheck = new FailureChecker();
        timer.schedule(fcheck, 2000, 4000);
	}
}
