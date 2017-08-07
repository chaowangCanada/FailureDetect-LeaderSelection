package Replica;

import java.io.IOException;

import util.Config;

public class ReplicaRunner3 {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Replica r3 = new Replica(Config.SERVER_PORT_REPLICA2);
		r3.launch();
	}

}
