package store.messages;

import store.storeRecords.Value;

public class GetMessage extends Message{
    private final String key;
    private Value value;

    public GetMessage(int port, String key) {
        super("", port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
