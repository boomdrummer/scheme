package parser;

import scheme.SyntaxException;

import java.io.InputStream;

public class Lexer {
    public static final Object EOF = new Lexer(null);
    private Reader reader;

    Lexer(InputStream in) {
        reader = new Reader(in);
    }

    Object nextToken() throws SyntaxException {
        char ch = reader.nextCharNotWhiteSpace();

        switch (ch) {
            case (char) -1:
                return EOF;
            case '(':
                return '(';
            case ')':
                return ')';
            case '\'':
                return '\'';
            case ',':
                return ',';
            case '#':
                return reader.read() == 't';
            case '"':
                return reader.readString().toCharArray();
            default:
                String symbol = reader.readSymbol(ch);
                if (ch >= '0' && ch <= '9') { //number
                    try {
                        return Double.valueOf(symbol);
                    } catch (NumberFormatException e) {
                        throw new SyntaxException(symbol + "is not a number");
                    }
                } else {
                    return symbol.intern(); //symbol
                }
        }
    }

}
