package Replica;

import java.io.IOException;

import util.Config;

public class ReplicaRunner2 {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Replica r2 = new Replica(Config.SERVER_PORT_REPLICA1);
		r2.launch();
	}

}
