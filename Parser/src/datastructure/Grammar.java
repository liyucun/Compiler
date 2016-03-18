package datastructure;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author yucunli
 */
public class Grammar {
    private List<Variable> variables;
    private List<Terminal> terminals;
    private HashMap<Variable, List<List<Symbol>>> relations;
    private Variable start;
    
    public Grammar(List<Variable> variables, List<Terminal> terminals,
            HashMap< Variable, List<List<Symbol>>> relations, Variable start) {
        this.variables = variables;
        this.terminals = terminals;
        this.relations = relations;
        this.start = start;
    }
    
    public void addVariableToVariableSet(Variable variable) {
        this.variables.add(variable);
    }

    public void addEpsilonToTerminalSet() {
        if (!this.terminals.contains(Epsilon.getInstance())) {
            this.terminals.add(Epsilon.getInstance());
        }
    }
    
    public List<Variable> getVariables() {
        return variables;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public HashMap<Variable, List<List<Symbol>>> getRelations() {
        return relations;
    }

    public Variable getStart() {
        return start;
    }
    
    private String relationsToString() {
        HashMap<Variable, List<List<Symbol>>> relations = this.getRelations();
        String result = "";
        for (Variable variable : this.variables) {
            result += variable + "->";
            List<List<Symbol>> rightPartsForTheSameVariable = relations.get(variable);
            for (List<Symbol> rightPart : rightPartsForTheSameVariable) {
                for (Symbol token : rightPart) {
                    result += token + " ";
                }
                // Pop the last character
                result = result.substring(0, result.length() - 1);
                result += "|";
            }
            // Pop the last character
            result = result.substring(0, result.length() - 1);
            result += "\n";
        }
        return result;
    }
    
     @Override
    public String toString() {
        return "Variables: " + this.variables + "\n"
                + "Terminal: " + this.terminals + "\n"
                + "Start: " + this.start + "\n"
                + "Relations:\n"
                + relationsToString();
    }
}
