package store.messages;

public class GetMessage extends Message{
    private final String key;
    private String value;

    public GetMessage(char[] id, int port, String key) {
        super(id, port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
