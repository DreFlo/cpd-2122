package store.messages;

import java.util.HashMap;

public class SuccessorMessage extends Message{
    private HashMap<String, byte[]> keyValues;

    public SuccessorMessage(String id, int port, HashMap<String, byte[]> keyValues) {
        super(id, port);
        this.keyValues = keyValues;
    }

    public HashMap<String, byte[]> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(HashMap<String, byte[]> keyValues) {
        this.keyValues = keyValues;
    }
}
