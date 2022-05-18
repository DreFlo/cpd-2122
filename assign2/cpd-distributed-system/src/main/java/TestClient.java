import static java.lang.Integer.parseInt;
import store.Utils;
import store.messages.DeleteMessage;
import store.messages.GetMessage;
import store.messages.Message;
import store.messages.PutMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        Socket socket = new Socket(InetAddress.getByName(ipAddress), port);
        switch (operation){
            case "put":
                String filePath = args[2];
                Scanner scanner = new Scanner(filePath);

                ClassLoader classLoader = TestClient.class.getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream(filePath);
                String value = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                String key = Utils.hash(value);

                //NAO REMOVER PRINT - OBRIGATORIO TER
                System.out.println("Test Client Put\nKey: " + key);

                PutMessage putMessage = new PutMessage(new char[1], port, key, value);
                socket.getOutputStream().write(putMessage.toBytes());
                socket.close();
                break;
            case "get":
                String hexSymbols = args[2];
                GetMessage getMessage = new GetMessage(new char[1], port, hexSymbols);
                socket.getOutputStream().write(getMessage.toBytes());

                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                String received = reader.lines().collect(Collectors.joining("\n"));
                System.out.println("Test Client Get:\n" + received);
                socket.close();
                break;
            case "delete":
                hexSymbols = args[2];
                DeleteMessage deleteMessage = new DeleteMessage(new char[1], port, hexSymbols);
                socket.getOutputStream().write(deleteMessage.toBytes());
                socket.close();
                break;
            case "join":
            case "leave":
                break;
            default:
                throw new IllegalArgumentException("Invalid operation");
        }

    }
}
