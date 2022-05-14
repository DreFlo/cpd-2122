package store.messages;

import java.io.*;
import store.Utils;

public abstract class Message implements Serializable {
    String header;
    Object body;
    char[] id;
    int port;

    public Message(String header, Object body, char[] id, int port) {
        this.header = header;
        this.body = body;
        this.id = id;
        this.port = port;
    }

    public String getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }

    public char[] getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public static String getHeaderMsg(char[] nodeId, int port) {
        return "NODE\t" + Utils.keyToString(nodeId) + "\n" +
                "PORT\t" + port + "\n";
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

    public byte[] toBytes() {
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

    @Override
    public String toString() {
        return header;
    }

    @Override
    public int hashCode() {
        return  header.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return getHeader().equals(message.getHeader());
    }
}
