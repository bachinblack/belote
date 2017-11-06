import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Cards {

    private static String[] _Value = { "Ace", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King" };
    private static String[] _Color = { "Heart", "Diamond", "Club", "Spade" };
    public static char[]  _Deck = new char[32];

    public static String toString(char id) { return _Value[id / 4] + " of " + _Color[id % 4]; }

    public static char toColor(String col) {
        char it = 0;
        for (String s : _Color) {
            if (s.equalsIgnoreCase(col))
                return it;
            ++it;
        }
        System.out.println("No color correspond to your choice, please retry.");
        return 6;
    }

    public static String colToString(char c) { return _Color[c]; }
}