package parser;

import scheme.Pair;
import scheme.SyntaxException;

import java.io.InputStream;

import static parser.Lexer.EOF;
import static scheme.Utils.list;

public class Parser {
    private Lexer lexer;

    public Parser(InputStream in) {
        lexer = new Lexer(in);
    }

    public Object parse() throws SyntaxException {
            Object token = lexer.nextToken();
            if (token instanceof Character) {
                switch ((char) token) {
                    case '(':
                        return readList();
                    case ')':
                        return ')';
                    case '\'':
                        return list("quote", parse());
                    default:
                        throw new SyntaxException(token + "is not supported ...");
                }
            } else {
                return token;
            }

    }

    private Object readList() throws SyntaxException {
        Pair head = new Pair(null, null);
        Pair prev = head;
        Object token = parse();
        while (token != EOF && !(token instanceof Character && (char) token == ')')) {
            prev.second = new Pair(token, null);
            prev = (Pair) prev.second;
            token = parse();
        }

        if (token == EOF)
            throw new SyntaxException("expect ),giving EOF...");

        return head.second;
    }
}
