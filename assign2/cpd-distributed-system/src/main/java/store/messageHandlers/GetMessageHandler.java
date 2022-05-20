package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.GetMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class GetMessageHandler extends MessageHandler<GetMessage> {
    public GetMessageHandler(Store store, GetMessage message, Socket responseSocket) {
        super(store, message, responseSocket);
    }

    @Override
    public void handle() throws IOException {
        float keyAngle = Utils.getAngle(getMessage().getKey());
        ClusterNodeInformation nodeInformation =
                Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), keyAngle);

        if(nodeInformation.id().equals(getStore().getId())){
            System.out.println("I1");
            byte[] value = getStore().get(getMessage().getKey());
            System.out.println("I2: " + new String(value));
            getResponseSocket().getOutputStream().write(value);
            System.out.println("I3");
            getResponseSocket().close();
            System.out.println("I4");
        }
        else {
            System.out.println("E1");
            Socket socket = new Socket(nodeInformation.ipAddress(), nodeInformation.port());
            System.out.println("E2");
            getStore().sendTCP(getMessage(), socket);
            System.out.println("E3");
            byte[] valueReceived = socket.getInputStream().readAllBytes();
            System.out.println("E4: " + new String(valueReceived));
            getResponseSocket().getOutputStream().write(valueReceived);
            System.out.println("E5");
            getResponseSocket().close();
            System.out.println("E6");
        }
    }
}
