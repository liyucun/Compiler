package parser;

import datastructure.EOF;
import datastructure.Epsilon;
import datastructure.Grammar;
import datastructure.Terminal;
import datastructure.Symbol;
import datastructure.Variable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yucunli
 */
public class GrammarParser {
    
    private String filename;
    
    public GrammarParser(String filename) {
        this.filename = filename;
    }
    
    public Grammar generateGrammar() {
        List<Variable> variables = new ArrayList<>();
        List<Terminal> terminals = new ArrayList<>();
        HashMap<Variable, List<List<Symbol>>> relations = new HashMap<>();
        Variable start = null;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(this.filename);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;

            // First line : variables
            line = br.readLine();
            String[] splittedLine = line.split("\\s+");
            for (int i = 0; i < splittedLine.length; ++i) {
                variables.add(new Variable(splittedLine[i]));
            }

            // Second line : terminals
            line = br.readLine();
            splittedLine = line.split("\\s+");
            for (int i = 0; i < splittedLine.length; ++i) {
                // Epsilon
                if (splittedLine[i].equals("EPSILON_VALUE")) {
                    terminals.add(Epsilon.getInstance());
                } else if (splittedLine[i].equals("$")) {
                    terminals.add(EOF.getInstance());
                } else {
                    terminals.add(new Terminal(splittedLine[i]));
                }
            }

            // Third line : start
            line = br.readLine();
            start = new Variable(line);

            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    splittedLine = line.split("->");
                    Variable leftPart = new Variable(splittedLine[0]);

                    // Parse the several right parts for a same variable
                    List<List<Symbol>> rightPartsForTheSameVariable = new ArrayList<>();
                    splittedLine = splittedLine[1].split("\\|");
                    for (int i = 0; i < splittedLine.length; ++i) {
                        // Parse one right part
                        String[] splittedSplittedLine = splittedLine[i].split("\\s+");
                        List<Symbol> rightPart = new ArrayList<>();
                        for (int j = 0; j < splittedSplittedLine.length; ++j) {
                            // Epsilon
                            if (splittedSplittedLine[j].equals("EPSILON_VALUE")) {
                                rightPart.add(Epsilon.getInstance());
                            } else if(splittedSplittedLine[j].equals("$")) {
                                rightPart.add(EOF.getInstance());
                            } else {
                                Variable variable = new Variable(splittedSplittedLine[j]);
                                Terminal terminal = new Terminal(splittedSplittedLine[j]);
                                // Check whether the token belongs to the variables or the terminals
                                if (variables.contains(variable)) {
                                    rightPart.add(variable);
                                } else if (terminals.contains(terminal)) {
                                    rightPart.add(terminal);
                                } else {
                                    throw new Exception("Right part does not belong to the variables nor the terminals : " + terminal);
                                }
                            }
                        }

                        rightPartsForTheSameVariable.add(rightPart);
                    }
                    relations.put(leftPart, rightPartsForTheSameVariable);
                }
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GrammarParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GrammarParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GrammarParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(GrammarParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return new Grammar(variables, terminals, relations, start);
    }
}
