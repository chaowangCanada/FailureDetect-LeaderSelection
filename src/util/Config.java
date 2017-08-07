package util;

public interface Config {
    public final static int GROUP_PORT = 8437;
    
    final int SERVER_PORT_REPLICA0 = 8000;  //leader port
	final int SERVER_PORT_REPLICA1 = 8001;
	final int SERVER_PORT_REPLICA2 = 8002;
	final int[] SERVER_PORT_ARR = new int[] {SERVER_PORT_REPLICA0, SERVER_PORT_REPLICA1, SERVER_PORT_REPLICA2};

}
