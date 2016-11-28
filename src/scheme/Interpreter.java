package scheme;

import parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static scheme.Utils.*;

@SuppressWarnings("unchecked")
public class Interpreter {
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
                } else if (fun instanceof PrimitiveFunction) {
                    return ((PrimitiveFunction) fun).apply(params);
                } else {
                    throw new SyntaxException("not supported ... ");
                }
            }
        } else {
            return exp;
        }

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
            Environment env = Environment.EMPTY_ENV;
            env.define("a", 1);
            env.define("+", (PrimitiveFunction) param -> {
                Object p1 = car(param);
                Object p2 = cadr(param);
                if (p1 instanceof Integer && p2 instanceof Integer) {
                    return (Integer) p1 + (Integer) p2;
                }
                return 0;
            });
            interpreter.eval(parser.parse(), env);

            System.out.println(interpreter.eval(parser.parse(), env));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
