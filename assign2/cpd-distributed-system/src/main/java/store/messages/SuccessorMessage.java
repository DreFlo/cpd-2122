package store.messages;

import store.storeRecords.Value;

import java.util.HashMap;

public class SuccessorMessage extends Message{
    private HashMap<String, Value> keyValues;

    public SuccessorMessage(String id, int port, HashMap<String, Value> keyValues) {
        super(id, port);
        this.keyValues = keyValues;
    }

    public HashMap<String, Value> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(HashMap<String, Value> keyValues) {
        this.keyValues = keyValues;
    }
}
