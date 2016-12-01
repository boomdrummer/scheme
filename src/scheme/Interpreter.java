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

public class Interpreter {
    private Environment env0 = Environment.ENV0;

    public Interpreter() {
        init();
    }

    public static void main(String[] args) {
        Path path = Paths.get("test.txt");
        try {
            InputStream inputStream = Files.newInputStream(path);
            Parser parser = new Parser(inputStream);
            Interpreter interpreter = new Interpreter();
            Object exp = parser.parse();
            while (exp != Lexer.EOF) {
                System.out.println(interpreter.eval(exp, Environment.ENV0));
                exp = parser.parse();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
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

    @SuppressWarnings("unchecked")
    private Object eval(Object exp, Environment env) {

        loop:
        while (true) {
            if (exp instanceof String) {
                return env.lookup(exp);
            } else if (exp instanceof Pair) {
                Object first = car(exp);
                if (first == "if") {
                    exp = isTrue(eval(cadr(exp), env)) ? caddr(exp) : cadddr(exp); // predicate, first expression ,second expression
                } else if (first == "list") {
                    return evaluateList(cdr(exp), env);
                } else if (first == "define") {
                    Object second = cadr(exp);
                    if (second instanceof Pair) //  (fun a b) style
                        return env.define(car(second), new Closure(cdr(second), cddr(exp), env)); //variable name,parameters,body
                    else
                        return env.define(second, eval(caddr(exp), env));
                } else if (first == "lambda") {
                    return new Closure(cadr(exp), cddr(exp), env); //parameters , body
                } else if (first == "let") {
                    Object params = cadr(exp);
                    exp = caddr(exp);   // exp -> let expression body
                    env = new Environment(map(params, Utils::car), evaluateList(map(params, Utils::cadr), env), env); // extend environment with let parameter list
                } else if (first == "cond") {
                    Object list = cdr(exp);
                    while (list != null) {
                        Object current = car(list);
                        Object predicate = car(current);
                        if ("else".equals(predicate) || isTrue(eval(predicate, env))) {  // if predicate is true evaluate cdr of current pair
                            exp = cadr(current);
                            continue loop;
                        } else {
                            list = cdr(list);
                        }
                    }
                    return null;
                } else if (first == "quote") {
                    return cadr(exp);
                } else {
                    Object fun = eval(first, env);
                    Object params = evaluateList(cdr(exp), env);
                    if (fun instanceof Closure) {               //custom function
                        return apply((Closure) fun, params);
                    } else if (fun instanceof Function) {
                        return ((Function) fun).apply(params);   //primitive function
                    }else {
                        return null;
                    }
                }
            } else {
                return exp;
            }
        }
    }

    private Object evaluateList(Object list, Environment env) {
        return map(list, exp -> eval(exp, env));
    }

    private Object apply(Closure closure, Object vals) {
        Environment env = new Environment(closure.params, vals, closure.env);
        Object body = closure.body;
        while (!isLast(body)) {
            eval(car(body), env);
            body = cdr(body);
        }
        return eval(car(body), env);
    }


}
