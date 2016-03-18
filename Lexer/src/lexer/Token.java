package lexer;

/**
 *
 * @author yucunli
 */
public class Token {
    public final int tag;
    private int line;
    private int index;
    public Token(int t, int l, int i) {tag = t; line = l; index = i;}
    public String toString() {return "" + (char)tag;}

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    
    
}
