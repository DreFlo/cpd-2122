package store.messages;

public class GetMessage extends Message{
    private final String key;
    private byte[] value;

    public GetMessage(String id, int port, String key) {
        super(id, port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
