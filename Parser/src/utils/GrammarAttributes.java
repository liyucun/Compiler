package utils;

import datastructure.EOF;
import datastructure.Epsilon;
import datastructure.Grammar;
import datastructure.Terminal;
import datastructure.Symbol;
import datastructure.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility functions for computing particular attributes of grammars, such as
 * the FIRST and FOLLOW sets for the grammar.
 * <p>
 * These utility functions are used by the parser generators to learn enough
 * about the grammar to generate parse tables.
 *
 * @author yucunli
 */
public final class GrammarAttributes {

    /**
     * Computes and returns the FIRST sets for all of the nonterminal symbols in
     * the grammar.
     *
     * @param grammer The grammar whose FIRST sets should be computed.
     * @return A map associating nonterminals in the grammar with their FIRST
     * sets.
     */
    public static HashMap<Variable, Set<Terminal>> computeFirstSets(Grammar grammar) {
        /* Sanity-check the input. */
        if (grammar == null) {
            throw new NullPointerException("grammar must not be null.");
        }

        /* Create a map that will hold the result of this operation. */
        HashMap<Variable, Set<Terminal>> first
                = initializeNonterminalMapping(grammar);

        Boolean isChangeDetected = false;
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        do {
            isChangeDetected = false;
            for (Variable leftPart : relations.keySet()) {
                for (List<Symbol> rightPart : relations.get(leftPart)) {

                    Set<Terminal> computedFirst = getFirstSetForSequence(rightPart, first);

                    if (first.get(leftPart).addAll(computedFirst)) {
                        isChangeDetected = true;
                    }
                }
            }

        } while (isChangeDetected);

        return first;
    }

    /**
     * Computes the FOLLOW sets for each nonterminal in a grammar.
     *
     * @param g The grammar whose FOLLOW sets should be computed.
     * @param first The FIRST sets for g.
     * @return The FOLLOW sets for g.
     */
    public static HashMap<Variable, Set<Terminal>>
            computeFollowSets(Grammar grammar, HashMap<Variable, Set<Terminal>> first) {
        /* Ensure the arguments aren't null. */
        if (grammar == null || first == null) {
            throw new NullPointerException();
        }

        /* Create a map that will hold the result. */
        HashMap<Variable, Set<Terminal>> follow
                = initializeNonterminalMapping(grammar);

        /* Add the EOF marker to the start symbol's FOLLOW set. */
        follow.get(grammar.getStart()).add(EOF.getInstance());

        /* Run the fixed-point iteration to compute FOLLOW sets. */
        boolean shouldContinue;
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        do {
            shouldContinue = false;

            /* For each production, scan the production to find what
             * nonterminals, if any, it contains.  For each nonterminal, use
             * the aforementioned logic to update the FOLLOW set for that
             * nonterminal.
             */
            for (Variable leftPart : relations.keySet()) {

                /* For each right part of one left part*/
                for (List<Symbol> rightPart : relations.get(leftPart)) {
                    /* We use a standard for-loop here instead of a foreach loop
                     * because we need the index of the current symbol so that we
                     * can talk about the symbols following the current symbol.
                     */
                    for (int index = 0; index < rightPart.size(); ++index) {
                        Symbol token = rightPart.get(index);
                        /* If this symbol is a terminal, skip over it. */
                        if (token.isTerminal()) {
                            continue;
                        }

                        /* Get the FIRST set of the remainder of the string. */
                        Set<Terminal> startOfRest = getFirstSetForSequence(rightPart.subList(index + 1, rightPart.size()), first);

                        /* Add in everything from this set except epsilon. */
                        for (Terminal t : startOfRest) {
                            /* Skip epsilon; it never appears in a FOLLOW set. */
                            if (t.equals(Epsilon.getInstance())) {
                                continue;
                            }

                            /* Add this symbol to the FOLLOW set. */
                            if (follow.get(token).add(t)) {
                                shouldContinue = true;
                            }
                        }

                        /* If this set contains epsilon, then we should also add
                         * the FOLLOW set from the nonterminal at the head of this
                         * production to the FOLLOW set.
                         */
                        if (startOfRest.contains(Epsilon.getInstance())) {
                            if (follow.get(token).addAll(follow.get(leftPart))) {
                                shouldContinue = true;
                            }
                        }
                    }
                }

            }

        } while (shouldContinue);

        return follow;
    }

