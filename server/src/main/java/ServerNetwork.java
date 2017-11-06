import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerNetwork {

    private Server server = new Server();

    public ServerNetwork(int port) {
        try {
            server.start();
            server.bind(port, port);
        } catch (java.io.IOException e) {
            System.out.println("Couldn't setup the server with port " + port + ", maybe this port is unavailable.");
            return;
        }
        Kryo kryo = server.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        kryo.register(Msg.class);
        kryo.register(InitMsg.class);
        kryo.register(char[].class);
        kryo.register(String[].class);
        kryo.register(GameCommand.class);
}

    public void setListeners(WaitingRoom room) {
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof SomeRequest) {
                    SomeResponse response = new SomeResponse();
                    SomeRequest request = (SomeRequest)object;

                    if (!room.isPlayerRegistered(request.text)) {
                        response.text = "[server] Welcome, you are currently in the waiting room, waiting for a game to begin.";
                        connection.sendTCP(response);
                        room.addPlayer(connection, request.text);
                    }
                    else {
                        response.text = "[!server] Seems like an other player is already using this name. Try an other one";
                        connection.sendTCP(response);
                    }
                } else if (object instanceof Msg) {
                    room.sendChatMessage((Msg)object);
                } else if (object instanceof GameCommand) {
                    room.dispatchCommand((GameCommand)object);
                }
            }
        });
        server.addListener(new Listener() {
            public void disconnected (Connection connection) {
                room.removePlayer(connection);
            }
        });
    }
}
