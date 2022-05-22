package store.messages;

import java.util.HashMap;

public class LeaveKeyTransferMessage extends Message{
    private HashMap<String, byte[]> keyValues;

    public LeaveKeyTransferMessage(String id, int port, HashMap<String, byte[]> keyValues) {
        super(id, port);
        this.keyValues = keyValues;
    }

    public HashMap<String, byte[]> getKeyValues() {
        return keyValues;
    }
}
