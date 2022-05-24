package store.messages;

import store.storeRecords.Value;

public class PutMessage extends Message {
    private String key;
    private Value value;

    public PutMessage(int port, String key, Value value) {
        super("", port);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
