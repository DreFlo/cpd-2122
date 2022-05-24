package store.messages;

import store.storeRecords.Value;

import java.util.HashMap;

public class JoinKeyTransferMessage extends Message{
    private HashMap<String, Value> keyValues;

    public JoinKeyTransferMessage(String id, int port) {
        super(id, port);
    }

    public HashMap<String, Value> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(HashMap<String, Value> keyValues) {
        this.keyValues = keyValues;
    }
}
