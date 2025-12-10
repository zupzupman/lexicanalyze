import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BisayaInterpreter {
    
    private Map<String, Object> variables = new HashMap<>();
    private StringBuilder output = new StringBuilder();
    private StringBuilder lineBuffer = new StringBuilder();
    private Scanner scanner = new Scanner(System.in);
    
    public String execute(String code) {
        output = new StringBuilder();
        lineBuffer = new StringBuilder();
        variables.clear();
        
        try {
            ArrayList<BisayaLexer.Token> tokens = BisayaLexer.lex(code);
            ArrayList<BisayaLexer.Token> filteredTokens = new ArrayList<>();
            
            for (BisayaLexer.Token token : tokens) {
                if (token.type != BisayaLexer.TokenType.COMMENT) {
                    filteredTokens.add(token);
                }
            }
            
            interpretTokens(filteredTokens);
            flushLine();
        } catch (RuntimeException e) {
            output.append("ERROR: ").append(e.getMessage()).append("\n");
        } catch (Exception e) {
            output.append("ERROR: ").append(e.getMessage()).append("\n");
        }
        
        return output.toString();
    }
    
    private void flushLine() {
        if (lineBuffer.length() > 0) {
            output.append(lineBuffer.toString()).append("\n");
            lineBuffer = new StringBuilder();
        }
    }
    
    private void interpretTokens(ArrayList<BisayaLexer.Token> tokens) {
        int i = 0;
        
        while (i < tokens.size()) {
            BisayaLexer.Token token = tokens.get(i);
            
            if (token.type == BisayaLexer.TokenType.KEYWORD) {
                if (token.value.equals("sulti")) {
                    i = handleSulti(tokens, i);
                    flushLine();
                } else if (token.value.equals("paminaw")) {
                    i = handlePaminaw(tokens, i);
                } else if (token.value.equals("numero") || token.value.equals("teksto") || 
                           token.value.equals("karakter") || token.value.equals("boolean")) {
                    i = handleVariableDeclaration(tokens, i);
                } else if (token.value.equals("ArrayTix")) {
                    i = handleArrayDeclaration(tokens, i);
                } else if (token.value.equals("set")) {
                    i = handleSet(tokens, i);
                } else if (token.value.equals("kung")) {
                    i = handleKung(tokens, i);
                } else if (token.value.equals("samtang")) {
                    i = handleSamtang(tokens, i);
                } else if (token.value.equals("para")) {
                    i = handlePara(tokens, i);
                } else {
                    i++;
                }
            } else if (token.type == BisayaLexer.TokenType.IDENTIFIER) {
                if (i + 1 < tokens.size()) {
                    BisayaLexer.Token next = tokens.get(i + 1);
                    if (next.value.equals("[")) {
                        i = handleArrayAccess(tokens, i);
                    } else if (next.value.equals("=") || next.value.equals("+=") || next.value.equals("-=") ||
                        next.value.equals("*=") || next.value.equals("/=") || next.value.equals("%=") ||
                        next.value.equals("^=") || next.value.equals("++") || next.value.equals("--")) {
                        i = handleAssignment(tokens, i);
                    } else {
                        i++;
                    }
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
    }
    
    private int handleSulti(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        if (i >= tokens.size()) {
            throw new RuntimeException("sulti() expects an argument");
        }
        
        if (tokens.get(i).value.equals("(")) {
            i++;
        } else {
            throw new RuntimeException("sulti() requires parentheses");
        }
        
        if (i >= tokens.size()) {
            throw new RuntimeException("sulti() missing closing parenthesis");
        }
        
        Object result = evaluateExpression(tokens, i);
        
        if (result != null && result.toString().isEmpty()) {
            flushLine();
        } else {
            lineBuffer.append(result != null ? result.toString() : "");
        }
        
        while (i < tokens.size() && !tokens.get(i).value.equals(")")) {
            i++;
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals(")")) {
            i++;
        } else {
            throw new RuntimeException("sulti() missing closing parenthesis");
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private int handlePaminaw(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        if (i < tokens.size() && tokens.get(i).value.equals("(")) {
            i++;
        }
        
        if (i < tokens.size() && tokens.get(i).type == BisayaLexer.TokenType.IDENTIFIER) {
            String varName = tokens.get(i).value;
            i++;
            
            if (i < tokens.size() && tokens.get(i).value.equals(")")) {
                i++;
            }
            
            if (i < tokens.size() && tokens.get(i).value.equals("=")) {
                i++;
            }
            
            if (i < tokens.size() && tokens.get(i).type == BisayaLexer.TokenType.STRING) {
                output.append(tokens.get(i).value);
                i++;
            }
            
            String input = scanner.nextLine();
            variables.put(varName, input);
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private Object evaluateExpression(ArrayList<BisayaLexer.Token> tokens, int start) {
        if (start >= tokens.size()) return "";
        
        BisayaLexer.Token token = tokens.get(start);
        
        if (token.type == BisayaLexer.TokenType.STRING) {
            return token.value;
        } else if (token.type == BisayaLexer.TokenType.NUMBER) {
            if (token.value.contains(".")) {
                return Double.parseDouble(token.value);
            } else {
                return Integer.parseInt(token.value);
            }
        } else if (token.type == BisayaLexer.TokenType.IDENTIFIER) {
            if (start + 1 < tokens.size() && tokens.get(start + 1).value.equals("[")) {
                String arrayName = token.value;
                int i = start + 2;
                Object indexExpr = evaluateFullExpressionUntil(tokens, i, "]");
                int index = indexExpr instanceof Number ? ((Number)indexExpr).intValue() : 0;
                
                if (variables.containsKey(arrayName) && variables.get(arrayName) instanceof Object[]) {
                    Object[] array = (Object[])variables.get(arrayName);
                    if (index >= 0 && index < array.length) {
                        return array[index];
                    } else {
                        throw new RuntimeException("Array index out of bounds: " + index);
                    }
                } else {
                    throw new RuntimeException("'" + arrayName + "' is not an array");
                }
            } else if (variables.containsKey(token.value)) {
                return variables.get(token.value);
            }
            return token.value;
        } else if (token.value.equals("tinuod")) {
            return true;
        } else if (token.value.equals("bakak")) {
            return false;
        } else if (token.value.equals("gidak_on")) {
            if (start + 1 < tokens.size() && tokens.get(start + 1).value.equals("(")) {
                int i = start + 2;
                if (i < tokens.size() && tokens.get(i).type == BisayaLexer.TokenType.IDENTIFIER) {
                    String arrayName = tokens.get(i).value;
                    if (variables.containsKey(arrayName) && variables.get(arrayName) instanceof Object[]) {
                        Object[] array = (Object[])variables.get(arrayName);
                        return array.length;
                    }
                }
            }
            return 0;
        }
        
        return "";
    }
    
    private int handleVariableDeclaration(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        if (i >= tokens.size() || tokens.get(i).type != BisayaLexer.TokenType.IDENTIFIER) {
            throw new RuntimeException("Variable declaration requires an identifier");
        }
        
        String varName = tokens.get(i).value;
        i++;
        
        if (i >= tokens.size()) {
            variables.put(varName, null);
        } else if (tokens.get(i).value.equals("=")) {
            i++;
            if (i >= tokens.size()) {
                throw new RuntimeException("Expected value after '='");
            }
            Object value = evaluateFullExpression(tokens, i);
            variables.put(varName, value);
            
            while (i < tokens.size() && !tokens.get(i).value.equals(";")) {
                i++;
            }
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private int handleSet(ArrayList<BisayaLexer.Token> tokens, int start) {
        return handleVariableDeclaration(tokens, start);
    }
    
    private int handleArrayDeclaration(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        if (i >= tokens.size() || tokens.get(i).type != BisayaLexer.TokenType.IDENTIFIER) {
            throw new RuntimeException("Array declaration requires an identifier");
        }
        
        String arrayName = tokens.get(i).value;
        i++;
        
        if (i >= tokens.size() || !tokens.get(i).value.equals("=")) {
            throw new RuntimeException("Array declaration requires '='");
        }
        i++;
        
        if (i >= tokens.size()) {
            throw new RuntimeException("Expected array initialization");
        }
        
        if (tokens.get(i).value.equals("bag_o")) {
            i++;
            if (i >= tokens.size() || !tokens.get(i).value.equals("[")) {
                throw new RuntimeException("Expected '[' after bag_o");
            }
            i++;
            
            Object sizeExpr = evaluateFullExpressionUntil(tokens, i, "]");
            int size = 0;
            if (sizeExpr instanceof Number) {
                size = ((Number)sizeExpr).intValue();
            } else {
                throw new RuntimeException("Array size must be a number");
            }
            
            while (i < tokens.size() && !tokens.get(i).value.equals("]")) {
                i++;
            }
            if (i < tokens.size()) i++;
            
            Object[] array = new Object[size];
            variables.put(arrayName, array);
            
        } else if (tokens.get(i).value.equals("[")) {
            i++;
            ArrayList<Object> elements = new ArrayList<>();
            
            while (i < tokens.size() && !tokens.get(i).value.equals("]")) {
                Object value = evaluateFullExpressionUntil(tokens, i, ",", "]");
                elements.add(value);
                
                while (i < tokens.size() && !tokens.get(i).value.equals(",") && !tokens.get(i).value.equals("]")) {
                    i++;
                }
                
                if (i < tokens.size() && tokens.get(i).value.equals(",")) {
                    i++;
                }
            }
            
            if (i < tokens.size() && tokens.get(i).value.equals("]")) {
                i++;
            }
            
            Object[] array = elements.toArray();
            variables.put(arrayName, array);
        } else {
            throw new RuntimeException("Invalid array initialization");
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private int handleArrayAccess(ArrayList<BisayaLexer.Token> tokens, int start) {
        String arrayName = tokens.get(start).value;
        int i = start + 1;
        
        if (!variables.containsKey(arrayName)) {
            throw new RuntimeException("Array '" + arrayName + "' not declared");
        }
        
        Object arrayObj = variables.get(arrayName);
        if (!(arrayObj instanceof Object[])) {
            throw new RuntimeException("'" + arrayName + "' is not an array");
        }
        
        Object[] array = (Object[])arrayObj;
        
        if (i >= tokens.size() || !tokens.get(i).value.equals("[")) {
            throw new RuntimeException("Expected '[' after array name");
        }
        i++;
        
        Object indexExpr = evaluateFullExpressionUntil(tokens, i, "]");
        int index = 0;
        if (indexExpr instanceof Number) {
            index = ((Number)indexExpr).intValue();
        } else {
            throw new RuntimeException("Array index must be a number");
        }
        
        while (i < tokens.size() && !tokens.get(i).value.equals("]")) {
            i++;
        }
        if (i < tokens.size()) i++;
        
        if (index < 0 || index >= array.length) {
            throw new RuntimeException("Array index out of bounds: " + index);
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals("=")) {
            i++;
            Object value = evaluateFullExpression(tokens, i);
            array[index] = value;
            
            while (i < tokens.size() && !tokens.get(i).value.equals(";")) {
                i++;
            }
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private Object evaluateFullExpressionUntil(ArrayList<BisayaLexer.Token> tokens, int start, String... delimiters) {
        if (start >= tokens.size()) return 0;
        
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();
        
        int i = start;
        while (i < tokens.size()) {
            boolean isDelimiter = false;
            for (String delim : delimiters) {
                if (tokens.get(i).value.equals(delim)) {
                    isDelimiter = true;
                    break;
                }
            }
            if (isDelimiter) break;
            
            BisayaLexer.Token token = tokens.get(i);
            
            if (token.type == BisayaLexer.TokenType.NUMBER) {
                if (token.value.contains(".")) {
                    values.add(Double.parseDouble(token.value));
                } else {
                    values.add(Integer.parseInt(token.value));
                }
            } else if (token.type == BisayaLexer.TokenType.STRING) {
                values.add(token.value);
            } else if (token.type == BisayaLexer.TokenType.IDENTIFIER) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).value.equals("[")) {
                    String arrayName = token.value;
                    i += 2;
                    Object indexExpr = evaluateFullExpressionUntil(tokens, i, "]");
                    int index = indexExpr instanceof Number ? ((Number)indexExpr).intValue() : 0;
                    
                    while (i < tokens.size() && !tokens.get(i).value.equals("]")) {
                        i++;
                    }
                    
                    if (variables.containsKey(arrayName) && variables.get(arrayName) instanceof Object[]) {
                        Object[] array = (Object[])variables.get(arrayName);
                        if (index >= 0 && index < array.length) {
                            values.add(array[index]);
                        } else {
                            throw new RuntimeException("Array index out of bounds: " + index);
                        }
                    } else {
                        throw new RuntimeException("'" + arrayName + "' is not an array");
                    }
                } else if (variables.containsKey(token.value)) {
                    values.add(variables.get(token.value));
                } else {
                    values.add(0);
                }
            } else if (token.type == BisayaLexer.TokenType.OPERATOR && 
                       "+-*/%^".indexOf(token.value) != -1) {
                operators.add(token.value);
            }
            
            i++;
        }
        
        if (values.isEmpty()) return 0;
        if (values.size() == 1) return values.get(0);
        
        for (int j = 0; j < operators.size(); j++) {
            if (operators.get(j).equals("^")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, "^", right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        for (int j = 0; j < operators.size(); j++) {
            String op = operators.get(j);
            if (op.equals("*") || op.equals("/") || op.equals("%")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, op, right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        for (int j = 0; j < operators.size(); j++) {
            String op = operators.get(j);
            if (op.equals("+") || op.equals("-")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, op, right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        return values.get(0);
    }
    
    private int handleAssignment(ArrayList<BisayaLexer.Token> tokens, int start) {
        String varName = tokens.get(start).value;
        int i = start + 1;
        
        if (i >= tokens.size()) {
            throw new RuntimeException("Incomplete assignment for variable '" + varName + "'");
        }
        
        String op = tokens.get(i).value;
        i++;
        
        if (op.equals("++")) {
            if (!variables.containsKey(varName)) {
                throw new RuntimeException("Variable '" + varName + "' not declared");
            }
            Object val = variables.get(varName);
            if (val instanceof Integer) {
                variables.put(varName, (Integer)val + 1);
            } else if (val instanceof Double) {
                variables.put(varName, (Double)val + 1);
            } else {
                throw new RuntimeException("Cannot increment non-numeric variable '" + varName + "'");
            }
        } else if (op.equals("--")) {
            if (!variables.containsKey(varName)) {
                throw new RuntimeException("Variable '" + varName + "' not declared");
            }
            Object val = variables.get(varName);
            if (val instanceof Integer) {
                variables.put(varName, (Integer)val - 1);
            } else if (val instanceof Double) {
                variables.put(varName, (Double)val - 1);
            } else {
                throw new RuntimeException("Cannot decrement non-numeric variable '" + varName + "'");
            }
        } else if (op.equals("=")) {
            if (i >= tokens.size()) {
                throw new RuntimeException("Expected value after '='");
            }
            Object value = evaluateFullExpression(tokens, i);
            variables.put(varName, value);
            while (i < tokens.size() && !tokens.get(i).value.equals(";")) {
                i++;
            }
        } else if (op.equals("+=") || op.equals("-=") || op.equals("*=") || 
                   op.equals("/=") || op.equals("%=") || op.equals("^=")) {
            if (!variables.containsKey(varName)) {
                throw new RuntimeException("Variable '" + varName + "' not declared");
            }
            if (i >= tokens.size()) {
                throw new RuntimeException("Expected value after '" + op + "'");
            }
            Object value = evaluateFullExpression(tokens, i);
            variables.put(varName, applyCompoundOp(variables.get(varName), op, value));
            while (i < tokens.size() && !tokens.get(i).value.equals(";")) {
                i++;
            }
        }
        
        while (i < tokens.size() && tokens.get(i).value.equals(";")) {
            i++;
        }
        
        return i;
    }
    
    private int handleKung(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        if (i >= tokens.size()) {
            throw new RuntimeException("kung statement requires a condition");
        }
        
        if (tokens.get(i).value.equals("(")) {
            i++;
        } else {
            throw new RuntimeException("kung statement requires parentheses around condition");
        }
        
        if (i >= tokens.size()) {
            throw new RuntimeException("kung statement missing condition");
        }
        
        boolean condition = evaluateCondition(tokens, i);
        
        while (i < tokens.size() && !tokens.get(i).value.equals(")")) {
            i++;
        }
        if (i < tokens.size()) {
            i++;
        } else {
            throw new RuntimeException("kung statement missing closing parenthesis");
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals(":")) {
            i++;
        } else {
            throw new RuntimeException("kung statement requires ':' after condition");
        }
        
        if (condition) {
            while (i < tokens.size() && !isBlockEnd(tokens.get(i))) {
                i = executeStatement(tokens, i);
            }
        } else {
            int depth = 0;
            while (i < tokens.size()) {
                if (tokens.get(i).value.equals("kung") || tokens.get(i).value.equals("para") || 
                    tokens.get(i).value.equals("samtang")) {
                    depth++;
                }
                if (isBlockEnd(tokens.get(i))) {
                    if (depth == 0) break;
                    depth--;
                }
                i++;
            }
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals("end")) {
            i++;
        }
        
        return i;
    }
    
    private int handleSamtang(ArrayList<BisayaLexer.Token> tokens, int start) {
        int conditionStart = start + 1;
        
        while (true) {
            int i = conditionStart;
            
            if (i < tokens.size() && tokens.get(i).value.equals("(")) {
                i++;
            }
            
            boolean condition = evaluateCondition(tokens, i);
            
            while (i < tokens.size() && !tokens.get(i).value.equals(")")) {
                i++;
            }
            if (i < tokens.size()) i++;
            
            if (i < tokens.size() && tokens.get(i).value.equals(":")) {
                i++;
            }
            
            if (!condition) {
                while (i < tokens.size() && !isBlockEnd(tokens.get(i))) {
                    i++;
                }
                return i;
            }
            
            while (i < tokens.size() && !isBlockEnd(tokens.get(i))) {
                i = executeStatement(tokens, i);
            }
        }
    }
    
    private int handlePara(ArrayList<BisayaLexer.Token> tokens, int start) {
        int i = start + 1;
        
        while (i < tokens.size() && tokens.get(i).value.equals("(")) {
            i++;
        }
        
        String varName = "";
        if (i < tokens.size() && tokens.get(i).type == BisayaLexer.TokenType.IDENTIFIER) {
            varName = tokens.get(i).value;
            i++;
        } else {
            throw new RuntimeException("para loop requires loop variable");
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals("=")) {
            i++;
        } else {
            throw new RuntimeException("para loop requires '=' after variable");
        }
        
        int startVal = 0;
        int startExprEnd = i;
        while (startExprEnd < tokens.size() && 
               !tokens.get(startExprEnd).value.equals("to") && 
               !tokens.get(startExprEnd).value.equals(")")) {
            startExprEnd++;
        }
        
        Object startResult = evaluateFullExpression(tokens, i);
        if (startResult instanceof Number) {
            startVal = ((Number)startResult).intValue();
        } else {
            throw new RuntimeException("para loop start value must be a number");
        }
        
        i = startExprEnd;
        
        if (i < tokens.size() && tokens.get(i).value.equals("to")) {
            i++;
        } else {
            throw new RuntimeException("para loop requires 'to' keyword");
        }
        
        int endVal = 0;
        int endExprEnd = i;
        while (endExprEnd < tokens.size() && 
               !tokens.get(endExprEnd).value.equals(":") && 
               !tokens.get(endExprEnd).value.equals(")")) {
            endExprEnd++;
        }
        
        Object endResult = evaluateFullExpression(tokens, i);
        if (endResult instanceof Number) {
            endVal = ((Number)endResult).intValue();
        } else {
            throw new RuntimeException("para loop end value must be a number");
        }
        
        i = endExprEnd;
        
        if (i < tokens.size() && tokens.get(i).value.equals(")")) {
            i++;
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals(":")) {
            i++;
        } else {
            throw new RuntimeException("para loop requires ':' after range");
        }
        
        int bodyStart = i;
        
        if (startVal <= endVal) {
            for (int counter = startVal; counter <= endVal; counter++) {
                variables.put(varName, counter);
                
                i = bodyStart;
                while (i < tokens.size() && !isBlockEnd(tokens.get(i))) {
                    i = executeStatement(tokens, i);
                }
            }
        } else {
            for (int counter = startVal; counter >= endVal; counter--) {
                variables.put(varName, counter);
                
                i = bodyStart;
                while (i < tokens.size() && !isBlockEnd(tokens.get(i))) {
                    i = executeStatement(tokens, i);
                }
            }
        }
        
        if (i < tokens.size() && tokens.get(i).value.equals("end")) {
            i++;
        }
        
        return i;
    }
    
    private int executeStatement(ArrayList<BisayaLexer.Token> tokens, int start) {
        if (start >= tokens.size()) return start;
        
        BisayaLexer.Token token = tokens.get(start);
        
        if (token.type == BisayaLexer.TokenType.KEYWORD) {
            if (token.value.equals("sulti")) {
                return handleSulti(tokens, start);
            } else if (token.value.equals("numero") || token.value.equals("teksto") || 
                       token.value.equals("karakter") || token.value.equals("boolean")) {
                return handleVariableDeclaration(tokens, start);
            } else if (token.value.equals("set")) {
                return handleSet(tokens, start);
            } else if (token.value.equals("kung")) {
                return handleKung(tokens, start);
            } else if (token.value.equals("samtang")) {
                return handleSamtang(tokens, start);
            } else if (token.value.equals("para")) {
                return handlePara(tokens, start);
            } else if (token.value.equals("end")) {
                return start;
            }
        } else if (token.type == BisayaLexer.TokenType.IDENTIFIER) {
            return handleAssignment(tokens, start);
        }
        
        return start + 1;
    }
    
    private boolean isBlockEnd(BisayaLexer.Token token) {
        return token.value.equals("kontra") || token.value.equals("end");
    }
    
    private boolean evaluateCondition(ArrayList<BisayaLexer.Token> tokens, int start) {
        if (start >= tokens.size()) return false;
        
        int i = start;
        Object left = evaluateExpression(tokens, i);
        i++;
        
        if (i >= tokens.size() || tokens.get(i).value.equals(")")) {
            if (left instanceof Boolean) return (Boolean)left;
            return false;
        }
        
        String op = tokens.get(i).value;
        i++;
        
        Object right = evaluateExpression(tokens, i);
        
        return compareValues(left, op, right);
    }
    
    private boolean compareValues(Object left, String op, Object right) {
        if (left instanceof Number && right instanceof Number) {
            double l = ((Number)left).doubleValue();
            double r = ((Number)right).doubleValue();
            
            switch (op) {
                case "==": return l == r;
                case "!=": return l != r;
                case ">": return l > r;
                case "<": return l < r;
                case ">=": return l >= r;
                case "<=": return l <= r;
            }
        }
        
        if (op.equals("==")) {
            return left != null && left.equals(right);
        } else if (op.equals("!=")) {
            return left == null || !left.equals(right);
        }
        
        return false;
    }
    
    private Object evaluateFullExpression(ArrayList<BisayaLexer.Token> tokens, int start) {
        if (start >= tokens.size()) return 0;
        
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();
        
        int i = start;
        while (i < tokens.size() && !tokens.get(i).value.equals(";") && !tokens.get(i).value.equals(")")) {
            BisayaLexer.Token token = tokens.get(i);
            
            if (token.type == BisayaLexer.TokenType.NUMBER) {
                if (token.value.contains(".")) {
                    values.add(Double.parseDouble(token.value));
                } else {
                    values.add(Integer.parseInt(token.value));
                }
            } else if (token.type == BisayaLexer.TokenType.STRING) {
                values.add(token.value);
            } else if (token.type == BisayaLexer.TokenType.IDENTIFIER) {
                if (variables.containsKey(token.value)) {
                    values.add(variables.get(token.value));
                } else {
                    values.add(0);
                }
            } else if (token.type == BisayaLexer.TokenType.OPERATOR && 
                       "+-*/%^".indexOf(token.value) != -1) {
                operators.add(token.value);
            }
            
            i++;
        }
        
        if (values.isEmpty()) return 0;
        if (values.size() == 1) return values.get(0);
        
        for (int j = 0; j < operators.size(); j++) {
            if (operators.get(j).equals("^")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, "^", right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        for (int j = 0; j < operators.size(); j++) {
            String op = operators.get(j);
            if (op.equals("*") || op.equals("/") || op.equals("%")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, op, right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        for (int j = 0; j < operators.size(); j++) {
            String op = operators.get(j);
            if (op.equals("+") || op.equals("-")) {
                Object left = values.get(j);
                Object right = values.get(j + 1);
                values.set(j, applyOperation(left, op, right));
                values.remove(j + 1);
                operators.remove(j);
                j--;
            }
        }
        
        return values.get(0);
    }
    
    private Object applyOperation(Object left, String op, Object right) {
        if (left instanceof Number && right instanceof Number) {
            double l = ((Number)left).doubleValue();
            double r = ((Number)right).doubleValue();
            
            switch (op) {
                case "+": return l + r;
                case "-": return l - r;
                case "*": return l * r;
                case "/": return r != 0 ? l / r : 0;
                case "%": return l % r;
                case "^": return Math.pow(l, r);
            }
        }
        
        return 0;
    }
    
    private Object applyCompoundOp(Object current, String op, Object value) {
        if (current instanceof Number && value instanceof Number) {
            double c = ((Number)current).doubleValue();
            double v = ((Number)value).doubleValue();
            
            switch (op) {
                case "+=": return c + v;
                case "-=": return c - v;
                case "*=": return c * v;
                case "/=": return v != 0 ? c / v : c;
                case "%=": return c % v;
                case "^=": return Math.pow(c, v);
            }
        }
        
        return current;
    }
    
    public String getOutput() {
        return output.toString();
    }
}
