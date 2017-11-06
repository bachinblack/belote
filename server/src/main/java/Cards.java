import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Cards {

    private static String[] _Value = { "Ace", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King" };
    private static String[] _Color = { "Heart", "Diamond", "Club", "Spade" };
    private static char[]   _Deck = new char[32];

    private static short[]  _NAValue = { 11, 0, 0, 0, 10, 2, 3, 4 };
    private static short[]  _AValue = { 11, 0, 0, 14, 10, 20, 3, 4 };
    private static char     _trump = 5;

    public String toString(char id) { return _Value[id / 4] + " of " + _Color[id / 4]; }

    public static char[] getHand(int i) {
        char[]  hand = new char[8];
        int k = 7;

        i = i * 8 - 1;
        for (int j= i + 8;j>i; --j) {
            hand[k] = _Deck[j];
            --k;
        }
        return hand;
    }

    public static void fillDeck() {
        char k = 0;

        for (char i = 0; i < 32; ++i) {
            _Deck[k++] = i;
        }
        shuffleArray();
    }

    static void shuffleArray()
    {
        char    a;
        Random  rnd = ThreadLocalRandom.current();

        for (char i = (char)(_Deck.length - 1); i > 0; --i) {
            char index = (char)rnd.nextInt(i + 1);
            a = _Deck[index];
            _Deck[index] = _Deck[i];
            _Deck[i] = a;
        }
    }

    public static int getScore(char id) { return (id % 4 == _trump ? _AValue[id / 4] : _NAValue[id /4]); }

    public static String colToString(char c) { return _Color[c]; }

    public static void setTrump(char nt) { _trump = nt; }
}