    /**
     * Creates a map that associates each nonterminal with an empty set.
     *
     * @param g The grammar for which the set should be constructed.
     * @return A map associating each nonterminal with an empty set.
     */
    private static HashMap<Variable, Set<Terminal>>
            initializeNonterminalMapping(Grammar g) {
        HashMap<Variable, Set<Terminal>> result
                = new HashMap<Variable, Set<Terminal>>();

        /* Add a mapping for each nonterminal in the grammar. */
        for (Variable variable : g.getVariables()) {
            result.put(variable, new HashSet<Terminal>());
        }

        return result;
    }

    /**
     * Given a sequence of terminals and nonterminals, along with the computed
     * FIRST sets for those nonterminals, returns the set of symbols that could
     * appear at the start of a string derivable from the initial sequence.
     *
     * @param sequence The sequence of terminals and nonterminals.
     * @param first The FIRST sets for those nonterminals.
     * @return The set of symbols that could start off a string derivable from
     * the initial string.
     */
    public static Set<Terminal>
            getFirstSetForSequence(List<Symbol> tokenList,
                    HashMap<Variable, Set<Terminal>> first) {

        Set<Terminal> result = new HashSet<>();

        for (Symbol token : tokenList) {
            /* If it's a terminal, we're done. */
            if (token.isTerminal()) {
                result.add((Terminal) token);
                return result;
            }

            /* If the FIRST set for this nonterminal does not contain epsilon,
             * add everything from its FIRST set to the result set and stop.
             */
            if (!first.get(token).contains(Epsilon.getInstance())) {
                result.addAll(first.get(token));
                return result;
            }

            /* Otherwise, it does contain epsilon.  Add everything in from that
             * set, but then take out the epsilon.
             */
            result.addAll(first.get(token));
            result.remove(Epsilon.getInstance());
        }

        /* If we made it here, then there must be some way that the string can
         * derive epsilon (either the production was empty, or it's a sequence
         * of nonterminals that are all nullable.
         */
        result.add(Epsilon.getInstance());
        return result;
    }

    /**
     * print out grammar first and follow set
     *
     * @param grammar
     */
    public static void printFirstFollowSet(Grammar grammar) {

        HashMap<Variable, Set<Terminal>> first = GrammarAttributes.computeFirstSets(grammar);
        HashMap<Variable, Set<Terminal>> follow = GrammarAttributes.computeFollowSets(grammar, first);

        /* print out first set */
        for (Symbol t : first.keySet()) {
            System.out.print(t.toString() + ": {");
            for (Terminal tt : first.get(t)) {
                System.out.print(tt.toString() + " ");
            }
            System.out.println("}");
        }

        System.out.println("************************************");
        System.out.println("************************************");

        /* print out follow set */
        for (Symbol t : follow.keySet()) {
            System.out.print(t.toString() + ": {");
            for (Terminal tt : follow.get(t)) {
                System.out.print(tt.toString() + " ");
            }
            System.out.println("}");
        }
    }

    /**
     * Remove grammar left recursion.
     *
     * @param grammar
     */
    public static void removeLeftRecursion(Grammar grammar) {
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        List<Variable> orderedVariables = new ArrayList<>(grammar.getVariables());

        for (int i = 0; i < orderedVariables.size(); ++i) {
            if (((Symbol) orderedVariables.get(i)).getValue().equals("aParams")) {
                System.out.println("found it");
            }
            List<List<Symbol>> newRightAiParts = new ArrayList<>(relations.get(orderedVariables.get(i)));
            for (int j = 0; j < i; ++j) {

                for (List<Symbol> rightPartAi : relations.get(orderedVariables.get(i))) {
                    // Ai -> Aja
                    if (rightPartAi.get(0).equals(orderedVariables.get(j))) {
                        // Remove Ai -> Aja
                        newRightAiParts.remove(rightPartAi);
                        for (List<Symbol> rightPartAj : relations.get(orderedVariables.get(j))) {
                            List<Symbol> newRightPartAi = new ArrayList<>(rightPartAi);
                            // Replace Aj by the value of the relation
                            newRightPartAi.remove(0);
                            newRightPartAi.addAll(0, rightPartAj);

                            newRightAiParts.add(newRightPartAi);
                        }
                    }
                }

            }
            relations.remove(orderedVariables.get(i));
            relations.put(orderedVariables.get(i), newRightAiParts);

            removeDirectLeftRecursionRemoval(grammar, orderedVariables.get(i));
        }
    }

