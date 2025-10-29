import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class FlowerAgent extends Agent {
    private int x;
    private int y;
    private int nectar = 10;
    private int maxNectar = 10;
    private boolean blooming = true;

    protected void setup() {
        String name = getLocalName();

        if (name.equals("flower1")) {
            x = 150;
            y = 100;
        } else if (name.equals("flower2")) {
            x = 650;
            y = 150;
        } else if (name.equals("flower3")) {
            x = 200;
            y = 450;
        } else if (name.equals("flower4")) {
            x = 600;
            y = 400;
        } else if (name.equals("flower5")) {
            x = 100;
            y = 250;
        } else if (name.equals("flower6")) {
            x = 700;
            y = 300;
        } else {
            x = 100 + (int) (Math.random() * 600);
            y = 100 + (int) (Math.random() * 400);
        }

        System.out.println(getLocalName() + " bloomed at (" + x + "," + y + ") with " + nectar + " nectar");

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("hive", AID.ISLOCALNAME));
        msg.setContent("FLOWER:" + getLocalName() + ":" + x + ":" + y);
        send(msg);

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if ("TAKE1".equals(msg.getContent())) {
                        if (nectar > 0) {
                            nectar--;
                            System.out
                                    .println(getLocalName() + " gave 1 nectar. Remaining: " + nectar + "/" + maxNectar);
                            sendStatus();
                        }
                    }
                } else {
                    block();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                if (blooming && nectar < maxNectar) {
                    nectar++;
                    System.out.println(getLocalName() + " regenerated 1 nectar. Total: " + nectar + "/" + maxNectar);
                }
                sendStatus();
            }
        });

        sendStatus();
    }

    private void sendStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("gui", AID.ISLOCALNAME));
        String status = nectar > 0 ? "blooming" : "empty";
        msg.setContent(getLocalName() + " " + x + " " + y + " " + nectar + " " + status);
        send(msg);
    }
}