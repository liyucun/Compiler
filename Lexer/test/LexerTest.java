import java.io.FileNotFoundException;
import java.io.IOException;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yucunli
 */
public class LexerTest {
    
    public LexerTest() {
    }
    
    @Test
    public void testLetter() throws FileNotFoundException, IOException {
        Lexer lex = new Lexer("testcase/letter/input.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
    }

    @Test
    public void testNumber() throws FileNotFoundException, IOException, Exception {
        Lexer lex = new Lexer("testcase/number/input.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
    }
    
    @Test
    public void testSymbols() throws FileNotFoundException, IOException {
        Lexer lex = new Lexer("testcase/symbols/input.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
    }
    
    @Test
    public void testIdentifier() throws FileNotFoundException, IOException {
        Lexer lex = new Lexer("testcase/identifier/input.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
    }
    
    @Test
    public void testSamples() throws FileNotFoundException, IOException {
        Lexer lex = new Lexer("testcase/samples/example-valid-program.txt");
        lex.init();
        while(true){
            Token t = lex.debug_next_token();
            if (t.tag == Tag.EOF) break;
        }
        
    }
}
