package store.messages;

public class PutMessage extends Message {
    private String key;
    private String value;

    public PutMessage(char[] id, int port, String key, String value) {
        super(id, port);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
