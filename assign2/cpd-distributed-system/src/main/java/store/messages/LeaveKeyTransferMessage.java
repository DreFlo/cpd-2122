package store.messages;

import store.storeRecords.Value;

import java.util.HashMap;

public class LeaveKeyTransferMessage extends Message{
    private HashMap<String, Value> keyValues;

    public LeaveKeyTransferMessage(String id, int port, HashMap<String, Value> keyValues) {
        super(id, port);
        this.keyValues = keyValues;
    }

    public HashMap<String, Value> getKeyValues() {
        return keyValues;
    }
}
