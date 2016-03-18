package driver;

import datastructure.Grammar;
import datastructure.Terminal;
import exceptions.GrammarNotLL1Exception;
import exceptions.ParseErrorException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import parser.GrammarParser;
import parser.ParseTree;
import parser.Parser;
import parser.ParserGenerator;
import visulization.TreeView;
import static visulization.TreeView.demo;
import static visulization.TreeView.demo;

/**
 *
 * @author yucunli
 */
public class ParserDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            GrammarParser gp = new GrammarParser("grammar/grammer_changed_from_solution.txt");
            Grammar g = gp.generateGrammar();
            
            Parser parser = ParserGenerator.createParser(g);
            
            Lexer lex = new Lexer("tests/test.txt");
            lex.init();
            while (true) {
                Token t = lex.debug_next_token();
                
                if (t.tag == Tag.EOF) {
                    break;
                }
                
                try {
                    parser.nextTerminal(new Terminal(Tag.tagToTerminalString(t.tag)));
                } catch (ParseErrorException ex) {
                    synchronized(System.out){
                        System.out.println("At source code line: "+t.getLine()+" "+t.getIndex()+" :");
                        System.out.println(ex);
                    }
                }
            }
            
            ParseTree parseTree = parser.inputComplete();
            System.out.println(parseTree.toString());
            parseTree.writeToXML("parse_tree.xml");
            
            ParserDriver.displayParseTree("parse_tree.xml");
            
        } catch (GrammarNotLL1Exception ex) {
            System.err.println("Given grammar is not LL1 grammar!");
            System.err.println(ex);
        } catch (FileNotFoundException ex) {
            System.err.println("Source file is not founded!");
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println("IO exception happened in lexer in init() method!");
            System.err.println(ex);
        } catch (ParseErrorException ex) {
            System.err.println("Parser inputComplete method cannot deal with last symbol!");
            System.err.println(ex);
        }
    }
    
    public static void displayParseTree(String filename){
        // set the route of xml file
        TreeView.TREE_CHI = filename;
        
        JComponent treeview = demo(filename, "name");
        
        JFrame frame = new JFrame("Parse Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(treeview);
        frame.pack();
        frame.setVisible(true);
    }

}
