package scheme;

import java.util.HashMap;
import java.util.Objects;

import static scheme.Utils.*;

public class Environment {
    static final Environment ENV0 = init();
    private HashMap<String, Object> map = new HashMap<>();
    private Environment parent;
    private Environment() {
    }

    public Environment(Object vars, Object vals, Environment env) {
        if (length(vals) != length(vals)) {
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

    private static Environment init() {
        Environment env = new Environment();
        env.define("()", null);
        env.define("car", unaryAdapter(Utils::car));
        env.define("cdr", unaryAdapter(Utils::cdr));
        env.define("null?", unaryAdapter(Objects::isNull));
        env.define("cons", binaryAdapter(Utils::cons));
        env.define("eq?", binaryAdapter((a, b) -> a == b));
        env.define("equal", binaryAdapter(Object::equals));
        env.define("+", numericAdapter((a, b) -> a + b));
        env.define("-", numericAdapter((a, b) -> a - b));
        env.define("*", numericAdapter((a, b) -> a * b));
        env.define("/", numericAdapter((a, b) -> a / b));
        env.define("%", numericAdapter((a, b) -> a % b));
        env.define("=", numericAdapter((a, b) -> Math.abs(a - b) < 0.00000001));
        env.define(">", numericAdapter((a, b) -> a > b));
        env.define("<", numericAdapter((a, b) -> a < b));
        return env;
    }

    Object lookup(Object var) {
        if (var instanceof String) {
            if (map.containsKey(var)) {
                return map.get(var);
            } else {
                if (parent != null) {
                    return parent.lookup(var);
                } else {
                    throw new SyntaxException("undefined variable:" + var);
                }
            }
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
        return (String) var;
    }



}
