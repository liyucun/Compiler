package parser;

import datastructure.EOF;
import datastructure.Epsilon;
import datastructure.Grammar;
import datastructure.Terminal;
import datastructure.Symbol;
import datastructure.Variable;
import exceptions.GrammarNotLL1Exception;
import exceptions.ParseErrorException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.GrammarAttributes;

/**
 * A parser generator that generates LL(1) parsers for a given grammar.
 * 
 * @author yucunli
 */
public final class ParserGenerator {
     /* This class is not meant to be instantiated. */
    private ParserGenerator() {
        // Empty //
    }
    
    /**
     * Given a grammar, constructs an LL(1) parser for that grammar.  If the
     * parser can be formed, it is returned.  If the grammar is not LL(1), an
     * exception is raised.
     *
     * @param g The grammar to build an LL(1) parser for.
     * @return An LL(1) parser for that grammar.
     * @throws GrammarNotLL1Exception If the grammar is not LL(1).
     */
    public static Parser createParser(Grammar grammar) throws GrammarNotLL1Exception{
    
        /* Compute the FIRST and FOLLOW sets for the grammar. */
        HashMap<Variable, Set<Terminal>> first = GrammarAttributes.computeFirstSets(grammar);
        HashMap<Variable, Set<Terminal>> follow = GrammarAttributes.computeFollowSets(grammar, first);
        
        /* Allocate space for the parsing table. */
        Map<LL1Key, List<Symbol>> parsingTable = 
            new HashMap<LL1Key, List<Symbol>>();
        
        HashMap<Variable, List<List<Symbol>>> relations = grammar.getRelations();
        /* Begin filling in the parsing table. */
        for (Variable leftPart : relations.keySet()) {
            
            for (List<Symbol> rightPart : relations.get(leftPart)) {
                /* Get the FIRST set for the production. */
                Set<Terminal> firstTokens =
                GrammarAttributes.getFirstSetForSequence(rightPart, first);
                
                /* Add every non-epsilon production into the parsing table. */
                for (Terminal t: firstTokens) {
                    if (t.equals(Epsilon.getInstance())) continue;
                    
                    /* Add this entry to the parsing table, failing if something
                    * was already there.
                    */
                    if (parsingTable.put(new LL1Key(leftPart, t), rightPart) != null)
                        throw new GrammarNotLL1Exception("Conflict detected for " + leftPart + ", " + t);
                }
                
                /* Now, if the FIRST set contains epsilon, add in similar rules for
                * everything in the FOLLOW set.
                */
                if (firstTokens.contains(Epsilon.getInstance())) {
                    for (Terminal t: follow.get(leftPart)) {
                        /* Add this entry to the parsing table, failing if 
                         * something was already there.
                         */
                        if (parsingTable.put(new LL1Key(leftPart, t), rightPart) != null)
                            throw new GrammarNotLL1Exception("Conflict detected for " + leftPart + ", " + t);
                    }
                }
            }
        }
        
        //printParsingTable(parsingTable);
        
        /* Wrap the parsing table up into a parser, then hand it back. */
        return new LL1Parser(parsingTable, grammar.getStart());
    }
    
    
    
    
    /**
     * A class representing a pair of a nonterminal and a terminal that acts as
     * the key in an LL(1) parsing table.
     */
    private static final class LL1Key{
    
        /** The nonterminal used in the key. */
        private final Variable nonterminal;

        /** The terminal (or EOF) used in the key. */
        private final Terminal terminal;
        
        /**
         * Constructs a new LL1Key holding the given nonterminal/terminal pair.
         *
         * @param nonterminal The nonterminal in the key.
         * @param terminal The terminal in the key.
         */
        public LL1Key(Variable nonterminal, Terminal terminal) {
            /* Check the input for correctness. */
            if (nonterminal == null || terminal == null)
                throw new NullPointerException();

            this.nonterminal = nonterminal;
            this.terminal = terminal;
        }
        
