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
        while (true) {
            if (exp instanceof String) {
                return env.lookup(exp);
            } else if (exp instanceof Pair) {
                Object first = car(exp);
                if (first == "if") {
                    Object pred = cadr(exp);
                    Object e1 = caddr(exp);
                    Object e2 = cadddr(exp);
                    exp = isTrue(eval(pred, env)) ? e1 : e2;
                } else if (first == "list") {
                    final Environment env0 = env;
                    return map(cdr(exp), o -> eval(o, env0));
                } else if (first == "define") {
                    Object second = cadr(exp);
                    if (second instanceof Pair) { // define function
                        Object var = car(second);
                        Object params = cdr(second);
                        Object body = cddr(exp);
                        return env.define(var, new Closure(params, body, env));
                    } else if (second instanceof String) {
                        return env.define(second, eval(caddr(exp), env));
                    } else {
                        throw new SyntaxException("illege define ..");
                    }
                } else if (first == "lambda") {
                    Object params = cadr(exp);
                    Object body = cddr(exp);
                    return new Closure(params, body, env);
                } else if (first == "let") {
                    Object params = cadr(exp);
                    Object body = caddr(exp);
                    Object vars = map(params, Utils::car);
                    Object vals = map(params, Utils::cadr);
                    exp = body;
                    env = new Environment(vars, vals, env);
                } else if (first == "cond") {
                    Object list = cdr(exp);
                    while (list != null) {
                        Object current = car(list);
                        Object pred = car(current);
                        if (pred.equals("else") || isTrue(eval(pred, env))) {
                            exp = cadr(current);
                            break;
                        } else {
                            list = cdr(list);
                        }
                    }
                } else {
                    Object fun = eval(first, env);
                    final Environment env0 = env;
                    Object params = map(cdr(exp), o -> eval(o, env0));
                    if (fun instanceof Closure) {
                        return apply((Closure) fun, params);
                    } else if (fun instanceof Function) {
                        return ((Function) fun).apply(params);
                    }
                }
            } else {
                return exp;
            }

        }
    }

    Object apply(Closure closure, Object vals) {
        Environment env = new Environment(closure.params, vals, closure.env);
        Object body = closure.body;
        while (!isLast(body)) {
            eval(car(body), env);
            body = cdr(body);
        }

        return eval(car(body), env);

    }

    public static void main(String[] args) {
        Path path = Paths.get("test.txt");
        try {
            InputStream inputStream = Files.newInputStream(path);
            Parser parser = new Parser(inputStream);
            Interpreter interpreter = new Interpreter();
            Object exp = parser.parse();
            while (exp != Lexer.EOF) {
                System.out.println(interpreter.eval(exp, Environment.INIT_ENV));
                exp = parser.parse();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
