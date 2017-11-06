import java.io.IOException;

public class App {

    private static String         ip = "localhost";
    private static int            port = 27960;
    public static ClientNetwork   net;

    public static void main(String[] args) throws IOException {
        Game game = new Game();

        if (args.length >= 2) {
               ip = args[0];
               port = Integer.parseInt(args[1]);
        }
        else {
            System.out.println("No arguments given, taking default ip/port { localhost | 27960 }");
        }
        net = new ClientNetwork(ip, port, game.getName());
        net.setListeners(game);
        game.setClient(net.getClient());
        game.waitLoop();
    }


}
