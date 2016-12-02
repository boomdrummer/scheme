package scheme;

import parser.Parser;

import java.io.IOException;
import java.io.InputStream;

import static parser.Lexer.EOF;
import static scheme.Interpreter.eval;
import static scheme.Utils.print;
import static scheme.Utils.println;
import static scheme.Utils.warn;

public class REPL {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        Parser parser = new Parser(inputStream);
        Object exp = null;
        do {
            try {
                print(">");
                exp = parser.parse();
                println(">" + eval(exp));
            } catch (SyntaxException e) {
                warn(e.getMessage());
                println(">");
            }
        } while (exp != EOF);
    }
}
