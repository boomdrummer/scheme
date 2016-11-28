package parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Lexer {
    Reader reader;
    Object savedToken;
    boolean isSaved;
    public static final Object EOF = new Lexer(null);

    public Lexer(InputStream in) {
        reader = new Reader(in);
    }

    Object nextToken() throws ParseException {
        if (isSaved) {
            return savedToken;
        }

        try {
            char ch = reader.read();
            StringBuilder buffer;
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
                    ch = reader.read();
                    if (ch == 't') {
                        return true;
                    } else if (ch == 'f') {
                        return false;
                    } else {
                        throw new ParseException('#' + ch + " not supported");
                    }
                case '"':
                    ch = reader.read();
                    buffer = new StringBuilder(50);
                    while (ch != (char)-1 && ch != '"') {
                        buffer.append(ch);
                        ch = reader.read();
                    }
                    if (ch == '"') {
                        return buffer.toString().toCharArray();
                    } else {
                        throw new ParseException("except a \",not complete...");
                    }
                default:
                    buffer = new StringBuilder(10);
                    do {
                        buffer.append(ch);
                        ch = reader.read();
                    } while (ch != (char) -1 && ch != ')' && !Character.isWhitespace(ch));

                    if (ch == ')') {
                        reader.save(')');
                    }
                    return buffer.toString().intern();

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
            return null;
        }

    }

    void SaveToken(Object o) {
        savedToken = o;
        isSaved = true;
    }


    public static void main(String[] args){
        Path path = Paths.get("test.txt");
        InputStream in = null;
        try {
            in = Files.newInputStream(path);
            Lexer lexer = new Lexer(in);
            Object o = lexer.nextToken();
            while (o != EOF) {
                System.out.println(o);
                o = lexer.nextToken();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
