package lexer;

/**
 *
 * @author yucunli
 */
public class Num extends Token{
    
    public final int value;

    public Num(int v, int l, int i) {
        super(Tag.NUM, l, i);
        value = v;
    }
    
    public String toString() {
        return "" + value;
    }

}
