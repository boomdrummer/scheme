package scheme;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Utils {

    static Object cons(Object o1, Object o2) {
        return new Pair(o1, o2);
    }

    static Object car(Object pair) {
        if (pair instanceof Pair) {
            return ((Pair) pair).first;
        } else {
            throw new SyntaxException("expect pair type ...");
        }
    }

    static Object cdr(Object pair) {
        if (pair instanceof Pair) {
            return ((Pair) pair).second;
        } else {
            throw new SyntaxException("expect pair type...");
        }
    }

    static Object map(Object o, Function f) {
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


    private static void foreach(Object o, Consumer f) {
        while (o instanceof Pair) {
            Pair p = (Pair) o;
            f.accept(p.first);
            o = p.second;
        }
    }

    static Object cadr(Object pair) {
        return car(cdr(pair));
    }

    static Object caddr(Object pair) {
        return car((cdr(cdr(pair))));
    }

    static Object cadddr(Object pair) {
        return car(cdr(cdr(cdr(pair))));
    }

    static Object cddr(Object pair) {
        return cdr(cdr(pair));
    }

    static boolean isLast(Object pair) {
        if (pair instanceof Pair) {
            return cdr(pair) == null;
        } else {
            throw new SyntaxException("expect pair type..");
        }
    }

    static Function unaryAdapter(Function function) {
        return p -> function.apply(car(p));
    }

    static Function binaryAdapter(BiFunction function) {
        return p -> function.apply(car(p), cadr(p));
    }

    static Function numericAdapter(BiFunction<Double, Double, Object> function) {
        return p -> function.apply((Double) car(p), (Double) cadr(p));
    }


    /**
     * if object is false return false,else return true
     *
     * @param object whatever
     * @return if object is false return false,else return true
     */
    static boolean isTrue(Object object) {
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
        while (list instanceof Pair) {
            length++;
            list = ((Pair) list).second;
        }
        return length;
    }


    static void print(String s) {
        System.out.print(s);
    }
}
