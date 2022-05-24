package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.GetMessage;
import store.storeRecords.ClusterNodeInformation;
import store.storeRecords.Value;

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
            Value value = getStore().get(getMessage().getKey());
            getResponseSocket().getOutputStream().write(value.toBytes());
            getResponseSocket().close();
        }
        else {
            Socket socket = new Socket(nodeInformation.ipAddress(), nodeInformation.port());
            socket.getOutputStream().write(getMessage().toBytes());
            byte[] valueReceived = socket.getInputStream().readAllBytes();
            socket.close();
            getResponseSocket().getOutputStream().write(valueReceived);
            getResponseSocket().close();
        }
    }
}
