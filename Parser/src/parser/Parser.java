package parser;

import datastructure.Terminal;
import exceptions.ParseErrorException;

/**
 * An interface representing an object that can parse a sequence of terminals
 * into a parse tree.
 * @author yucunli
 */
public interface Parser {
    /**
     * Feeds another terminal into the parser, potentially causing the parser
     * to continue parsing or issue a parse error.
     *
     * @param terminal The terminal to feed into the parser.
     * @throws ParseErrorException If a parse error occurs.
     */
    public void nextTerminal(Terminal terminal) throws ParseErrorException;

    /**
     * Indicates to the parser that the end of input has been reached, causing
     * the parser to hand back the parse tree it has created so far.If a
     * parse error occurs when doing so, the parser may issue a parse error.
     *
     * @return The completed parse tree.
     * @throws ParseErrorException If a parse error occurs.
     */
    public ParseTree inputComplete() throws ParseErrorException;
}
