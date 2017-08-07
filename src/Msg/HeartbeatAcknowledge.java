package Msg;

public class HeartbeatAcknowledge extends Message {
    public String toString() {
        return String.format("I'm alive %d", num);
    }
}