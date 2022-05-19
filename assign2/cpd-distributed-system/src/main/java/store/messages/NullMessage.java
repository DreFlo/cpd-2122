package store.messages;

import java.util.Arrays;

public class NullMessage extends Message{
    private final byte[] bytes;
    public NullMessage(byte[] bytes) {
        super("", 0);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return "Type - NULL MESSAGE";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NullMessage)) return false;
        NullMessage that = (NullMessage) o;
        return Arrays.equals(getBytes(), that.getBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBytes());
    }
}
