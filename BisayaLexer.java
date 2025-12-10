import java.util.ArrayList;

public class BisayaLexer {

    public enum TokenType {
        KEYWORD, IDENTIFIER, NUMBER, OPERATOR, SEPARATOR, STRING, COMMENT, UNKNOWN
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

    private static final String[] KEYWORDS = {
        "sulti", "paminaw", "kung", "kontra", "kungkontra", "samtang", "buhata", 
        "para", "baylo", "kaso", "default", "undang", "padayon", "balik",
        "numero", "teksto", "karakter", "boolean", "tinuod", "bakak", "walay",
        "klase", "publiko", "set", "goto", "ArrayTix", "bag_o", "gidak_on"
    };
    
    // private static final String[] NOISE_WORDS = {
    //     "ean", "ger", "acter"
    // };

    public static ArrayList<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '#' && i + 1 < input.length() && input.charAt(i + 1) == '"') {
                i += 2;
                StringBuilder buffer = new StringBuilder();
                while (i < input.length()) {
                    if (input.charAt(i) == '"' && i + 1 < input.length() && input.charAt(i + 1) == '#') {
                        i += 2;
                        break;
                    }
                    buffer.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.COMMENT, buffer.toString()));
                continue;
            }

            if (c == '#') {
                StringBuilder buffer = new StringBuilder();
                while (i < input.length() && input.charAt(i) != '\n') {
                    buffer.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.COMMENT, buffer.toString()));
                continue;
            }

            if (c == '"') {
                StringBuilder buffer = new StringBuilder();
                i++;
                while (i < input.length() && input.charAt(i) != '"') {
                    if (input.charAt(i) == '\\' && i + 1 < input.length()) {
                        i++;
                        char escaped = input.charAt(i);
                        if (escaped == 'n') buffer.append('\n');
                        else if (escaped == 't') buffer.append('\t');
                        else if (escaped == '\\') buffer.append('\\');
                        else if (escaped == '"') buffer.append('"');
                        else buffer.append(escaped);
                        i++;
                    } else {
                        buffer.append(input.charAt(i));
                        i++;
                    }
                }
                if (i < input.length()) i++;
                tokens.add(new Token(TokenType.STRING, buffer.toString()));
                continue;
            }

            if (c == '\'') {
                StringBuilder buffer = new StringBuilder();
                i++;
                if (i < input.length() && input.charAt(i) != '\'') {
                    buffer.append(input.charAt(i));
                    i++;
                }
                if (i < input.length() && input.charAt(i) == '\'') i++;
                tokens.add(new Token(TokenType.STRING, buffer.toString()));
                continue;
            }

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

            if (i < input.length() - 1) {
                String twoChar = input.substring(i, i + 2);
                String[] multiOps = {"==", "!=", "<=", ">=", "&&", "||", "++", "--", "+=", "-=", "*=", "/=", "%=", "^="};
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

            if ("+-*/%=<>!&|^~".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                i++;
                continue;
            }

            if ("(){}[];,.:".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c)));
                i++;
                continue;
            }

            tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c)));
            i++;
        }

        return tokens;
    }
}
