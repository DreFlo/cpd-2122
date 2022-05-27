import static java.lang.Integer.parseInt;

import store.Store;
import store.Utils;
import store.messages.*;
import store.storeRecords.NullValue;
import store.storeRecords.TombstoneValue;
import store.storeRecords.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestClient {
    final String nodeAp;
    final String operation;
    String operand;

    public TestClient(String nodeAp, String operation, String operand) {
        this.nodeAp = nodeAp;
        this.operation = operation;
        this.operand = operand;
    }
    public TestClient(String nodeAp, String operation){
        this.nodeAp = nodeAp;
        this.operation = operation;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String[] arg = args[0].split(":");
        String ipAddress = arg[0];
        Integer port = parseInt(arg[1]);

        String operation = args[1];
        switch (operation){
            case "put":
                String filePath = args[2];
                Scanner scanner = new Scanner(filePath);

                ClassLoader classLoader = TestClient.class.getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream(filePath);
                byte[] value = inputStream.readAllBytes();

                String key = Utils.hash(value);

                //NAO REMOVER PRINT - OBRIGATORIO TER
                System.out.println("TestClient Put\nKey: " + key);
                //O PROXIMO SO PRA TESTES
                System.out.println("Angle: " + Utils.getAngle(key));

                Path path = Paths.get(filePath);
                Value value1 = new Value(path.getFileName().toString(), value);

                PutMessage putMessage = new PutMessage(port, key, value1);

                Socket socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(putMessage.toBytes());
                byte[] valueReceived = socket.getInputStream().readAllBytes();
                System.out.println(new String(valueReceived));
                socket.close();
                break;
            case "get":
                String hexSymbols = args[2];
                try{
                    new BigInteger(hexSymbols, 16);
                } catch (NumberFormatException e){
                    System.out.println("Hash is not in correct format.");
                    break;
                }

                GetMessage getMessage = new GetMessage(port, hexSymbols);
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(getMessage.toBytes());

                valueReceived = socket.getInputStream().readAllBytes();
                socket.close();
                Value value2 = Value.fromBytes(valueReceived);
                if(value2 instanceof TombstoneValue){
                    System.out.println("File has been deleted.");
                    break;
                }
                else if(value2 instanceof NullValue){
                    System.out.println("File doesn't exist.");
                    break;
                }

                String newFileName = "received_" + value2.getFilename();
                File file = new File(newFileName);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value2.getValue());
                fileOutputStream.close();
                System.out.println("Get file successful.\nCreated file with name: " + newFileName);
                break;
            case "delete":
                hexSymbols = args[2];
                try{
                    new BigInteger(hexSymbols, 16);
                } catch (NumberFormatException e){
                    System.out.println("Hash is not in correct format.");
                    break;
                }

                DeleteMessage deleteMessage = new DeleteMessage(port, hexSymbols);
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(deleteMessage.toBytes());
                valueReceived = socket.getInputStream().readAllBytes();
                System.out.println(new String(valueReceived));
                socket.close();
                break;
            case "join":
                TestJoinMessage testJoinMessage = new TestJoinMessage();
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(testJoinMessage.toBytes());
                socket.close();
                break;
            case "leave":
                TestLeaveMessage testLeaveMessage = new TestLeaveMessage();
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(testLeaveMessage.toBytes());
                socket.close();
                break;
            default:
                throw new IllegalArgumentException("Invalid operation");
        }

    }
}
