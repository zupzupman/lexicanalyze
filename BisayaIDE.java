import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BisayaIDE extends JFrame {
    
    private JTextArea codeEditor;
    private JTextArea outputArea;
    private BisayaInterpreter interpreter;
    
    public BisayaIDE() {
        interpreter = new BisayaInterpreter();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Bai++ - Maayo kaayo!");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(40, 44, 52));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(33, 37, 43));
        JLabel titleLabel = new JLabel("Bai++ Programming Language");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        JPanel editorPanel = new JPanel(new BorderLayout(5, 5));
        editorPanel.setBackground(new Color(40, 44, 52));
        
        JLabel editorLabel = new JLabel("I-sulat ang imong code dinhi:");
        editorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        editorLabel.setForeground(Color.WHITE);
        
        codeEditor = new JTextArea();
        codeEditor.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeEditor.setBackground(new Color(30, 33, 39));
        codeEditor.setForeground(new Color(171, 178, 191));
        codeEditor.setCaretColor(Color.WHITE);
        codeEditor.setLineWrap(true);
        codeEditor.setWrapStyleWord(true);
        codeEditor.setText("");
        
        JScrollPane editorScroll = new JScrollPane(codeEditor);
        editorScroll.setBorder(BorderFactory.createLineBorder(new Color(61, 133, 224), 2));
        
        editorPanel.add(editorLabel, BorderLayout.NORTH);
        editorPanel.add(editorScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(40, 44, 52));
        
        JButton runButton = new JButton("Dagan! (Run)");
        runButton.setFont(new Font("Arial", Font.BOLD, 14));
        runButton.setBackground(new Color(39, 174, 96));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.setBorderPainted(false);
        runButton.setOpaque(true);
        runButton.setPreferredSize(new Dimension(180, 40));
        runButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton clearButton = new JButton("Limpyohi (Clear)");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(231, 76, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setOpaque(true);
        clearButton.setPreferredSize(new Dimension(180, 40));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton helpButton = new JButton("Tabang (Help)");
        helpButton.setFont(new Font("Arial", Font.BOLD, 14));
        helpButton.setBackground(new Color(52, 152, 219));
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setBorderPainted(false);
        helpButton.setOpaque(true);
        helpButton.setPreferredSize(new Dimension(180, 40));
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(helpButton);
        
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBackground(new Color(40, 44, 52));
        
        JLabel outputLabel = new JLabel("Output (Resulta):");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 14));
        outputLabel.setForeground(Color.WHITE);
        
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setBackground(new Color(23, 26, 31));
        outputArea.setForeground(new Color(97, 218, 251));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createLineBorder(new Color(97, 218, 251), 2));
        
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPanel, outputPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(350);
        splitPane.setBackground(new Color(40, 44, 52));
        
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCode();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeEditor.setText("");
                outputArea.setText("");
            }
        });
        
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void runCode() {
        String code = codeEditor.getText();
        outputArea.setText("Gipatugma na... (Running...)\n\n");
        
        try {
            String result = interpreter.execute(code);
            if (result.isEmpty()) {
                outputArea.append("Nahuman na! Walay i-print.\n");
            } else {
                outputArea.setText(result);
            }
            outputArea.append("\nTapos na!");
        } catch (Exception ex) {
            outputArea.setText("Naay sayop bai! (Error!)\n\n" + ex.getMessage());
        }
    }
    
    private void showHelp() {
        String helpText = 
            "BAI++ PROGRAMMING LANGUAGE\n\n" +
            "KEYWORDS:\n" +
            "  sulti       - Output/print\n" +
            "  paminaw     - Input\n" +
            "  numero      - Integer/number type\n" +
            "  teksto      - String type\n" +
            "  karakter    - Character type\n" +
            "  boolean     - Boolean type\n" +
            "  ArrayTix    - Array/list declaration\n" +
            "  bag_o       - New array (with size)\n" +
            "  gidak_on    - Array length\n" +
            "  kung        - if\n" +
            "  kontra      - else\n" +
            "  kungkontra  - else if\n" +
            "  samtang     - while\n" +
            "  para        - for\n" +
            "  baylo       - switch\n" +
            "  kaso        - case\n" +
            "  tinuod      - true\n" +
            "  bakak       - false\n" +
            "  walay       - null\n" +
            "  set         - variable assignment\n" +
            "  balik       - return\n\n" +
            "OPERATORS:\n" +
            "  Arithmetic: + - * / % ^\n" +
            "  Assignment: = += -= *= /= %= ^=\n" +
            "  Comparison: == != > < >= <=\n" +
            "  Logical: && || !\n" +
            "  Unary: ++ --\n\n" +
            "ARRAYTIX (Arrays):\n" +
            "  Create with size:\n" +
            "    ArrayTix nums = bag_o[10];\n" +
            "  Create with values:\n" +
            "    ArrayTix colors = [\"Red\", \"Blue\"];\n" +
            "  Access element:\n" +
            "    nums[0] = 5;\n" +
            "    sulti(nums[0]);\n" +
            "  Get length:\n" +
            "    sulti(gidak_on(nums));\n\n" +
            "EXAMPLES:\n\n" +
            "Variables:\n" +
            "  numero x = 10;\n" +
            "  teksto name = \"Juan\";\n\n" +
            "Arrays:\n" +
            "  ArrayTix data = bag_o[5];\n" +
            "  data[0] = 100;\n\n" +
            "Loops:\n" +
            "  para i = 1 to 10:\n" +
            "      sulti(i);\n" +
            "  end\n\n" +
            "Comments:\n" +
            "  # Single line\n" +
            "  #\" Multi-line \"#";
        
        JTextArea helpArea = new JTextArea(helpText);
        helpArea.setEditable(false);
        helpArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        helpArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(helpArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Bai++ Programming Guide", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BisayaIDE ide = new BisayaIDE();
                ide.setVisible(true);
            }
        });
    }
}
