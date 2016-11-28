package scheme;

import java.util.HashMap;

import static scheme.Utils.length;

public class Environment {
    private HashMap<String, Object> map = new HashMap<>();
    Environment parent;


    private Environment() {

    }
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

            if (result == null) {
                throw new SyntaxException("undefined variable: " + var);
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

    static final Environment EMPTY_ENV = new Environment();

}
