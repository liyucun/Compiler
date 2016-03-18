package datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author yucunli
 */
public class ProductionRule {

    private static int counter = 1;
    public static HashMap<Integer, ProductionRule> productionRules = new HashMap<>();
    private int id;
    private Variable leftPart;
    private List<Symbol> rightPart;
    
    private ProductionRule(Variable leftPart, List<Symbol> rightPart) {
        this.id = counter;
        ++counter;
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        productionRules.put(id, this);
    }
    
    public static List<ProductionRule> generateProductionRulesFromGrammar(Grammar grammar) {
        List<ProductionRule> result = new ArrayList<>();
        
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        
        for (Variable leftPart : grammar.getVariables()) {
            for (List<Symbol> rightPart : relations.get(leftPart)) {
                result.add(new ProductionRule(leftPart, rightPart));
            }
        }
        
        return result;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Variable getLeftPart() {
        return this.leftPart;
    }
    
    public List<Symbol> getRightPart() {
        return this.rightPart;
    }

    @Override
    public String toString() {
        String result = "";
        result += "(" + this.id + ")" + this.leftPart.toString() + "->";
        for (Symbol token : this.rightPart) {
            result += token.toString() + " ";
        }
        result = result.substring(0, result.length()-1);
        return result;
    }
}
