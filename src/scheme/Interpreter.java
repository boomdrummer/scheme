package scheme;

import static scheme.Utils.*;

public class Interpreter {

    static Object eval(Object exp) throws SyntaxException {
        return eval(exp, Environment.ENV0);
    }

    @SuppressWarnings("unchecked")
    static Object eval(Object exp, Environment env) throws SyntaxException {

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
//                    env = new Environment(map(params, Utils::car), evaluateList(map(params, Utils::cadr), env), env); // extend environment with let parameter list
                    env = env.extend(map(params, Utils::car), evaluateList(map(params, Utils::cadr), env)); // extend environment with let parameter list
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
                    } else if (fun instanceof Func) {
                        return ((Func) fun).apply(params);   //primitive function
                    } else {
                        return null;
                    }
                }
            } else {
                return exp;
            }

        }
    }

    static private Object evaluateList(Object list, Environment env) throws SyntaxException {
        return map(list, exp -> eval(exp, env));
    }

    static private Object apply(Closure closure, Object vals) throws SyntaxException {
        Environment env = closure.env.extend(closure.params, vals);
        Object body = closure.body;
        while (!isLast(body)) {
            eval(car(body), env);
            body = cdr(body);
        }
        return eval(car(body), env);
    }


}
