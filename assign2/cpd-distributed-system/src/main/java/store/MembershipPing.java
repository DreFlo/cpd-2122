package store;

import store.messages.MembershipMessage;

import java.io.IOException;

public class MembershipPing implements Runnable{
    private final Store store;

    public MembershipPing(Store store) {
        this.store = store;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1_000L * store.getClusterNodes().size());
                if (store.isStaleNode()) continue;
                store.sendUDP(new MembershipMessage(store.getId(), store.getPort(), store.getIpAddress(), store.getMostRecentMembershipEvents(), store.getClusterNodes()), store.getGroup());
            } catch (IOException e) {
                System.out.println("Could not broadcast membership message. Error: " + e);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
