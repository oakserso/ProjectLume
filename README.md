# Lume: A High-Performance, Object-Oriented Tree-Walk Interpreter in Java

**Lume** is a fully-featured, dynamically-typed scripting language interpreter implemented in **pure Java**, designed to support a wide range of language features including **classes, inheritance, closures, lexical scoping, native functions, and postfix expression evaluation**.

This project demonstrates deep understanding of **language design**, **compiler theory**, and **runtime system architecture**, all built without external dependencies — just clean, extensible, production-quality Java.

---

## Core Features and Engineering Highlights

- **Custom Lexer and Recursive Descent Parser**  
  Built from scratch, supporting full token stream processing and AST construction via custom expression and statement classes. The grammar supports:
  - Class declarations, inheritance, `this`, `super`
  - First-class functions, closures, and lexical scoping
  - `for`, `while`, `if/else`, `break`, and `return` control flow
  - Postfix and prefix unary operators (`--i`, `i++`)
  - Flexible binary operations including number-string coercion
  - Grouping, equality, logical (short-circuit) expressions

- **Visitor Pattern–Driven AST Traversal**  
  Implements a clean and modular **Visitor design pattern** for both interpretation and resolution phases. This allows easy extensibility and separates syntax from behavior.

- **Lexical Scope Resolution**  
  Built a **two-pass interpreter** system using a `Resolver` class to statically analyze and resolve variable scopes before execution. Implements **shadowing detection**, **read-before-define protection**, and **class method resolution**.

- **OOP Runtime Support**  
  - **Classes and Subclasses** with full inheritance
  - Runtime support for `this` binding and method resolution
  - Dynamic dispatch and method lookup
  - Validations for improper superclass references (e.g. self-inheritance)

- **Custom Runtime Environment (No External VM)**  
  - Stack-based scoping with chained `Environment` objects
  - Fully isolated execution contexts for functions and blocks
  - Variable resolution using depth-based environment lookup
  - Runtime error handling with custom `RuntimeError` types

- **Native Function Interface (NFI)**  
  Lume allows bridging native Java functionality into the language. Examples:
  - `clock()` – returns system time in seconds
  - `readln()` / `readnx()` – native stdin reading support

- **Postfix Expression Evaluation (`--i`, `i++`)**  
  Added support for postfix evaluation **beyond the original Lox spec**. Handles subtle evaluation order differences (e.g., returns original value but still mutates variable) — just like in JavaScript, Java, and C++.

- **Error Recovery & Synchronization**  
  Implements robust error recovery in the parser to allow multiple statements to be interpreted even after encountering syntax errors.

- **REPL and Script Mode Support**  
  Lume runs in both:
  - **Interactive Prompt (REPL)** with persistent state
  - **Script mode**, executing `.lume` files from disk with correct exit codes based on error types
 
## Advanced Concepts and Language Architecture Highlights

Lume implements a rich set of advanced compiler and interpreter features, demonstrating mastery of core language tooling techniques and runtime behavior. Highlights include:

- **Abstract Syntax Tree (AST)**  
  Full AST construction for expressions and statements, enabling modular parsing and evaluation workflows.

- **Visitor Pattern (Expr & Stmt)**  
  Cleanly separates logic from structure using the Visitor pattern for both interpretation and scope resolution, allowing high extensibility and separation of concerns.

- **Lexical Environment Stack**  
  Implements nested, block-scoped environments with dynamic chaining and runtime resolution to simulate call frames and closures.

- **Static Variable Resolution**  
  A pre-runtime pass resolves all variable references to their lexical depth, improving performance and catching shadowing or forward-reference errors.

- **Error Recovery & Synchronization**  
  The parser gracefully recovers from syntax errors using a synchronization mechanism, allowing the REPL and interpreter to continue after encountering issues.

- **Runtime Exception Handling**  
  Custom `RuntimeError`, `Return`, and `Break` exceptions modeled for precise runtime behavior control, scoped error messages, and correct function termination.

