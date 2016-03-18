package datastructure;

/**
 *
 * @author yucunli
 */
public class Variable extends Symbol {
    
    private static int newVariableCounter = 0;

    public Variable(String value) {
        super(value);
    }

    @Override
    public Boolean isTerminal() {
        return false;
    }
    
    public static Variable getNewVariable() {
        ++newVariableCounter;
        return new Variable("NEW_VAR'{" + newVariableCounter + "}");
    }
    
    public static Variable getNewVariable(Variable variable) {
        ++newVariableCounter;
        return new Variable(variable.getValue() + "'{" + newVariableCounter + "}");
    }
}
