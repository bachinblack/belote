import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientNetwork {
    private Client client = new Client();

    public Client getClient() { return client; }

    public ClientNetwork(String ip, int port, String name) {
        Kryo kryo = client.getKryo();
        new Thread(client).start();

        try {
            client.connect(5000, ip, port, port);
        } catch (java.io.IOException e) {
            System.out.println("Couldn't connect to server, maybe it isn't running or the ip/port are wrong");
            System.exit(1);
        }

        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        kryo.register(Msg.class);
        kryo.register(InitMsg.class);
        kryo.register(char[].class);
        kryo.register(String[].class);
        kryo.register(GameCommand.class);

        SomeRequest request = new SomeRequest();
        request.text = name;
        client.sendTCP(request);
    }

    public void setListeners(Game game) {
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {

                if (object instanceof SomeResponse) {
                    SomeResponse response = (SomeResponse)object;
                    System.out.println(response.text);
                    if (response.text.substring(0, 9).equals("[!server]"))
                        System.exit(1);
                }
                else if (object instanceof Msg) {
                    Msg msg = (Msg)object;
                    System.out.println(msg.text);
                }
                else if (object instanceof InitMsg) {
                    game.InitData((InitMsg)object);
                }
                else if (object instanceof GameCommand) {
                    game.receiveCommand((GameCommand)object);
                }
            }
        });
    }
}