        /**
         * Returns whether this LL1Key is equal to some other object.
         *
         * @param o The object to compare again.
         * @return Whether this object is equal to o.
         */
        @Override 
        public boolean equals(Object o) {
            /* The other object must be an LL1Key. */
            if (!(o instanceof LL1Key)) return false;

            /* Downcast to the actual LL1Key. */
            LL1Key other = (LL1Key) o;

            /* See if we have the same terminal/nonterminal pair. */
            return terminal.equals(other.terminal) &&
                   nonterminal.equals(other.nonterminal);
        }
        
        /**
         * Returns a hash code for this LL1Key.
         *
         * @return A hash code for this LL1Key.
         */
        @Override 
        public int hashCode() {
            return 31 * nonterminal.hashCode() + terminal.hashCode();
        }
        
        /**
         * Returns a human-readable description of this LL1Key.
         *
         * @return A human-readable description of this LL1Key.
         */
        @Override 
        public String toString() {
            return "(" + nonterminal + ", " + terminal + ")";
        }

    }
    
    /** 
     * An LL(1) parser.  Internally.  The parser maintains a stack containing
     * the predicted symbols, along with an LL(1) parsing table telling it
     * which actions to take.
     */
    private static final class LL1Parser implements Parser{
        /**
         * A utility struct pairing a symbol and the parse tree it corresponds
         * to.  The LL(1) parser will maintain a sequence of these elements so
         * that we can build up a parse tree for the input as we go.
         */
        private static final class StackEntry {
            /** The token (or EOF) in the stack. */
            public final Symbol token;
            
            /** The parse tree associated with that symbol. */
            public final ParseTree tree;
            
            /**
             * Constructs a new StackEntry holding the given symbol and parse
             * tree.
             *
             * @param symbol The symbol to store here.
             * @param tree The parse tree to store here.
             */
            public StackEntry(Symbol token, ParseTree tree) {
                this.token = token;
                this.tree = tree;
            }
        }
        
        /** The parsing table. */
        private final Map<LL1Key, List<Symbol>> parsingTable;
        
        /** The parsing stack. */
        private final Deque<StackEntry> parsingStack = new ArrayDeque<StackEntry>();

        /** The generated parse tree. */
        private final ParseTree parseTree;
        
        /**
         * Constructs a new LL(1) parser using the given parse table and start
         * symbol.  The parsing stack is seeded with the start symbol.
         *
         * @param parsingTable The parsing table.
         * @param start The start symbol.
         */
        public LL1Parser(Map<LL1Key, List<Symbol>> parsingTable,
                         Variable start) {
            this.parsingTable = parsingTable;

            /* Create a new parse tree seeded with the start symbol. */
            parseTree = new ParseTree(start);

            /* Put the EOF marker atop the stack. */
            parsingStack.offerFirst(new StackEntry(EOF.getInstance(), null));

            /* Insert a pair of the start symbol/parse tree atop the parsing
             * stack.
             */
            parsingStack.offerFirst(new StackEntry(start, parseTree));
        }
        
        /**
         * Consumes the next terminal symbol, applying a predict/match step
         * as necessary.  This may cause multiple predict steps to be applied
         * before a match step is made.  If no prediction exists or if the
         * match step fails, a ParseErrorException is raised.
         *
         * @param terminal The next terminal symbol.
         * @throws ParseErrorException If a parse error occurs.

         */
        @Override 
        public void nextTerminal(Terminal terminal) throws ParseErrorException {
            processSymbol(terminal);
        }
        
        /**
         * Processes the end of the input.  This should conclude with a series
         * of predicts and matches that ultimately empties the stack and
         * returns the parse tree.
         *
         * @return The parse tree generated by the parser.
         * @throws ParseErrorException If EOF wasn't expected.
         */
        @Override
        public ParseTree inputComplete() throws ParseErrorException {
            ParseTree result = processSymbol(EOF.getInstance());
            assert result != null;
            return result;
        }
        
