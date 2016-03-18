package lexer;

/**
 *
 * @author yucunli
 */
public class Tag {
    
    public final static int
            EOF = -1, EQ = 256, NOTEQ = 257, LESS = 258, GREATER = 259, LESS_OR_EQ = 260,
            GREATER_OR_EQ = 261, SEMI = 262, COMMA = 263, DOT = 264, ADD = 265,
            SUB = 266, MULTI = 267, DIV = 268, ASSIGN = 269, AND = 270, NOT = 271, 
            OR = 272,
            
            OPEN_PARENTHESE = 273, CLOSE_PARENTHESE = 274, OPEN_BRACE = 275, 
            CLOSE_BRACE = 276, OPEN_BRACKET = 277, CLOSE_BRACKET = 278,
            
            /*OPEN_BLOCK_COMMENT = 279, CLOSE_BLOCK_COMMENT = 280, EOL_COMMENT = 279,*/
            
            IF = 280, THEN = 281, ELSE = 282, FOR = 283, CLASS = 284, INT = 285, 
            FLOAT = 286, GET = 287, PUT = 288, RETURN = 289, 
            
            REAL = 290, NUM = 291, FRACTION = 292,
            
            ID = 293, PROGRAM = 294;
    
    /**
     * Convert tag value to string
     *
     * @param tag the token tag in question.
     */
    public static String tagToString(int tag){
        String result = null;
        
        switch(tag){
            case Tag.AND:
            case Tag.NOT:
            case Tag.OR:
            case Tag.IF:
            case Tag.THEN:
            case Tag.ELSE:
            case Tag.FOR:
            case Tag.CLASS:
            case Tag.INT:
            case Tag.FLOAT:
            case Tag.PUT:
            case Tag.GET:
            case Tag.RETURN:
            case Tag.PROGRAM:
                result = "keyword";
                break;
            case Tag.NUM:
                result = "integer";
                break;
            case Tag.REAL:
                result = "float";
                break;
            case Tag.ID:
                result = "identifier";
                break;
            case Tag.EQ:
                result = "equal";
                break;
            case Tag.NOTEQ:
                result = "not equal";
                break;
            case Tag.LESS:
                result = "less";
                break;
            case Tag.GREATER:
                result = "greater";
                break;
            case Tag.LESS_OR_EQ:
                result = "less or equal";
                break;
            case Tag.GREATER_OR_EQ:
                result = "greater or equal";
                break;
            case Tag.SEMI:
                result = "semi";
                break;
            case Tag.COMMA:
                result = "comma";
                break;
            case Tag.DOT:
                result = "dot";
                break;
            case Tag.ADD:
                result = "add";
                break;
            case Tag.SUB:
                result = "sub";
                break;
            case Tag.MULTI:
                result = "multi";
                break;
            case Tag.DIV:
                result = "div";
                break;
            case Tag.ASSIGN:
                result = "assign";
                break;
            case Tag.OPEN_BRACE:
                result = "open brace";
                break;
            case Tag.CLOSE_BRACE:
                result = "close brace";
                break;
            case Tag.OPEN_BRACKET:
                result = "open bracket";
                break;
            case Tag.CLOSE_BRACKET:
                result = "close bracket";
                break;
            case Tag.OPEN_PARENTHESE:
                result = "open parenthese";
                break;
            case Tag.CLOSE_PARENTHESE:
                result = "close parenthese";
                break;
        }
        
        if(result != null){
            return result;
        }else{
            return "Type not specified!";
        }
    }
    
    /**
     * Convert tag value to terminal string
     *
     * @param tag the token tag in question.
     */
    public static String tagToTerminalString(int tag){
        String result = null;
        
        switch(tag){
            case Tag.PROGRAM:
                result = "program";
                break;
            case Tag.AND:
                result = "and";
                break;
            case Tag.NOT:
                result = "not";
                break;
            case Tag.OR:
                result = "or";
                break;
            case Tag.IF:
                result = "if";
                break;
            case Tag.THEN:
                result = "then";
                break;
            case Tag.ELSE:
                result = "else";
                break;
            case Tag.FOR:
                result = "for";
                break;
            case Tag.CLASS:
                result = "class";
                break;
            case Tag.INT:
                result = "int";
                break;
            case Tag.FLOAT:
                result = "float";
                break;
            case Tag.PUT:
                result = "put";
                break;
            case Tag.GET:
                result = "get";
                break;
            case Tag.RETURN:
                result = "return";
                break;
            case Tag.NUM:
                result = "int";
                break;
            case Tag.REAL:
                result = "float";
                break;
            case Tag.ID:
                result = "id";
                break;
            case Tag.EQ:
                result = "==";
                break;
            case Tag.NOTEQ:
                result = "<>";
                break;
            case Tag.LESS:
                result = "<";
                break;
            case Tag.GREATER:
                result = ">";
                break;
            case Tag.LESS_OR_EQ:
                result = "<=";
                break;
            case Tag.GREATER_OR_EQ:
                result = ">=";
                break;
            case Tag.SEMI:
                result = ";";
                break;
            case Tag.COMMA:
                result = ",";
                break;
            case Tag.DOT:
                result = ".";
                break;
            case Tag.ADD:
                result = "+";
                break;
            case Tag.SUB:
                result = "-";
                break;
            case Tag.MULTI:
                result = "*";
                break;
            case Tag.DIV:
                result = "/";
                break;
            case Tag.ASSIGN:
                result = "=";
                break;
            case Tag.OPEN_BRACE:
                result = "{";
                break;
            case Tag.CLOSE_BRACE:
                result = "}";
                break;
            case Tag.OPEN_BRACKET:
                result = "[";
                break;
            case Tag.CLOSE_BRACKET:
                result = "]";
                break;
            case Tag.OPEN_PARENTHESE:
                result = "(";
                break;
            case Tag.CLOSE_PARENTHESE:
                result = ")";
                break;
        }
        
        if(result != null){
            return result;
        }else{
            return "Type not specified!";
        }
    }        
    
}
