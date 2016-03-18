package datastructure;

/**
 *
 * @author yucunli
 */
public class EOF extends Terminal {
    
    private EOF(String value) {
        super(value);
    }
    
    public static EOF getInstance() {
        if (eof == null) {
            eof = new EOF("$");
        }
        return eof;
    }
    
    private static EOF eof = null;
}
