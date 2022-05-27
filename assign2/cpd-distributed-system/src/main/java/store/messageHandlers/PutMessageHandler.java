package store.messageHandlers;

import store.Store;
import store.Utils;
import store.messages.PutMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;

public class PutMessageHandler extends MessageHandler<PutMessage> {
    public PutMessageHandler(Store store, PutMessage message, Socket responseSocket) {
        super(store, message, responseSocket);
    }

    @Override
    public void handle() throws IOException {
        float keyAngle = Utils.getAngle(getMessage().getKey());
        ClusterNodeInformation nodeInformation =
                Utils.getClosestNode(getStore().getClusterNodes().stream().toList(), keyAngle);
        if(nodeInformation.id().equals(getStore().getId())){
            String result = getStore().put(getMessage().getKey(), getMessage().getValue());
            ClusterNodeInformation firstSuccessor = Utils.sendSuccessorKey(
                    getStore(),
                    getStore().getId(),
                    new AbstractMap.SimpleEntry<>(getMessage().getKey(), getMessage().getValue()));
            if(firstSuccessor != null){
                Utils.sendSuccessorKey(
                        getStore(),
                        firstSuccessor.id(),
                        new AbstractMap.SimpleEntry<>(getMessage().getKey(), getMessage().getValue()));
            }
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
