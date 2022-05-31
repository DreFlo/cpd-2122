package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.DeleteMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;

public class DeleteMessageHandler extends MessageHandler<DeleteMessage> {
    public DeleteMessageHandler(Store store, DeleteMessage message, Socket responseSocket) {
        super(store, message, responseSocket);
    }

    @Override
    public void handle() throws IOException {
        float keyAngle = Utils.getAngle(getMessage().getKey());
        ClusterNodeInformation nodeInformation =
                Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), keyAngle);

        if(nodeInformation.id().equals(getStore().getId())){
            String result = getStore().delete(getMessage().getKey());
            ClusterNodeInformation firstSuccessor =
                    Utils.getSuccessor(getStore().getClusterNodes().stream().toList(), getStore().getId());
            Socket socket = new Socket(firstSuccessor.ipAddress(), firstSuccessor.port());
            getStore().sendTCP(new DeleteMessage(getStore().getPort(), getMessage().getKey()), socket);

            ClusterNodeInformation secondSuccessor =
                    Utils.getSuccessor(getStore().getClusterNodes().stream().toList(), firstSuccessor.id());
            socket = new Socket(secondSuccessor.ipAddress(), secondSuccessor.port());
            getStore().sendTCP(new DeleteMessage(getStore().getPort(), getMessage().getKey()), socket);

            getResponseSocket().getOutputStream().write(result.getBytes());
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
