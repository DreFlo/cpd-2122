package store.messages;

public class CheckReplicationMessage extends Message{
    private final String key;

    public CheckReplicationMessage(String id, int port, String key) {
        super(id, port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
