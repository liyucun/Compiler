package lexer;

/**
 *
 * @author yucunli
 */
public class Word extends Token{
    public String lexeme = "";
    
    /* Constructor for keywords */
    public Word(String s, int tag){
     super(tag, -1, -1);
     lexeme = s;
    }
    
    public Word(String s, int tag, int l, int i) {
        super(tag, l, i);
        lexeme = s;
    }
    
    public String toString() {
        return lexeme;
    }
}
