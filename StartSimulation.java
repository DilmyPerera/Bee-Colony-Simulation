import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;

public class StartSimulation {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            AgentContainer container = rt.createMainContainer(p);

            System.out.println("Starting Bee Colony Simulation...");

            container.createNewAgent("gui", "GUIAgent", null).start();
            Thread.sleep(1000);

            container.createNewAgent("hive", "HiveAgent", null).start();
            Thread.sleep(500);

            container.createNewAgent("flower1", "FlowerAgent", null).start();
            container.createNewAgent("flower2", "FlowerAgent", null).start();
            container.createNewAgent("flower3", "FlowerAgent", null).start();
            container.createNewAgent("flower4", "FlowerAgent", null).start();
            container.createNewAgent("flower5", "FlowerAgent", null).start();
            container.createNewAgent("flower6", "FlowerAgent", null).start();

            Thread.sleep(500);

            for (int i = 1; i <= 5; i++) {
                container.createNewAgent("bee" + i, "BeeAgent", null).start();
                Thread.sleep(200);
            }

            System.out.println("Bee Colony Simulation started!");
            System.out.println("Watch bees collect nectar and return to hive!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}