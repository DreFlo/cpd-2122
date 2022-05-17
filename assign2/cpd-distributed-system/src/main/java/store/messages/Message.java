package store.messages;

import store.Utils;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public abstract class Message implements Serializable {
    char[] id;
    int port;

    public Message(char[] id, int port) {
        this.id = id;
        this.port = port;
    }

    public char[] getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public static Message fromBytes(byte[] bytes) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
        Object o = null;
        try {
            o = ois.readObject();
        } catch (ClassNotFoundException e) {
            o = new NullMessage(bytes);
        }
        return (Message) o;
    }

    public final byte[] toBytes() {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
            )
        {
            oos.writeObject(this);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return
                "Type - " + getClass().getName() + "\n" +
                "Node - " + Utils.keyToString(id) + "\n" +
                "Port - " + port + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return getPort() == message.getPort() && Arrays.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getPort());
        result = 31 * result + Arrays.hashCode(getId());
        return result;
    }
}
