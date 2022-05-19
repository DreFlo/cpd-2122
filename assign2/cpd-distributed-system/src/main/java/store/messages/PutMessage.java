package store.messages;

public class PutMessage extends Message {
    private String key;
    private byte[] value;

    public PutMessage(String id, int port, String key, byte[] value) {
        super(id, port);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
