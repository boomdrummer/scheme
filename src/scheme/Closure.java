package scheme;

public class Closure {
    Object params;
    Object body;
    Environment env;
    public Closure(Object params, Object body,Environment env) {
        this.params = params;
        this.body = body;
        this.env = env;
    }
}
