import com.esotericsoftware.kryonet.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Game {
    private char[]    cards;
    private String[]  players = new String[1];
    private char[]    board;
    private short     nCards;
    private short     nBoard;
    private char[]    trump;
    private Client    _client;
    private char      threadId = (char)-1;
    private boolean   gStart = false;
    private char      cmd = (char)-1;

    Game() {
        BufferedReader br;
        nCards = 0;
        nBoard = 0;

        System.out.println("Chose a nickname:");
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            while ((players[0] = br.readLine()).equals("") == true);
        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
        System.out.println("Okay, your nickname is [" + players[0] + "].");
    }

    private void displayCards() {
        int i = 0;
        System.out.println("----------------------------------------------");
        System.out.println("You have " + nCards + " cards left" + (nCards == 0 ? "." : ":"));
        for (char c : cards) {
            if (c != 32) {
                System.out.println(i + ": " + Cards.toString(c));
            }
            ++i;
        }
        System.out.println("----------------------------------------------");
    }

    private void displayBoard() {

        nBoard = 0;
        for (char c : board) {
            if (c != 32)
                ++nBoard;
        }
        System.out.println("----------------------------------------------");
        System.out.println("There are " + nBoard + " cards on the board" + (nBoard == 0 ? "." : ":"));
        for (char c : board) {
            if (c != 32)
                System.out.println("- " + Cards.toString(c));
        }
        System.out.println("----------------------------------------------");
    }

    private void displayPlayers() {
        System.out.println("Here are the players in your room:");

        for (String s : players) {
            System.out.println("- " + s);
        }
        System.out.println("----------------------------------------------");
    }

    void setClient(Client client) { _client = client; }

    String getName() { return (players[0]); }

    void waitLoop() throws IOException {
        Msg msg = new Msg();
        String s;
        char[] data = new char[2];

        try {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while ((s = br.readLine()).equals("!quit") == false) {
                if (!gStart) {
                    if (s.length() != 0) {
                        msg.text = players[0] + ": " + s;
                        _client.sendTCP(msg);
                    }
                } else if (cmd == 0){
                    if (s.length() == 0) {
                        data[0] = 0;
                        data[1] = 0;
                    } else {
                        data = getTrumpValues(s);
                        if (data[0] == 6) {
                            continue;
                        }
                    }
                    sendCommand(data);
                    System.out.println("Your answer have been sent...");
                    cmd = (char)-1;
                } else if (cmd == 1) {
                    if (s.length() == 0) {
                        System.out.println("You can't skip your turn, you have to chose a card.");
                        continue;
                    }
                    if ((data[0] = getCardToPlay(s)) == 32)
                        continue;
                    sendCommand(data);
                    System.out.println("Your answer have been sent...");
                    cmd = (char)-1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(char[] data) {
        GameCommand cmd = new GameCommand();

        cmd.data = data;
        cmd.meta = threadId;
        _client.sendTCP(cmd);
    }

    void receiveCommand(GameCommand com) {
        cmd = com.meta;
        switch (com.meta) {
            case 0: {
                trump = com.data;
                if (com.data[0] == 0x5)
                    System.out.println("You are the first to bet (empty for skipping):");
                else
                    System.out.println("The current bet is: " + Cards.colToString(com.data[0]) + " and " + (int)com.data[1] + " points. Type your bet (empty for skipping).");
                System.out.println("Chose a color in this list [Heart - Diamond - Club - Spade] and a score between " + (trump[1] == 0 ? 80 : (int)trump[1]) + " and 250");
                break;
            }
            case 1: {
                board = com.data;
                play();
                break;
            }
        }
    }

    private void play() {
        displayCards();
        displayBoard();
            System.out.println("Chose one of your cards by its number [0-7].");
    }

    void InitData(InitMsg msg) {
        gStart = true;
        nCards = 8;
        cards = msg.cards;
        players = msg.players;
        threadId = msg.threadId;
        displayPlayers();
        displayCards();
    }

    private char[] getTrumpValues(String s) {
        char[] data = {0, 0};
        int subValue = 0;
        int it = 0;

        for (char c : s.toCharArray()) {
            if (c == ' ')
                subValue = it;
            else if (subValue != 0 && (c < '0' || c > '9')) {
                System.out.println("The second parameter has to be an integer between " + (trump[1] == 0 ? 80 : (int)trump[1]) + " and 250");
                data[0] = 6;
                return data;
            }
            ++it;
        }
        if (subValue == 0)
            System.out.println("Your answer need to be formatted like this: [trump] [score]");
        else {
            System.out.println("trump color is:" + s.substring(0, subValue));
            data[0] = Cards.toColor(s.substring(0, subValue));
            subValue = Integer.parseInt(s.substring(subValue + 1));
            if (subValue < (trump[1] == 0 ? 80 : trump[1]) || subValue > 250) {
                data[0] = 6;
                System.out.println("The second parameter has to be an integer between " + (trump[1] == 0 ? 80 : (int)trump[1]) + " and 250");
            } else
                data[1] = (char)subValue;
        }
        return data;
    }

    private char getCardToPlay(String s) {
        char keep;
        char ret;

        if (s.length() > 1 || s.charAt(0) < '0' || s.charAt(0) > '7') {
            System.out.println("you have to select a card by its number [0-7].");
            return 32;
        }

        keep = (char)Integer.parseInt(s);
        if (cards[keep] == 32) {
            System.out.println("This card isn't available anymore. You have to chose another one.");
            return 32;
        }

        --nCards;
        ret = cards[keep];
        cards[keep] = (char)32;
        return ret;
    }
}