    /**
     * Remove direct left recursion of one production.
     *
     * @param grammar target grammar
     * @param leftPart variable of one production rule
     *
     */
    private static void removeDirectLeftRecursionRemoval(Grammar grammar, Variable leftPart) {
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();

        // A -> Aa1 | ... | Aar
        List<List<Symbol>> leftRecursiveSet = new ArrayList<>();
        // A -> b1 | ... | bs
        List<List<Symbol>> notLeftRecursiveSet = new ArrayList<>();
        for (List<Symbol> rightPart : relations.get(leftPart)) {
            if (rightPart.get(0).equals(leftPart)) {
                leftRecursiveSet.add(rightPart);
            } else {
                notLeftRecursiveSet.add(rightPart);
            }
        }

        /* If there exists a left recursion for this variable, 
         * remove all the rules for this variable and replace them 
         * by new rules without left recursion.
         */
        if (!leftRecursiveSet.isEmpty()) {
            relations.remove(leftPart);
            Variable oldVariable = leftPart;

            Variable newVariable = Variable.getNewVariable(oldVariable);
            grammar.addVariableToVariableSet(newVariable);

            // A -> b1A' | ... | bsA'
            for (List<Symbol> rightPart : notLeftRecursiveSet) {
                rightPart.add(newVariable);
            }
            relations.put(oldVariable, notLeftRecursiveSet);

            /* A' -> a1A' | ... | arA' | e */
            for (List<Symbol> rightPart : leftRecursiveSet) {
                // Romove left recursion
                rightPart.remove(0);
                rightPart.add(newVariable);
            }
            // Add epsilon
            List<Symbol> epsilonList = new ArrayList<>();
            epsilonList.add(Epsilon.getInstance());
            grammar.addEpsilonToTerminalSet();
            leftRecursiveSet.add(epsilonList);
            relations.put(newVariable, leftRecursiveSet);
        }
    }

    /**
     * Refactor right hand repetitive prefix of production rule.
     *
     * @param grammar target grammar
     */
    public static void refactorRepetitivePrefix(Grammar grammar) throws Exception {
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        List<Variable> orderedLeftParts = new ArrayList<>(relations.keySet());

        int leftPartIndex = 0;
        while (leftPartIndex < orderedLeftParts.size()) {
            Boolean isCommonPrefixFound = false;
            Variable leftPart = orderedLeftParts.get(leftPartIndex);
            List<List<Symbol>> orderedRightParts = new ArrayList<>(relations.get(leftPart));

            int i = 0;
            while (i < orderedRightParts.size()) {
                List<Integer> indexesOfMatches = new ArrayList<>();
                int sizeOfSubstring = orderedRightParts.get(i).size();
                while (sizeOfSubstring > 0) {
                    for (int j = 0; j < orderedRightParts.size(); ++j) {
                        // Match
                        if (j != i && orderedRightParts.get(j).size() >= sizeOfSubstring
                                && orderedRightParts.get(i).subList(0, sizeOfSubstring)
                                .equals(orderedRightParts.get(j).subList(0, sizeOfSubstring))) {

                            indexesOfMatches.add(j);
                        }
                    }
                    // If a match is found
                    if (!indexesOfMatches.isEmpty()) {
                        isCommonPrefixFound = true;
                        break;
                    }
                    --sizeOfSubstring;
                }

                // If a match is found
                if (isCommonPrefixFound) {
                    // Create new variable
                    Variable newVariable = Variable.getNewVariable();
                    grammar.addVariableToVariableSet(newVariable);
                    relations.put(newVariable, new ArrayList<List<Symbol>>());

                    // A' -> b1 | ... | bn
                    relations.get(leftPart).remove(orderedRightParts.get(i));
                    List<Symbol> sublist = orderedRightParts.get(i).subList(sizeOfSubstring, orderedRightParts.get(i).size());

                    if (sublist.isEmpty()) {
                        sublist.add(Epsilon.getInstance());
                        grammar.addEpsilonToTerminalSet();
                    }
                    relations.get(newVariable).add(sublist);

                    for (Integer index : indexesOfMatches) {
                        relations.get(leftPart).remove(orderedRightParts.get(index));
                        sublist = orderedRightParts.get(index).subList(sizeOfSubstring, orderedRightParts.get(index).size());
                        if (sublist.isEmpty()) {
                            sublist.add(Epsilon.getInstance());
                            grammar.addEpsilonToTerminalSet();;
                        }
                        relations.get(newVariable).add(sublist);
                    }

                    // A -> aA'
                    sublist = new ArrayList<>(orderedRightParts.get(i).subList(0, sizeOfSubstring));
                    if (sublist.isEmpty()) {
                        throw new Exception("Error : common prefix (a) is empty");
                    }
                    sublist.add(newVariable);
                    relations.get(leftPart).add(sublist);
                    break;
                }

                ++i;

            }
            if (!isCommonPrefixFound) {
                ++leftPartIndex;
            } else {
                leftPartIndex = 0;
                orderedLeftParts = new ArrayList<>(relations.keySet());
            }

        }
    }
}
