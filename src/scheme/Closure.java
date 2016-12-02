package scheme;

class Closure {
    Object params;
    Object body;
    Environment env;
    Closure(Object params, Object body, Environment env) {
        this.params = params;
        this.body = body;
        this.env = env;
    }
}
