import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.util.ArrayList;

public class HiveAgent extends Agent {
    private int x = 400;
    private int y = 300;
    private int totalHoney = 0;
    private int maxHoney = 60;
    private ArrayList<String> flowerLocations = new ArrayList<String>();
    private ArrayList<String> beeNames = new ArrayList<String>();
    private int flowerCount = 0;
    private boolean hiveFull = false;

    protected void setup() {
        System.out.println("Hive established at center (400, 300)");
        System.out.println("Hive capacity will be 10 * number_of_flowers");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    String senderName = msg.getSender().getLocalName();

                    if (senderName.startsWith("bee") && !beeNames.contains(senderName)) {
                        beeNames.add(senderName);
                        System.out.println("Hive registered bee: " + senderName);

                        for (String flowerInfo : flowerLocations) {
                            ACLMessage flowerMsg = new ACLMessage(ACLMessage.INFORM);
                            flowerMsg.addReceiver(new AID(senderName, AID.ISLOCALNAME));
                            flowerMsg.setContent(flowerInfo);
                            send(flowerMsg);
                        }
                        System.out.println("Sent " + flowerLocations.size() + " flower locations to " + senderName);
                    }

                    if (content.startsWith("FLOWER:")) {
                        flowerLocations.add(content);
                        flowerCount++;
                        maxHoney = 10 * flowerCount;
                        System.out.println("Hive registered flower. Total flowers: " + flowerCount);
                        System.out.println("Hive max capacity: " + maxHoney + " honey");

                        broadcastFlowerLocation(content);
                    } else if (content.startsWith("DEPOSIT:")) {
                        if (hiveFull) {
                            // Reject deposit if hive is full
                            ACLMessage rejectMsg = new ACLMessage(ACLMessage.REFUSE);
                            rejectMsg.addReceiver(new AID(senderName, AID.ISLOCALNAME));
                            rejectMsg.setContent("HIVE_FULL");
                            send(rejectMsg);
                            return;
                        }

                        String[] parts = content.split(":");
                        int depositAmount = Integer.parseInt(parts[1]);

                        if (totalHoney + depositAmount <= maxHoney) {
                            totalHoney += depositAmount;
                            System.out.println("========================================");
                            System.out.println("Hive received " + depositAmount + " nectar from " + senderName);
                            System.out.println("Total honey: " + totalHoney + "/" + maxHoney);
                            System.out.println("========================================");
                        } else {
                            totalHoney = maxHoney; // Cap at maximum
                        }

                        if (totalHoney >= maxHoney && !hiveFull) {
                            hiveFull = true;
                            System.out.println("\n\n");
                            System.out.println("***************************************************");
                            System.out.println("***          HIVE IS FULL!                      ***");
                            System.out.println("***          MISSION ACCOMPLISHED!              ***");
                            System.out.println("***          Total honey: " + totalHoney + "/" + maxHoney
                                    + "                   ***");
                            System.out.println("***          Stopping all bees...               ***");
                            System.out.println("***************************************************");
                            System.out.println("\n\n");

                            // Broadcast STOP message to all bees
                            broadcastStopMessage();
                        }
                    }
                } else {
                    block();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 1000) {
            protected void onTick() {
                sendStatus();
            }
        });
    }

    private void broadcastFlowerLocation(String flowerInfo) {
        for (String beeName : beeNames) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID(beeName, AID.ISLOCALNAME));
            msg.setContent(flowerInfo);
            send(msg);
        }
    }

    private void broadcastStopMessage() {
        for (String beeName : beeNames) {
            ACLMessage stopMsg = new ACLMessage(ACLMessage.REQUEST);
            stopMsg.addReceiver(new AID(beeName, AID.ISLOCALNAME));
            stopMsg.setContent("STOP_COLLECTION");
            send(stopMsg);
        }
        System.out.println("STOP message sent to all " + beeNames.size() + " bees");
    }

    private void sendStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("gui", AID.ISLOCALNAME));
        String status = hiveFull ? "full" : "active";
        msg.setContent("hive " + x + " " + y + " " + totalHoney + " " + maxHoney + " " + status);
        send(msg);
    }
}