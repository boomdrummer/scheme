package scheme;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Utils {

    public static Object cons(Object o1, Object o2) {
        return new Pair(o1, o2);
    }

    public static Object car(Object pair) {
        if (pair instanceof Pair) {
            return ((Pair) pair).first;
        } else {
            throw new SyntaxException("expect pair type ...");
        }
    }

    public static Object cdr(Object pair) {
        if (pair instanceof Pair) {
            return ((Pair) pair).second;
        } else {
            throw new SyntaxException("expect pair type...");
        }
    }

    public static Object map(Object o, Function f) {
        if (o instanceof Pair) {
            Pair head = new Pair(null, null);
            Pair prev = head;
            Object cur = o;
            while (cur != null && cur instanceof Pair) {
                Pair p = (Pair) cur;
                prev.second = new Pair(f.apply(p.first), null);
                prev = (Pair) prev.second;
                cur = p.second;
            }
            return head.second;

        } else {
            throw new SyntaxException("expect pair type...");
        }
    }


    public static void foreach(Object o, Consumer f) {
        while (o instanceof Pair) {
            Pair p = (Pair) o;
            f.accept(p.first);
            o = p.second;
        }
    }

    public static Object cadr(Object pair) {
        return car(cdr(pair));
    }

    public static Object caddr(Object pair) {
        return car((cdr(cdr(pair))));
    }

    public static Object cadddr(Object pair) {
        return car(cdr(cdr(cdr(pair))));
    }

    static Function unaryAdapter(Function function) {
        return p -> function.apply(car(p));
    }

    static Function binaryAdapter(BiFunction function) {
        return p -> function.apply(car(p), cadr(p));
    }

    static Function numericAdapter(BiFunction<Double, Double, Object> function) {
        return p -> function.apply((Double)car(p), (Double)cadr(p));
    }


    /**
     * if object is false return false,else return true
     *
     * @param object whatever
     * @return if object is false return false,else return true
     */
    public static boolean isTrue(Object object) {
        return !(object instanceof Boolean && !((boolean) object));
    }

    public static Pair list(Object... objects) {
        Pair head = new Pair(null, null);
        Pair prev = head;
        for (Object object : objects) {
            prev.second = new Pair(object, null);
            prev = (Pair) prev.second;
        }
        return (Pair) head.second;
    }

    /**
     * if param is a list type ,return it's length
     *
     * @param list linked pair
     * @return length of list
     */
    static int length(Object list) {
        int length = 0;
        while (list != null && list instanceof Pair) {
            length++;
            list = ((Pair) list).second;
        }

        if (list == null) {
            return length;
        } else {
            throw new RuntimeException("not a list");
        }
    }

    public static void main(String[] args) {
        Pair list = list(1, 2, 3, 4, 5);
        foreach(map(list, e -> e instanceof Integer ? (int) e + 1 : 0), System.out::print);

    }
}
