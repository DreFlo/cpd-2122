package store.messages;

public class DeleteSuccessorMessage extends Message{
    private final String key;

    public DeleteSuccessorMessage(String id, int port, String key) {
        super(id, port);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
