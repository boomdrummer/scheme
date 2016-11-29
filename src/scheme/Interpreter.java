package scheme;

import parser.Lexer;
import parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static scheme.Utils.*;

@SuppressWarnings("unchecked")
public class Interpreter {
    private Environment env0 = Environment.INIT_ENV;

    public Interpreter() {
        init();
    }

    void init() {
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
            eval(o, env0);
            o = parser.parse();
        }
    }

    Object eval(Object exp, Environment env) {
        if (exp instanceof String) {
            return env.lookup(exp);
        } else if (exp instanceof Pair) {
            Object first = car(exp);
            if (first == "if") {
                Object pred = cadr(exp);
                Object e1 = caddr(exp);
                Object e2 = cadddr(exp);
                return isTrue(eval(pred, env)) ? eval(e1, env) : eval(e2, env);
            } else if (first == "define") {
                Object second = cadr(exp);
                if (second instanceof Pair) { // define function
                    Object var = car(second);
                    Object params = cdr(second);
                    Object body = caddr(exp);
                    return env.define(var, new Closure(params, body, env));
                } else if (second instanceof String) {
                    return env.define(second, eval(caddr(exp), env));
                } else {
                    throw new SyntaxException("illege define ..");
                }
            } else if (first == "lambda") {
                Object params = cadr(exp);
                Object body = caddr(exp);
                return new Closure(params, body, env);
            } else {
                Object fun = eval(first, env);
                Object params = map(cdr(exp), o -> eval(o, env));
                if (fun instanceof Closure) {
                    return apply((Closure) fun, params);
                } else if (fun instanceof Function) {
                    return ((Function) fun).apply(params);
                }
            }
        }

        return exp;

    }

    Object apply(Closure closure, Object vals) {

        return eval(closure.body, new Environment(closure.params, vals, closure.env));
    }

    public static void main(String[] args) {
        Path path = Paths.get("test.txt");
        try {
            InputStream inputStream = Files.newInputStream(path);
            Parser parser = new Parser(inputStream);
            Interpreter interpreter = new Interpreter();
            Object exp = parser.parse();
            while (exp != Lexer.EOF) {
                System.out.println(interpreter.eval(exp,Environment.INIT_ENV));
                exp = parser.parse();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
