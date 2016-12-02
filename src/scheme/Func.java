package scheme;

@FunctionalInterface
public interface Func {
    Object apply (Object params) throws SyntaxException;
}
