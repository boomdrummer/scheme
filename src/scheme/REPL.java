package scheme;

import parser.Lexer;
import parser.Parser;

import java.io.IOException;
import java.io.InputStream;

import static scheme.Interpreter.eval;
import static scheme.Utils.print;

public class REPL {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        Parser parser = new Parser(inputStream);
        print(">");
        Object exp = parser.parse();
        while (exp != Lexer.EOF) {
            print(">"+eval(exp)+"\n>");
            exp = parser.parse();
        }
    }
}
