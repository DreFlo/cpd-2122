package store.messages;

public class DeleteMessage extends Message{
    private final String key;

    public DeleteMessage(char[] id, int port, String key) {
        super(id, port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Key - " + key + "\n";
    }
}
