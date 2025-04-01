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