- **First-Class Functions & Closures**  
  Functions are treated as first-class citizens, with proper lexical capture of surrounding environments for closures and lambdas.

- **Object-Oriented Programming (OOP)**  
  Supports class declarations, subclassing with `<`, method definitions, and dynamic dispatch via `this` and `super`.

- **Native Function Binding**  
  Seamless integration with native Java functions through an abstract `LumeCallable` interface. Includes built-in functions like `clock()`, `readln()`, and `readnx()`.

- **Custom Tokenization Engine**  
  Built-from-scratch lexer processes raw source into annotated token streams, enabling precise parsing and error reporting.

- **Operator Precedence & Associativity**  
  Recursive descent parsing handles all arithmetic, logical, and comparison operators with correct precedence and associativity.

- **String/Number Coercion**  
  Implements flexible and predictable behavior for `+` operator across types, including implicit coercion and type-safe concatenation.

- **Prefix & Postfix Increment Operators**  
  Extends the original Lox specification with full support for `++` and `--` (both prefix and postfix), mimicking real-world language behavior.

- **Control Flow Constructs**  
  Includes block-structured flow control with support for `return`, `break`, `if/else`, `while`, and `for`, all with syntactic and runtime validation.

- **Depth-Based Variable Lookup**  
  Optimized variable access via statically resolved scope depth, eliminating name resolution overhead at runtime.

## Sample Lume Code

```lume
class Animal {
  init(name) {
    this.name = name;
  }

  speak() {
    println this.name + " makes a noise.";
  }
}

class Dog < Animal {
  speak() {
    println this.name + " barks.";
  }
}

var d = Dog("Rex");
d.speak(); // Rex barks
```

## Build and Run Instructions

### Prerequisites
- Java 17 or later
- No third-party dependencies required

### Compilation and Execution
After either cloning the repo using ```git clone https://github.com/oakserso/ProjectLume.git``` or downloading and extracting the folder:

#### Run in REPL Mode (Interactive Prompt):
```java Lume.java```

#### Running a .lume file:
```java Lume.java [fileName].lume```

#### Notes:
- Only files with `.lume` extension are supported
- Type `exit` to quit the REPL

## Why This Project Matters

Lume isn't just a learning exercise — it's a comprehensive deep dive into **systems programming**, **language design**, and **runtime behavior modeling**. This project demonstrates:

- **Fluency in Compiler Architecture**  
  From tokenization and lexical analysis to AST construction and execution, Lume covers the full interpreter pipeline.

- **Mastery of Object-Oriented Language Features**  
  Implements runtime support for classes, inheritance, method resolution, and dynamic dispatch using `this` and `super`.

- **Memory-Safe Design Principles**  
  Uses scoped `Environment` chains and lexical resolution to safely manage variable state and function closures.

- **Custom Language Feature Implementation**  
  Adds support for native bindings, postfix operators, error recovery, and custom coercion logic — all modeled after real-world language behavior.

- **Native I/O and Extensibility**  
  Features a native function interface (`LumeCallable`) that integrates Java methods directly into the language runtime, such as `readln()` and `clock()`.

This interpreter was built with **production-quality design principles** in mind:

- **No External Frameworks**  
  Written entirely in Java with zero dependencies — no parser generators, no VMs, no toolkits.

- **High Readability and Modular Architecture**  
  Code is clean, well-structured, and separated into logical components (lexing, parsing, resolution, execution).

- **Extensible by Design**  
  The architecture is ready for future enhancements such as:
  - Import systems and module loading
  - Better error tracing and debugging support
  - Bytecode compilation or a VM-based backend

## Contact

For any inquiries or suggestions, feel free to reach out:

- Email: akiradev02@icloud.com
- LinkedIn: https://www.linkedin.com/in/odai-alqahwaji-2bbb50304
#
Project Link: [ProjectLume](https://github.com/oakserso/ProjectLume)
