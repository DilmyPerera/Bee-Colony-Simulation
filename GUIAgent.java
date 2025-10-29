import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GUIAgent extends Agent {
    private JFrame frame;
    private DrawPanel panel;
    private JLabel statusLabel;
    private HashMap<String, AgentInfo> agents = new HashMap<String, AgentInfo>();

    protected void setup() {
        System.out.println("GUI started");

        frame = new JFrame("Bee Colony Simulation");
        frame.setSize(800, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new DrawPanel();
        frame.add(panel, BorderLayout.CENTER);

        statusLabel = new JLabel("Bee Colony Simulation - Bees collecting nectar!");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(255, 223, 186));
        frame.add(statusLabel, BorderLayout.NORTH);

        frame.setVisible(true);

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String[] parts = msg.getContent().split(" ");
                    if (parts.length >= 5) {
                        String name = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int value = Integer.parseInt(parts[3]);

                        String status = parts[4];
                        if (name.equals("hive") && parts.length >= 6) {
                            int maxHoney = Integer.parseInt(parts[4]);
                            status = maxHoney + " " + (parts.length > 5 ? parts[5] : "active");
                        }

                        synchronized (agents) {
                            agents.put(name, new AgentInfo(x, y, value, status));
                        }
                        updateStatus();
                        panel.repaint();
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void updateStatus() {
        int totalHoney = 0;
        int maxHoney = 60;
        int beeCount = 0;
        int flowerCount = 0;
        boolean hiveFull = false;

        synchronized (agents) {
            for (String name : agents.keySet()) {
                AgentInfo info = agents.get(name);
                if (name.equals("hive")) {
                    totalHoney = info.value;
                    if (info.status.contains("full")) {
                        hiveFull = true;
                    }
                    String[] hiveParts = info.status.split(" ");
                    if (hiveParts.length > 0) {
                        try {
                            maxHoney = Integer.parseInt(hiveParts[0]);
                        } catch (Exception e) {
                        }
                    }
                } else if (name.startsWith("bee")) {
                    beeCount++;
                } else if (name.startsWith("flower")) {
                    flowerCount++;
                }
            }
        }

        if (hiveFull) {
            statusLabel.setText("HIVE FULL! Mission Complete! Honey: " + totalHoney + "/" + maxHoney + " | Bees: "
                    + beeCount + " | Flowers: " + flowerCount);
            statusLabel.setBackground(Color.YELLOW);
        } else {
            statusLabel.setText(
                    "Honey: " + totalHoney + "/" + maxHoney + " | Bees: " + beeCount + " | Flowers: " + flowerCount);
            statusLabel.setBackground(new Color(255, 223, 186));
        }
    }

    class AgentInfo {
        int x, y, value;
        String status;

        AgentInfo(int x, int y, int value, String status) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.status = status;
        }
    }

    class DrawPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(144, 238, 144));

            g.setColor(Color.BLACK);
            g.drawString("Bee Colony & Flower Pollination", 10, 20);

            HashMap<String, AgentInfo> agentsCopy;
            synchronized (agents) {
                agentsCopy = new HashMap<String, AgentInfo>(agents);
            }

            for (String name : agentsCopy.keySet()) {
                AgentInfo info = agentsCopy.get(name);

                if (name.equals("hive")) {
                    drawHive(g, info.x, info.y, info.value);
                } else if (name.startsWith("bee")) {
                    drawBee(g, info.x, info.y, info.value, info.status);
                } else if (name.startsWith("flower")) {
                    drawFlower(g, info.x, info.y, info.value, info.status);
                }
            }
        }

        private void drawHive(Graphics g, int x, int y, int honey) {
            int[] xPoints = { x, x + 15, x + 15, x, x - 15, x - 15 };
            int[] yPoints = { y - 20, y - 10, y + 10, y + 20, y + 10, y - 10 };

            g.setColor(new Color(218, 165, 32));
            g.fillPolygon(xPoints, yPoints, 6);
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints, yPoints, 6);

            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("HIVE", x - 15, y - 25);
            g.drawString("H:" + honey, x - 12, y + 5);

            g.setColor(new Color(255, 215, 0, 50));
            g.drawOval(x - 50, y - 50, 100, 100);
        }

        private void drawBee(Graphics g, int x, int y, int nectar, String status) {
            if (status.equals("returning")) {
                g.setColor(new Color(255, 215, 0));
            } else if (nectar > 0) {
                g.setColor(new Color(255, 200, 0));
            } else {
                g.setColor(new Color(255, 223, 0));
            }

            g.fillOval(x - 6, y - 4, 12, 8);

            g.setColor(Color.BLACK);
            g.drawLine(x - 2, y - 4, x - 2, y + 4);
            g.drawLine(x + 2, y - 4, x + 2, y + 4);

            g.setColor(new Color(173, 216, 230, 150));
            g.fillOval(x - 10, y - 8, 8, 6);
            g.fillOval(x + 2, y - 8, 8, 6);

            g.setColor(Color.BLACK);
            g.drawLine(x - 3, y - 4, x - 5, y - 8);
            g.drawLine(x + 3, y - 4, x + 5, y - 8);

            if (nectar > 0) {
                g.setFont(new Font("Arial", Font.BOLD, 9));
                g.drawString("" + nectar, x + 8, y + 5);
            }
        }

        private void drawFlower(Graphics g, int x, int y, int nectar, String status) {
            if (nectar > 7) {
                g.setColor(new Color(255, 105, 180));
            } else if (nectar > 3) {
                g.setColor(new Color(255, 182, 193));
            } else {
                g.setColor(new Color(211, 211, 211));
            }

            for (int i = 0; i < 5; i++) {
                double angle = Math.toRadians(i * 72);
                int px = x + (int) (Math.cos(angle) * 8);
                int py = y + (int) (Math.sin(angle) * 8);
                g.fillOval(px - 5, py - 5, 10, 10);
            }

            g.setColor(new Color(255, 215, 0));
            g.fillOval(x - 4, y - 4, 8, 8);
            g.setColor(Color.BLACK);
            g.drawOval(x - 4, y - 4, 8, 8);

            g.setColor(new Color(34, 139, 34));
            g.drawLine(x, y + 4, x, y + 15);

            g.setColor(new Color(255, 182, 193, 50));
            g.drawOval(x - 40, y - 40, 80, 80);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            g.drawString("" + nectar, x + 10, y);
        }
    }
}