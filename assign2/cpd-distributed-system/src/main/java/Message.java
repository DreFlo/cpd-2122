import java.io.*;
import java.util.Objects;

public class Message implements Serializable{
    String header;
    Object body;

    Message(String header, Object body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }

    public static String getHeaderMsg(char[] nodeId, int port) {
        return "NODE:\t" + Utils.keyToString(nodeId) + "\n" +
                "PORT:\t" + port + "\n";
    }

    public static Message fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
        Object o = ois.readObject();
        return (Message) o;
    }

    byte[] toBytes() {
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
