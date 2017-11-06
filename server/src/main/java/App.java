
public class App {

    private static int port = 27960;

    public static void main(String[] args){

        WaitingRoom room = new WaitingRoom();
        ServerNetwork net;

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
            System.out.println("Using port { " + args[0] + " }.");
        }
        else {
            System.out.println("No arguments given, taking default port { 27960 }.");
        }

        net = new ServerNetwork(port);
        net.setListeners(room);

        System.out.println("Launching waiting loop");
        room.waitForGame();
    }
}