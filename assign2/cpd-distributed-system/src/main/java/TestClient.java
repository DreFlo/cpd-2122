import static java.lang.Integer.parseInt;

import store.Store;
import store.Utils;
import store.messages.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
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
                System.out.println("Test Client Put\nKey: " + key);
                //O PROXIMO SO PRA TESTES
                System.out.println("Angle: " + Utils.getAngle(key));

                PutMessage putMessage = new PutMessage("", port, key, value);

                Socket socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(putMessage.toBytes());
                socket.close();
                break;
            case "get":
                String hexSymbols = args[2];
                GetMessage getMessage = new GetMessage("", port, hexSymbols);
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(getMessage.toBytes());

                byte[] valueReceived = socket.getInputStream().readAllBytes();
                System.out.println("Test Client Get:\n" + new String(valueReceived));
                socket.close();
                break;
            case "delete":
                hexSymbols = args[2];
                DeleteMessage deleteMessage = new DeleteMessage("", port, hexSymbols);
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                socket.getOutputStream().write(deleteMessage.toBytes());
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
