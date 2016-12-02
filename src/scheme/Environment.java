package scheme;

import parser.Lexer;
import parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import static scheme.Interpreter.eval;
import static scheme.Utils.*;

class Environment {
    static final Environment ENV0 = init();
    private HashMap<String, Object> map = new HashMap<>();
    private Environment parent;

    private Environment() {}
    private Environment(Environment parent){
        this.parent = parent;
    }

    private static Environment init() {
        try {
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

            Path path = Paths.get("primitive.init");
            InputStream in = null;
            try {
                in = Files.newInputStream(path);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("primitive.init file not exists");
                System.exit(2);
            }
            Parser parser = new Parser(in);
            Object o = parser.parse();
            while (o != Lexer.EOF) {
                eval(o, env);
                o = parser.parse();
            }
            return env;
        } catch (SyntaxException e) {
            e.printStackTrace();
            System.exit(2);
            return null;
        }
    }

    Environment extend(Object vars, Object vals) throws SyntaxException {
        Environment env = new Environment(this);
        if (length(vals) != length(vals)) {
            throw new SyntaxException("variables and values not match");
        }
        Pair variables = (Pair) vars;
        Pair values = (Pair) vals;
        while (variables != null) {
            env.define(variables.first, values.first);
            variables = (Pair) variables.second;
            values = (Pair) values.second;
        }
        return env;
    }

    Object lookup(Object var) throws SyntaxException {
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

    String define(Object var, Object val) throws SyntaxException {
        if (var instanceof String) {
            map.put((String) var, val);
        } else {
            throw new SyntaxException("expect a string...");
        }
        return (String) var;
    }



}
