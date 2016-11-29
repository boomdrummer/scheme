package scheme;

import java.util.HashMap;
import java.util.Objects;

import static scheme.Utils.*;

public class Environment {
    private HashMap<String, Object> map = new HashMap<>();
    Environment parent;
    static Environment INIT_ENV = new Environment().init();

    public Environment() {}

    public Environment(Object vars, Object vals, Environment env) {
        if (length(vals) != length(vals) && !(vars instanceof Pair) && !(vals instanceof Pair)) {
            throw new SyntaxException("variables and values not match");
        }
        Pair variables = (Pair) vars;
        Pair values = (Pair) vals;
        while (variables != null) {
            map.put((String) variables.first, values.first);
            variables = (Pair) variables.second;
            values = (Pair) values.second;
        }
        parent = env;
    }


    Object lookup(Object var) {
        if (var instanceof String) {
            Object result = map.get(var);
            if (result == null && parent != null) {
                result = parent.lookup(var);
            }
            return result;
        } else {
            throw new SyntaxException("expect a string...");
        }
    }

    String define(Object var, Object val) {
        if (var instanceof String) {
            map.put((String) var, val);
        } else {
            throw new SyntaxException("expect a string...");
        }
        return "done";
    }


    private Environment init() {
        define("()", null);
        define("car", unaryAdapter(Utils::car));
        define("cdr", unaryAdapter(Utils::cdr));
        define("null?", unaryAdapter(Objects::isNull));
        define("quote", unaryAdapter(x -> x));
        define("cons", binaryAdapter(Utils::cons));
        define("eq?", binaryAdapter((a, b) -> a == b));
        define("equal", binaryAdapter(Object::equals));
        define("+", numericAdapter((a, b) -> a + b));
        define("-", numericAdapter((a, b) -> a - b));
        define("*", numericAdapter((a, b) -> a * b));
        define("/", numericAdapter((a, b) -> a / b));
        define("%", numericAdapter((a, b) -> a % b));
        define("=", numericAdapter(Objects::equals));
        define(">", numericAdapter((a, b) -> a > b));
        define("<", numericAdapter((a, b) -> a < b));
        return this;
    }


}
