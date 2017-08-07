package Msg;

import java.io.Serializable;

public class Message implements Serializable {
    public long num;
    public String msg;
    public String toString() {
        return String.format("Message %d: %s", num, msg);
    }
}
