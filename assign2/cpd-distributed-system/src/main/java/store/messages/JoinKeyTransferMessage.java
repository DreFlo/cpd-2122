package store.messages;

import java.util.HashMap;

public class JoinKeyTransferMessage extends Message{
    private HashMap<String, byte[]> keyValues;

    public JoinKeyTransferMessage(String id, int port) {
        super(id, port);
    }

    public HashMap<String, byte[]> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(HashMap<String, byte[]> keyValues) {
        this.keyValues = keyValues;
    }
}
