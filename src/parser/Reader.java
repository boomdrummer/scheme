package parser;

import java.io.IOException;
import java.io.InputStream;

public class Reader {
    InputStream in;
    char savedChar;
    boolean isSaved;

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

}
