package store.messages;

public class NullMessage extends Message{
    private byte[] bytes;
    public NullMessage(byte[] bytes) {
        super("", null, "".toCharArray(), 0);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
