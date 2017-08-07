package Replica;

import java.io.IOException;

import util.Config;

public class ReplicaRunner1 {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Replica r1 = new Replica(Config.SERVER_PORT_REPLICA0);
		r1.launch();
	}

}
