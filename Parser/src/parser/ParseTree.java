package parser;

import datastructure.Symbol;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A class representing a parse tree showing a derivation of some string in the
 * grammar. Each tree node holds a symbol (either terminal or nonterminal) along
 * with an ordered list of child nodes.
 *
 * @author yucunli
 */
public class ParseTree implements Iterable<ParseTree> {

    /**
     * The grammar symbol represented by this node of the tree.
     */
    private final Symbol token;

    /**
     * The children of this parse tree node, in the order in which they appear
     */
    private final List<ParseTree> children;

    /**
     * Constructs a new parse tree wrapping the given token with the given
     * children.
     *
     * @param token The token at this parse tree node.
     * @param children The children of this parse tree node.
     */
    public ParseTree(Symbol token, List<ParseTree> children) {
        if (token == null || children == null) {
            throw new NullPointerException();
        }

        this.token = token;
        this.children = children;
    }

    /**
     * Constructs a new parse tree node holding the given token, but with no
     * children.
     *
     * @param symbol The symbol held by this parse tree node.
     */
    public ParseTree(Symbol token) {
        this(token, new ArrayList<ParseTree>());
    }

    /**
     * Returns the symbol held by this parse tree node.
     *
     * @return The symbol held by this parse tree node.
     */
    public Symbol getSymbol() {
        return token;
    }

    /**
     * Returns a mutable view of the children of this parse tree node.
     *
     * @return A mutable view of the children of this parse tree node.
     */
    public List<ParseTree> getChildren() {
        return children;
    }

    /**
     * Returns a mutable iterator to traverse the children of this parse tree.
     *
     * @return A mutable iterator traversing the children of this node.
     */
    public Iterator<ParseTree> iterator() {
        return getChildren().iterator();
    }

    /**
     * Returns a human-readable representation of the parse tree.
     *
     * @return A human-readable representation of the parse tree.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(token);
        if (!token.isTerminal()) {
            builder.append(" -> ");
            builder.append(getChildren());
        }
        return builder.toString();
    }
    
    public void writeToXML(String filename) {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("tree");
            doc.appendChild(mainRootElement);
 
            // append child elements to root element
            mainRootElement.appendChild(getDeclarations(doc));
            
            Element treeBranchRoot = getBranchs(doc, this.token.toString());
            mainRootElement.appendChild(treeBranchRoot);
            
            
            Stack<ParseTree> stack_tree = new Stack<>();
            Stack<Element> stack_elements = new Stack<>();
            stack_tree.push(this);
            stack_elements.push(treeBranchRoot);
            while(!stack_tree.isEmpty()) {
                ParseTree top_tree = stack_tree.pop();
                Element top_element = stack_elements.pop();
                
                for(ParseTree tree : top_tree.getChildren()){
                    Element e = getBranchs(doc, tree.token.toString());
                    top_element.appendChild(e);
                    
                    stack_tree.push(tree);
                    stack_elements.push(e);
                }
                
            }
 
            // output DOM XML to console 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes"); 
            DOMSource source = new DOMSource(doc);
            
            StreamResult console = new StreamResult(new File(filename));
            
            transformer.transform(source, console);
 
            System.out.println("\nXML DOM Created Successfully..");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element getBranchs(Document doc, String value) {
        Element branch = doc.createElement("branch");

        Element attribute = doc.createElement("attribute");
        attribute.setAttribute("name", "name");
        attribute.setAttribute("value", value);

        branch.appendChild(attribute);

        return branch;
    }

    private static Node getDeclarations(Document doc) {
        Element declaration = doc.createElement("declarations");

        Element attributeDecl = doc.createElement("attributeDecl");
        attributeDecl.setAttribute("name", "name");
        attributeDecl.setAttribute("type", "String");

        declaration.appendChild(attributeDecl);

        return declaration;
    }

}