        /**
         * Private helper function that processes the next token of the input,
         * which can be either a terminal or the EOF marker.  If a parsing
         * error occurs, a ParseErrorException is thrown.  If parsing completes
         * because the input was the EOF marker, the parse tree is returned.
         *
         * @param symbol The generalized symbol that appears next.
         * @return The completed parse tree, if any.
         * @throws ParseErrorException If a parse error occurs.
         */
        private ParseTree processSymbol(Terminal terminal) throws ParseErrorException {
            /* If the stack is empty, then we're done parsing and can't process
             * any more terminals.
             */
            if (parsingStack.isEmpty())
                throw new ParseErrorException("Parsing already completed.");
            
            /* Keep applying predict steps until the top of the stack holds a
             * terminal symbol.
             */
            while (true) {
                /* Look at the top of the stack to see what we find. */
                StackEntry top = parsingStack.pollFirst();
                
                /* If the top symbol matches the symbol we just encountered,
                 * the match is complete.
                 */
                if (top.token.equals(terminal)) {
                    if(top.tree != null && top.tree.getSymbol() != null) {
                        ((Terminal)top.tree.getSymbol()).setIndex(terminal.getIndex());
                        ((Terminal)top.tree.getSymbol()).setLine(terminal.getLine());
                    }
                    /* If the input was EOF, hand back the parse tree.
                     * Otherwise, hand back null as a sentinel.
                     */
                    return terminal.equals(EOF.getInstance()) ? parseTree : null;
                }
                
                /* If the top symbol didn't match, then one of two things must
                 * be true.  First, we could have a match failure, where the
                 * symbol in question does not match the symbol atop the stack.
                 * Since we know the top of the stack doesn't match the current
                 * symbol, the only way that we didn't have a mismatch is if
                 * the top of the stack is not a nonterminal.
                 */
                if (top.token.isTerminal()) {
                    // recover tech: push top terminal back(like skipping the wrong terminal)
                    parsingStack.offerFirst(top);
                    throw new ParseErrorException("Expected " + top.token + ", found " + terminal);
                }

                /* Otherwise, the top of the stack must be a nonterminal and
                 * we need to do a predict step.
                 */
                Variable variable = (Variable) top.token;
                List<Symbol> production = parsingTable.get(new LL1Key(variable, terminal));
                
                /* If no production is defined, then we have a parse error. */
                if (production == null) {
                    // recover tech: push top variable back(like skipping the wrong terminal)
                    parsingStack.offerFirst(top);
                    throw new ParseErrorException("No production for " + variable + " on seeing " + terminal);
                }

                /* Check if production is a epsilon terminal*/
                boolean isEpsilon = false;
                /* Otherwise, push each symbol onto the stack, annotated with
                 * a new parse tree node.  However, because the stack grows
                 * in the front, we have to push these symbols in the reverse
                 * order from when they appear.
                 */
                for (int i = production.size() - 1; i >= 0; --i) {
                    Symbol t = production.get(i);
                    
                    /* Construct a new parse tree node for this symbol. */
                    ParseTree tree = new ParseTree(t);
                    
                    /* If the symbol is epsilon, we won't push it to stack */
                    if(t.equals(Epsilon.getInstance())) {
                        top.tree.getChildren().add(tree);
                        isEpsilon = true;
                        break;
                    }

                    /* Insert this symbol/tree pair at the top of the stack. */
                    parsingStack.offerFirst(new StackEntry(t, tree));
                }
                
                if(!isEpsilon) {
                    /* Now, looking over the first |w| symbols of the stack, add
                    * each as a child of the parse tree node for the nonterminal
                    * we just expanded.
                    */
                   Iterator<StackEntry> iter = parsingStack.iterator();
                   for (int i = 0; i < production.size(); ++i) {
                       ParseTree tree = iter.next().tree;
                       // Because $ will be the last symbol in Stack, and if E -> epsilon is last production,
                       // this will prohibit a null tree to be added into top's tree
                       if(tree != null)
                           top.tree.getChildren().add(tree);
                   }
                }
            }
        }
    
    }

    public static void printParsingTable(Map<LL1Key, List<Symbol>> parsingTable) {
        System.out.println("Print the parsing table...");
        
        for(LL1Key key : parsingTable.keySet()){
            System.out.print(((Symbol)key.nonterminal).getValue() + " " + ((Symbol)key.terminal).getValue() + " : ");
            for(Symbol symbol : parsingTable.get(key)){
                System.out.print(symbol.getValue() + " ");
            }
            System.out.println();
            
        }
    }
}
