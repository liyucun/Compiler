package datastructure;

/**
 *
 * @author yucunli
 */
public class Epsilon extends Terminal{
    
    private Epsilon(String value) {
        super(value);
    }
    
    public static Epsilon getInstance() {
        if (epsilon == null) {
            epsilon = new Epsilon("EPSILON_VALUE");
        }
        return epsilon;
    }
    
    private static Epsilon epsilon = null;
}
