package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

/**
 *
 * @author yucunli
 */
public class Lexer implements Scanner {

    /**
     * First character of lookahead.
     */
    protected int next_char;

    /**
     * Second character of lookahead.
     */
    protected int next_char2;

    /**
     * EOF constant.
     */
    protected static final int EOF_CHAR = -1;

    /**
     * Table of single character symbols. For ease of implementation, we store
     * all unambiguous single character tokens in this table of Integer objects
     * keyed by Integer objects with the numerical value of the appropriate char
     * (currently Character objects have a bug which precludes their use in
     * tables). ; , . + - * ( )
     */
    protected Hashtable char_symbols = new Hashtable(12);

    /**
     * Table of keywords. Keywords are initially treated as identifiers. Just
     * before they are returned we look them up in this table to see if they
     * match one of the keywords. The string of the name is the key here, which
     * indexes Integer objects holding the symbol number.
     */
    protected Hashtable keywords = new Hashtable();

    /**
     * Current line number for use in error messages.
     */
    protected int current_line = 1;

    /**
     * Character position in current line.
     */
    protected int current_position = 1;

    /**
     * Count of total errors detected so far.
     */
    public int error_count = 0;

    /**
     * Count of warnings issued so far
     */
    public static int warning_count = 0;

    /**
     * Reader to read file.
     */
    protected Reader reader;

    public Lexer(String filename) throws FileNotFoundException {
        File file = new File(filename);
        InputStream in = new FileInputStream(file);
        reader = new BufferedReader(new InputStreamReader(in));

    }

    /**
     * Initialize the scanner. This sets up the keywords and char_symbols tables
     * and reads the first two characters of lookahead.
     */
    public void init() throws IOException {
        /* set up the keyword table */
        keywords.put("and", new Word("and", Tag.AND));
        keywords.put("not", new Word("not", Tag.NOT));
        keywords.put("or", new Word("or", Tag.OR));
        keywords.put("if", new Word("if", Tag.IF));
        keywords.put("then", new Word("then", Tag.THEN));
        keywords.put("else", new Word("else", Tag.ELSE));
        keywords.put("for", new Word("for", Tag.FOR));
        keywords.put("class", new Word("class", Tag.CLASS));
        keywords.put("int", new Word("int", Tag.INT));
        keywords.put("float", new Word("float", Tag.FLOAT));
        keywords.put("get", new Word("get", Tag.GET));
        keywords.put("put", new Word("put", Tag.PUT));
        keywords.put("return", new Word("return", Tag.RETURN));
        keywords.put("program", new Word("program", Tag.PROGRAM));

        /*keywords.put("==", new Integer(Tag.EQ));
         keywords.put("<>", new Integer(Tag.NOTEQ));
         keywords.put("<", new Integer(Tag.LESS));
         keywords.put(">", new Integer(Tag.GREATER));
         keywords.put("<=", new Integer(Tag.LESS_OR_EQ));
         keywords.put(">=", new Integer(Tag.GREATER_OR_EQ));
         keywords.put("=", new Integer(Tag.ASSIGN));*/

        /* set up the table of single character symbols */
        char_symbols.put(new Integer(';'), new Integer(Tag.SEMI));
        char_symbols.put(new Integer(','), new Integer(Tag.COMMA));
        char_symbols.put(new Integer('.'), new Integer(Tag.DOT));
        char_symbols.put(new Integer('+'), new Integer(Tag.ADD));
        char_symbols.put(new Integer('-'), new Integer(Tag.SUB));
        char_symbols.put(new Integer('*'), new Integer(Tag.MULTI));
        char_symbols.put(new Integer('/'), new Integer(Tag.DIV));
        char_symbols.put(new Integer('('), new Integer(Tag.OPEN_PARENTHESE));
        char_symbols.put(new Integer(')'), new Integer(Tag.CLOSE_PARENTHESE));
        char_symbols.put(new Integer('{'), new Integer(Tag.OPEN_BRACE));
        char_symbols.put(new Integer('}'), new Integer(Tag.CLOSE_BRACE));
        char_symbols.put(new Integer('['), new Integer(Tag.OPEN_BRACKET));
        char_symbols.put(new Integer(']'), new Integer(Tag.CLOSE_BRACKET));

        /* read two characters of lookahead */
        next_char = reader.read();
        if (next_char == EOF_CHAR) {
            next_char2 = EOF_CHAR;
        } else {
            next_char2 = reader.read();
        }
    }

