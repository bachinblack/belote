/*
    The goal of this class is to keep a list of the available players with their names
    and to handle the chat while players aren't in game.

 */

import com.esotericsoftware.kryonet.Connection;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.CopyOnWriteArrayList;

class Player {
    String      name;
    Connection  con;
    char[]      played = new char[32];

    Player(Connection _con, String _name) {
        con = _con;
        name = _name;
    }
}

class WaitingRoom {
    private CopyOnWriteArrayList<Player>    players = new CopyOnWriteArrayList<>();
    private List<GameInstance>              gth = new ArrayList<>();
    private static SimpleDateFormat         dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
    private static Date                     date = new Date();

    void addPlayer(Connection con, String name) {
        Msg msg = new Msg();
        System.out.println("Adding " + name);

        msg.text = addDate( name + " joined the room. There are now " + (players.size() + 1) + "/4 players in the room.");
        for (Player p : players) {
            p.con.sendTCP(msg);
        }
        players.add(new Player(con, name));
        if (players.size() >= 4)
            addGame();
    }

    boolean isPlayerRegistered(String name) {
        for (Player p : players) {
            if (p.name.equals(name))
                return true;
        }
        return false;
    }

    void sendChatMessage(Msg msg) {
        msg.text = addDate(msg.text);
        for (Player p : players) {
            p.con.sendTCP(msg);
        }
    }

    static String addDate(String msg) {
        date.setTime(System.currentTimeMillis());
        msg = dateFormat.format(date) + msg;
        return msg;
    }

    void removePlayer(Connection con) {
        Msg msg = new Msg();
        boolean found = false;

        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i).con == con) {
                msg.text = addDate( players.get(i).name + " left the room. There are now " + (players.size() - 1) + "/4 players in the room.");
                players.remove(i);
                found = true;
            }
        }
        if (!found) {
            for (GameInstance th : gth) {
                if ((th.seekPlayer(con)))
                    break;

            }
        }
        if (msg.text == null) return ;
        for (Player p : players) {
            p.con.sendTCP(msg);
        }
    }

    private void addGame() {
        GameInstance  gt = new GameInstance(players.get(0), players.get(1), players.get(2), players.get(3));

        gt.setId((char)gth.size());
        gth.add(gt);
        players.remove(0);
        players.remove(0);
        players.remove(0);
        players.remove(0);
        gt.run();
    }

    void waitForGame() {
        int i;

        while (true) {
            i = 0;
            System.out.println("There are " + players.size() + " players in the room. " + gth.size() + " games are currently being played.");
            while (i < gth.size()) {
                if (gth.get(i).gameOver) {
                    appendPlayers(gth.get(i).getPlayers());
                    gth.remove(i);
                    if (players.size() >= 4)
                        addGame();
                }
                ++i;
            }
            try {
                Thread.sleep(4000);
            } catch (java.lang.InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendPlayers(Player[] np) {
        for(Player p : np) {
            if (!p.name.equals(""))
                players.add(p);
        }
    }

    void dispatchCommand(GameCommand com) {
        for (GameInstance gt : gth) {
            if (gt.getId() == com.meta) {
                gt.processResponse(com.data);
                break;
            }
        }
    }
}
