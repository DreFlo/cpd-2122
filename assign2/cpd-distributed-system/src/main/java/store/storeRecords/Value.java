package store.storeRecords;

import java.io.*;

public class Value implements Serializable{
    private String filename;
    private byte[] value;
    public Value(String filename, byte[] value){
        this.filename = filename;
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            bytes = bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return bytes;
    }

    public static Value fromBytes(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object o;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {}
        }
        return (Value) o;
    }
}
