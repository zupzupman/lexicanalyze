import java.util.ArrayList;

public class SimpleLexer {

    public enum TokenType {
        KEYWORD, IDENTIFIER, NUMBER, OPERATOR, SEPARATOR, UNKNOWN
    }

    public static class Token {
        public TokenType type;
        public String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Token{%s, '%s'}", type, value);
        }
    }

    // Keywords for language syntax
    private static final String[] KEYWORDS = {
        "if", "else", "while", "for", "int", "float", "double", 
        "return", "class", "public", "private", "void", "new"
    };

    public static ArrayList<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Parse numbers (integers and decimals)
            if (Character.isDigit(c)) {
                StringBuilder buffer = new StringBuilder();
                boolean hasDot = false;

                while (i < input.length()) {
                    char ch = input.charAt(i);
                    if (Character.isDigit(ch)) {
                        buffer.append(ch);
                        i++;
                    } else if (ch == '.' && !hasDot) {
                        hasDot = true;
                        buffer.append(ch);
                        i++;
                    } else {
                        break;
                    }
                }
                tokens.add(new Token(TokenType.NUMBER, buffer.toString()));
                continue;
            }

            // Parse identifiers and keywords
            if (Character.isLetter(c) || c == '_') {
                StringBuilder buffer = new StringBuilder();

                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                    buffer.append(input.charAt(i));
                    i++;
                }

                String word = buffer.toString();
                boolean isKw = false;
                for (String keyword : KEYWORDS) {
                    if (keyword.equals(word)) {
                        isKw = true;
                        break;
                    }
                }
                tokens.add(new Token(isKw ? TokenType.KEYWORD : TokenType.IDENTIFIER, word));
                continue;
            }

            // Parse multi-character operators
            if (i < input.length() - 1) {
                String twoChar = input.substring(i, i + 2);
                String[] multiOps = {"==", "!=", "<=", ">=", "&&", "||", "++", "--", "+=", "-=", "*=", "/="};
                boolean isMulti = false;
                
                for (String op : multiOps) {
                    if (op.equals(twoChar)) {
                        isMulti = true;
                        break;
                    }
                }
                
                if (isMulti) {
                    tokens.add(new Token(TokenType.OPERATOR, twoChar));
                    i += 2;
                    continue;
                }
            }

            // Parse single-character operators
            if ("+-*/%=<>!&|^~".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                i++;
                continue;
            }

            // Parse separators
            if ("(){}[];,.".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c)));
                i++;
                continue;
            }

            // Unknown character
            tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c)));
            i++;
        }

        return tokens;
    }

    public static void main(String[] args) {
        String sourceCode = "int x = 10.5 + 50; if (x >= 60) return x++;";
        
        System.out.println("Source: " + sourceCode);
        System.out.println("--- Tokens ---");

        ArrayList<Token> tokenList = lex(sourceCode);
        for (Token t : tokenList) {
            System.out.println(t);
        }
    }
}
