package store.messages;

import store.storeRecords.Value;

import java.util.HashMap;

public class PutSuccessorMessage extends Message{
    private HashMap<String, Value> keyValues;

    public PutSuccessorMessage(String id, int port, HashMap<String, Value> keyValues) {
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
