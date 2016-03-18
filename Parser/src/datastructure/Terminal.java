package datastructure;

/**
 *
 * @author yucunli
 */
public class Terminal extends Symbol{
    
    int line;
    int index;
    
    public Terminal(String value) {
        super(value);
    }
    
    @Override
    public Boolean isTerminal() {
        return true;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    
}
