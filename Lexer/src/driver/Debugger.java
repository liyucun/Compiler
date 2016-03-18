package driver;

import java.io.FileNotFoundException;
import java.io.IOException;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;

/**
 *
 * @author yucunli
 */
public class Debugger {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Lexer lex = new Lexer("test_case.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
    }
    
}