    /**
     * Advance the scanner one character in the input stream. This moves
     * next_char2 to next_char and then reads a new next_char2.
     */
    protected void advance() throws IOException {
        int old_char;

        old_char = next_char;
        next_char = next_char2;
        if (next_char == EOF_CHAR) {
            next_char2 = EOF_CHAR;
        } else {
            next_char2 = reader.read();
        }

        /* count this */
        current_position++;
        if (old_char == '\n') {
            current_line++;
            current_position = 1;
        }
    }

    /**
     * Emit an error message. The message will be marked with both the current
     * line number and the position in the line. Error messages are printed on
     * standard error (System.err).
     *
     * @param message the message to print.
     */
    public void emit_error(String message) {
        System.err.println("Error at " + current_line + "(" + current_position
                + "): " + message);
        error_count++;
    }

    /**
     * Emit a warning message. The message will be marked with both the current
     * line number and the position in the line. Messages are printed on
     * standard error (System.err).
     *
     * @param message the message to print.
     */
    public void emit_warn(String message) {
        System.err.println("Warning at " + current_line + "(" + current_position
                + "): " + message);
        warning_count++;
    }

    /**
     * Determine if a character is ok to start an id.
     *
     * @param ch the character in question.
     */
    protected boolean id_start_char(int ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    /**
     * Determine if a character is ok for the middle of an id.
     *
     * @param ch the character in question.
     */
    protected boolean id_char(int ch) {
        return id_start_char(ch) || (ch >= '0' && ch <= '9') || (ch == '_');
    }
    
    /**
     * Determine a token type.
     *
     * @param Token the token in question.
     */
    protected String token_type(Token token){
        return Tag.tagToString(token.tag);
    }

    /**
     * Try to look up a single character symbol, returns -1 for not found.
     *
     * @param ch the character in question.
     */
    protected Word find_single_char(int ch) {
        Integer result;

        result = (Integer) char_symbols.get(new Integer((char) ch));
        if (result == null) {
            return new Word("", -1);
        } else {
            return new Word("" + (char) ch, result.intValue(), current_line, current_position);
        }
    }

    protected Word find_operator_word(int ch, int ch2) throws IOException {
        Word result = null;

        switch (ch) {
            case '<':
                if (ch2 == '=') {
                    result = new Word("<=", Tag.LESS_OR_EQ, current_line, current_position);
                    advance();
                    advance();
                } else if (ch2 == '>') {
                    result = new Word("<>", Tag.NOTEQ, current_line, current_position);
                    advance();
                    advance();
                } else {
                    result = new Word("<", Tag.LESS, current_line, current_position);
                    advance();
                }
                break;
            case '>':
                if (ch2 == '=') {
                    result = new Word(">=", Tag.GREATER_OR_EQ, current_line, current_position);
                    advance();
                    advance();
                } else {
                    result = new Word(">", Tag.GREATER, current_line, current_position);
                    advance();
                }
                break;
            case '=':
                if (ch2 == '=') {
                    result = new Word("==", Tag.EQ, current_line, current_position);
                    advance();
                    advance();
                } else {
                    result = new Word("=", Tag.ASSIGN, current_line, current_position);
                    advance();
                }
                break;
        }

        if (result == null) {
            return new Word("", -1);
        } else {
            return result;
        }
    }

    /**
     * Handle swallowing up a comment. Both old style C and new style C++
     * comments are handled.
     */
    protected void swallow_comment() throws IOException {
        /* next_char == '/' at this point */

        /* is it a traditional comment */
        if (next_char2 == '*') {
            /* swallow the opener */
            advance();
            advance();

            /* swallow the comment until end of comment or EOF */
            for (;;) {
                /* if its EOF we have an error */
                if (next_char == EOF_CHAR) {
                    emit_error("Specification file ends inside a comment");
                    return;
                }

                /* if we can see the closer we are done */
                if (next_char == '*' && next_char2 == '/') {
                    advance();
                    advance();
                    return;
                }

                /* otherwise swallow char and move on */
                advance();
            }
        }

        /* is its a new style comment */
        if (next_char2 == '/') {
            /* swallow the opener */
            advance();
            advance();

            /* swallow to '\n', '\f', or EOF */
            while (next_char != '\n' && next_char != '\f' && next_char != EOF_CHAR) {
                advance();
            }

            return;

        }

        /* shouldn't get here, but... if we get here we have an error */
        emit_error("Malformed comment in specification at line -- ignored");
        advance();
    }

    /**
     * Process an identifier. Identifiers begin with a letter, which is followed
     * by zero or more letters, numbers, or underscores. This routine returns an
     * Word suitable for return by the scanner.
     */
    protected Word do_id() throws IOException {
        StringBuffer result = new StringBuffer();
        String result_str;
        Word keyword;
        char buffer[] = new char[1];

        /* next_char holds first character of id */
        buffer[0] = (char) next_char;
        result.append(buffer, 0, 1);
        advance();

        /* collect up characters while they fit in id */
        while (id_char(next_char)) {
            buffer[0] = (char) next_char;
            result.append(buffer, 0, 1);
            advance();
        }

        /* extract a string and try to look it up as a keyword */
        result_str = result.toString();
        keyword = (Word) keywords.get(result_str);

        /* if we found something, return that keyword */
        if (keyword != null) {
            keyword.setLine(current_line);
            keyword.setIndex(current_position);
            return keyword;
        }

        /* otherwise build and return an id token with an attached string */
        return new Word(result_str, Tag.ID, current_line, current_position);
    }

    /**
     * Process an number. Number begin with a digit(0-9), which is followed by
     * digits or Dot. This routine returns an Num suitable for return by the
     * scanner.
     */
    protected Token do_num() throws IOException {
        int result = 0;
        int num_digits = 0;
        
        // delete non sense 0
        while(next_char == '0' && next_char != '.'){
            advance();
        }
        
        while(Character.isDigit(next_char)){
            result = 10*result + Character.digit(next_char, 10);
            num_digits++;
            advance();
        }
        
        if(next_char != '.'){
            // when num of digits is greater than integer digits num
            if(num_digits > 10){
                emit_error("The number is too big to catch -- ignored");
                return new Num(Integer.MAX_VALUE, current_line, current_position);
            }else{
                return new Num(result, current_line, current_position);
            }
        }
        
        float f_result = result; float d = 10;
        for(;;){
            advance();
            if(!Character.isDigit(next_char)) break;
            f_result = f_result + Character.digit(next_char, 10) / d;
            d = d*10;
        }
        
        return new Real(f_result, current_line, current_position);
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * Return one token. This is the main external interface to the scanner. It
     * consumes sufficient characters to determine the next input token and
     * returns it. To help with debugging, this routine actually calls
     * real_next_token() which does the work. If you need to debug the parser,
     * this can be changed to call debug_next_token() which prints a debugging
     * message before returning the token.
     */
    @Override
    public Token next_token() throws Exception {
        return real_next_token();
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * Debugging version of next_token(). This routine calls the real scanning
     * routine, prints a message on System.out indicating what the token is,
     * then returns it.
     */
    public Token debug_next_token() throws java.io.IOException {
        Token result = real_next_token();
        synchronized(System.out){
            System.out.println("# next_token() => " + result.toString() + "    " + token_type(result));
        }
        
        return result;
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * The actual routine to return one token. This is normally called from
     * next_token(), but for debugging purposes can be called indirectly from
     * debug_next_token().
     */
    protected Token real_next_token() throws IOException {
        Word word;

        for (;;) {
            /* look for white space (\r)-carriage return */
            if (next_char == ' ' || next_char == '\t' || next_char == '\n'
                    || next_char == '\f' || next_char == '\r') {
                /* advance past it and try the next character */
                advance();
                continue;
            }

            /* look for a comment */
            if (next_char == '/' && (next_char2 == '*' || next_char2 == '/')) {
                /* swallow then continue the scan */
                swallow_comment();
                continue;
            }

            /* look for a single character symbol */
            word = find_single_char(next_char);
            if (word.tag != -1) {
                /* found one -- advance past it and return a token for it */
                advance();
                return word;
            }

            /* look for a operator: <=, ==, >, <, = */
            if (next_char == '<' || next_char == '>' || next_char == '=') {
                word = find_operator_word(next_char, next_char2);
                if (word.tag != -1) {
                    /* word is advanced in find_operator_word method */
                    return word;
                }
            }

            /* look for an id or keyword */
            if (id_start_char(next_char)) {
                return do_id();
            }

            /* look for an number (integer, float) */
            if (Character.isDigit(next_char)) {
                return do_num();
            }

            /* look for EOF */
            if (next_char == EOF_CHAR) {
                return new Token(Tag.EOF, current_line, current_position);
            }

            /* if we get here, we have an unrecognized character */
            emit_warn("Unrecognized character '"
                    + new Character((char) next_char) + "'(" + next_char
                    + ") -- ignored");

            /* advance past it */
            advance();
        }
    }

}
