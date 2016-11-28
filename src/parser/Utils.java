package parser;

public class Utils {
    public static Pair list(Object... objects) {
        Pair head = new Pair(null, null);
        Pair prev = head;
        for (Object object : objects) {
            prev.second = new Pair(object, null);
            prev = (Pair) prev.second;
        }
        return (Pair) head.second;
    }
}
