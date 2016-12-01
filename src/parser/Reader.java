package parser;

import java.io.IOException;
import java.io.InputStream;

class Reader {
    private InputStream in;
    private char savedChar;
    private boolean isSaved;

    Reader(InputStream in) {
        this.in = in;
    }


    char read() throws IOException {
        if (isSaved) {
            isSaved = false;
            return savedChar;
        } else {
            return (char) in.read();
        }
    }

    void save(char c) {
        savedChar = c;
        isSaved = true;
    }

    char nextCharNotWhiteSpace() throws IOException {
        char ch;
        do {
            ch = read();
        } while (ch != (char) -1 && Character.isWhitespace(ch));
        return ch;
    }

    String readString() throws IOException, ParseException {
        char ch = read();
        StringBuilder buffer = new StringBuilder(50);
        while (ch != (char) -1 && ch != '"') {
            buffer.append(ch);
            ch = read();
        }

        if(ch != '"')
            throw new ParseException("except a \",not complete...");

        return buffer.toString();
    }

    /**
     * read a symbol from io stream
     * @param c first char of symble
     * @return target symble
     */
    String readSymbol(char c) throws IOException {
        StringBuilder buffer = new StringBuilder(10);
        do {
            buffer.append(c);
            c = read();
        } while (c != (char) -1 && c != ')' && !Character.isWhitespace(c));

        if (c == ')')
            save(')');

        return buffer.toString();
    }
}
