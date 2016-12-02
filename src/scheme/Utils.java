package scheme;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Utils {

    static Object cons(Object o1, Object o2) {
        return new Pair(o1, o2);
    }

    static Object car(Object pair) throws SyntaxException {
        if (pair instanceof Pair) {
            return ((Pair) pair).first;
        } else {
            throw new SyntaxException("expect pair type ...");
        }
    }

    static Object cdr(Object pair) throws SyntaxException {
        if (pair instanceof Pair) {
            return ((Pair) pair).second;
        } else {
            throw new SyntaxException("expect pair type...");
        }
    }

    static Object map(Object o, Func f) throws SyntaxException {
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

    static Object cadr(Object pair) throws SyntaxException {
        return car(cdr(pair));
    }

    static Object caddr(Object pair) throws SyntaxException {
        return car((cdr(cdr(pair))));
    }

    static Object cadddr(Object pair) throws SyntaxException {
        return car(cdr(cdr(cdr(pair))));
    }

    static Object cddr(Object pair) throws SyntaxException {
        return cdr(cdr(pair));
    }

    static boolean isLast(Object pair) throws SyntaxException {
        if (pair instanceof Pair) {
            return cdr(pair) == null;
        } else {
            throw new SyntaxException("expect pair type..");
        }
    }

    static Func unaryAdapter(Func fun) {
        return p -> fun.apply(car(p));
    }

    static Func binaryAdapter(BiFunction function) {
        return p -> function.apply(car(p), cadr(p));
    }

    static Func numericAdapter(BiFunction<Double, Double, Object> function) {
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

    static void println(String s) {
        System.out.println(s);
    }

    static void warn(String s) {
        System.err.println(s);
    }
}
