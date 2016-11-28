package scheme;

import parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args){
        Path path = Paths.get("test.txt");
        InputStream in;
        try {
            in = Files.newInputStream(path);
            Parser parser = new Parser(in);
            Object parse = parser.parse();
            System.out.println(parse);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
