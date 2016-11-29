package scheme;

public class Pair {
    public Object first;
    public Object second;

    public Pair(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }

    @Override
    public String toString() {
        return "(" + (first == null ? " " : first.toString()) + " " + (second == null ? " " : second.toString()) + ")";
    }
}
