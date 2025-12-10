# Bai++ Programming Language

Bisaya programming language gamit ang Java Swing IDE.

## Paano Patakbuhin ang Code (Para sa mga Baguhan)

Men, sundin lang ang mga hakbang na ito para mapatakbo ang Bai++ sa computer mo gamit ang VS Code:

### 1. I-install ang Java Development Kit (JDK)

1. Pumunta sa [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)
2. I-download ang **JDK 17** o mas bago pa (piliin ang version para sa operating system mo)
3. I-install ang na-download (i-click lang ang installer at sundin ang instructions)
4. Para ma-check kung na-install na:
   - Buksan ang **Command Prompt** o **Terminal**
   - I-type: `java -version`
   - Kung makita mo ang version number, okay na!

### 2. I-install ang VS Code (kung wala pa)

1. Pumunta sa [https://code.visualstudio.com/](https://code.visualstudio.com/)
2. I-download at i-install

### 3. I-install ang Java Extension Pack sa VS Code

1. Buksan ang VS Code
2. I-click ang **Extensions icon** sa left side (o press `Ctrl+Shift+X`)
3. Hanapin ang "**Extension Pack for Java**"
4. I-click ang **Install**

### 4. Buksan ang Project

1. I-download o i-clone ang project folder (yung folder na may lahat ng `.java` files)
2. Sa VS Code, i-click ang **File > Open Folder**
3. Piliin ang `lexicanalyze` folder
4. I-click ang **Select Folder**

### 5. Patakbuhin ang IDE

Men, may dalawang paraan para patakbuhin ang program:

#### Paraan 1: Gamit ang VS Code Run Button (Pinaka-madali)

1. Buksan ang file na `BisayaIDE.java`
2. Tingnan sa upper-right corner, may **Run** button (play icon ▶️)
3. I-click lang ang **Run** button
4. Hintay lang sandali, at bubuksan na ang Bai++ IDE window!

#### Paraan 2: Gamit ang Terminal

1. Sa VS Code, buksan ang **Terminal** (press `Ctrl+``)
2. I-type ang mga commands na ito sunod-sunod:

   ```bash
   javac BisayaLexer.java
   javac BisayaInterpreter.java
   javac BisayaIDE.java
   java BisayaIDE
   ```

3. Tapos bubuksan na ang IDE window!

### 6. Gamitin na!

Pagkatapos mag-load, makikita mo ang:
- **Code editor** - dito ka magsusulat ng Bai++ code mo
- **Run button** (Dagan!) - i-click para patakbuhin ang code
- **Clear button** (Limpyuha!) - i-click para i-clear ang editor at output
- **Help button** (Tabang!) - i-click kung gusto mo makita ang syntax guide

### Sample Code (Para Subukan)

Copy-paste lang ito sa code editor at i-click ang "Dagan!" button:

```
numero x = 5;
numero y = 10;
sulti("Ang total kay: ");
sulti(x + y);
sulti("");

para i = 1 to 5:
    sulti("Number ");
    sulti(i);
    sulti("");
end
```

## Kung May Error

- **"java is not recognized"** - Hindi pa na-install ang Java o hindi pa na-add sa PATH. I-install ulit ang JDK.
- **Cannot find symbol errors** - I-compile muna lahat ng .java files sa tamang order (Lexer > Interpreter > IDE)
- **GUI hindi lumalabas** - Check kung meron kang Java Runtime Environment (JRE) at kung supported ang Swing sa system mo

## Mga Keywords sa Bai++

- `sulti` - print/output
- `paminaw` - input
- `numero` - number variable
- `teksto` - string variable  
- `ArrayTix` - array declaration
- `bag_o` - new array
- `kung` - if condition
- `para` - for loop
- `samtang` - while loop

Para sa complete reference, i-click lang ang **"Tabang!"** button sa IDE!

---

**Men, enjoy coding sa Bai++! 🚀**