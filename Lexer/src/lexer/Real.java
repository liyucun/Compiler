package lexer;

/**
 *
 * @author yucunli
 */
public class Real extends Token{
    
    public final float value;

    public Real(float v, int l, int i) {
        super(Tag.REAL, l, i);
        value = v;
    }
    
    public String toString() {
        return "" + value;
    }

}
