import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.util.HashMap;

public class BeeAgent extends Agent {
    private int x = 400;
    private int y = 300;
    private int nectar = 0;
    private int maxNectar = 5;
    private String targetFlower = "";
    private int targetX = 0;
    private int targetY = 0;
    private int hiveX = 400;
    private int hiveY = 300;
    private boolean returningToHive = false;
    private int collectionRange = 40;
    private int hiveRange = 50;
    private HashMap<String, int[]> knownFlowers = new HashMap<String, int[]>();
    private long lastCollectionTime = 0;
    private int collectionCooldown = 1000;
    private boolean stopCollection = false;

    protected void setup() {
        System.out.println(getLocalName() + " started at hive");

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }

        ACLMessage pingMsg = new ACLMessage(ACLMessage.INFORM);
        pingMsg.addReceiver(new AID("hive", AID.ISLOCALNAME));
        pingMsg.setContent("BEE_READY");
        send(pingMsg);

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    if (content.startsWith("FLOWER:")) {
                        String[] parts = content.split(":");
                        String flowerName = parts[1];
                        int fx = Integer.parseInt(parts[2]);
                        int fy = Integer.parseInt(parts[3]);
                        knownFlowers.put(flowerName, new int[] { fx, fy });
                        System.out.println(getLocalName() + " learned about " + flowerName);
                    } else if (content.equals("STOP_COLLECTION")) {
                        stopCollection = true;
                        System.out.println(getLocalName() + " received STOP signal - ending collection");
                        targetFlower = "";
                        returningToHive = false;
                    } else if (content.equals("HIVE_FULL")) {
                        stopCollection = true;
                        System.out.println(getLocalName() + " informed hive is FULL - stopping");
                        nectar = 0;
                    }
                } else {
                    block();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 200) {
            protected void onTick() {
                if (!stopCollection) {
                    move();
                    collectOrDeposit();
                }
                sendPosition();
            }
        });
    }

    private void move() {
        if (returningToHive) {
            moveToward(hiveX, hiveY, 15);
        } else if (!targetFlower.isEmpty()) {
            moveToward(targetX, targetY, 15);
        } else {
            if (!knownFlowers.isEmpty()) {
                Object[] flowers = knownFlowers.keySet().toArray();
                targetFlower = (String) flowers[(int) (Math.random() * flowers.length)];
                int[] pos = knownFlowers.get(targetFlower);
                targetX = pos[0];
                targetY = pos[1];
                System.out.println(getLocalName() + " heading to " + targetFlower);
            } else {
                x += (int) (Math.random() * 20 - 10);
                y += (int) (Math.random() * 20 - 10);
            }
        }

        if (x < 20)
            x = 20;
        if (x > 780)
            x = 780;
        if (y < 40)
            y = 40;
        if (y > 560)
            y = 560;
    }

    private void collectOrDeposit() {
        long currentTime = System.currentTimeMillis();

        if (returningToHive) {
            double distToHive = Math.sqrt(Math.pow(x - hiveX, 2) + Math.pow(y - hiveY, 2));
            if (distToHive < hiveRange && nectar > 0) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("hive", AID.ISLOCALNAME));
                msg.setContent("DEPOSIT:" + nectar);
                send(msg);
                System.out.println(">>> " + getLocalName() + " DEPOSITED " + nectar + " nectar at HIVE! <<<");
                nectar = 0;
                returningToHive = false;
                targetFlower = "";
            }
        } else if (!targetFlower.isEmpty() && nectar < maxNectar) {
            double distToFlower = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));

            if (distToFlower < collectionRange && (currentTime - lastCollectionTime) > collectionCooldown) {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID(targetFlower, AID.ISLOCALNAME));
                msg.setContent("TAKE1");
                send(msg);

                nectar++;
                lastCollectionTime = currentTime;
                System.out.println(getLocalName() + " collected from " + targetFlower + "! Now carrying: " + nectar
                        + "/" + maxNectar);

                if (nectar >= maxNectar) {
                    System.out.println(">>> " + getLocalName() + " is FULL! Returning to hive <<<");
                    returningToHive = true;
                    targetFlower = "";
                }
            }
        }
    }

    private void moveToward(int targetX, int targetY, int speed) {
        int dx = targetX - x;
        int dy = targetY - y;

        if (Math.abs(dx) > speed) {
            x += (dx > 0) ? speed : -speed;
        } else {
            x = targetX;
        }

        if (Math.abs(dy) > speed) {
            y += (dy > 0) ? speed : -speed;
        } else {
            y = targetY;
        }
    }

    private void sendPosition() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("gui", AID.ISLOCALNAME));
        String status = stopCollection ? "stopped"
                : (returningToHive ? "returning" : (nectar > 0 ? "carrying" : "searching"));
        msg.setContent(getLocalName() + " " + x + " " + y + " " + nectar + " " + status);
        send(msg);
    }
}