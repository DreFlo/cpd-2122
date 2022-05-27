package store.messages;

public class CheckReplicationMessage extends Message{
    private final String key;
    private final Boolean tombstone;

    public CheckReplicationMessage(String id, int port, String key, Boolean tombstone) {
        super(id, port);
        this.key = key;
        this.tombstone = tombstone;
    }

    public String getKey() {
        return key;
    }

    public Boolean getTombstone() {
        return tombstone;
    }
}
