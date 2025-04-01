package lumeProject.lume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lume {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: lume [script.lume]");
            System.exit(64);
        } else if (args.length == 1) {
            if (!args[0].endsWith(".lume")) {
                System.err.println("Error: Lume interpreter only accepts .lume files");
                System.exit(64);
            }
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        // Double-check the extension (in case method is called directly)
        if (!path.endsWith(".lume")) {
            System.err.println("Error: Only .lume files are supported");
            System.exit(64);
        }

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("Lume REPL (type 'exit' to quit)");
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equals("exit")) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
    
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
    
        if (hadError) return;

        interpreter.interpret(statements);
        System.out.println("");
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
    
    private static void report(int line, String where,
                                String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message);

        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
            "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}