package lumeProject.lume;

import static lumeProject.lume.TokenType.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Interpreter implements Expr.Visitor<Object>,
                            Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new LumeCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter,
                                List<Object> arguments) {
            return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });

        globals.define("readnx", new LumeCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter,
                                List<Object> arguments) {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String text = scanner.next();
            scanner.close();
            return text;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });

        globals.define("readln", new LumeCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter,
                                List<Object> arguments) {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String text = scanner.nextLine();
            scanner.close();
            return text;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch(RuntimeError error) {
            Lume.runtimeError(error);
        }
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof LumeClass)) {
                throw new RuntimeError(stmt.superclass.name,
            "Superclass must be a class.");
            }
        }

        environment.define(stmt.name.lexeme, null);

        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, LumeFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            LumeFunction function = new LumeFunction(method, environment, 
                method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        LumeClass klass = new LumeClass(stmt.name.lexeme,
            (LumeClass)superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new Break();
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LumeFunction function = new LumeFunction(stmt, environment, 
                false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    void executeBlock(List<Stmt> statements,
                    Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (Break b) {
                break;
            }
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        if (stmt.printType.type == PRINT) System.out.print(stringify(value));
        else System.out.println(stringify(value));
        return null;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        LumeClass superclass = (LumeClass)environment.getAt(
            distance, "super");
        
        LumeInstance object = (LumeInstance)environment.getAt(
            distance - 1, "this");

        LumeFunction method = superclass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method,
                "Undefined property '" + expr.method.lexeme + "'.");
        }

        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
    
        if (!(object instanceof LumeInstance)) { 
            throw new RuntimeError(expr.name,
                                "Only instances have fields.");
        }
    
        Object value = evaluate(expr.value);
        ((LumeInstance)object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof LumeInstance) {
            return ((LumeInstance)object).get(expr.name);
        }
    
        throw new RuntimeError(expr.name,
            "Only instances have properties.");
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) { 
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LumeCallable)) {
            throw new RuntimeError(expr.paren,
                "Can only call functions and classes.");
        }

        LumeCallable function = (LumeCallable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " +
                function.arity() + " arguments but got " +
                arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
    
        switch(expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            case BANG:
                return !isTruthy(right);
            case PLUS_PLUS:
            case MINUS_MINUS:
                if (!(expr.right instanceof Expr.Variable)) {
                    throw new RuntimeError(expr.operator, 
                        "Operand of '" + expr.operator.lexeme + "' must be a variable.");
                }
    
                if (!(right instanceof Double)) {
                    throw new RuntimeError(expr.operator, "Operand must be a number.");
                }
    
                double value = (double)right;
                double updated = (expr.operator.type == TokenType.PLUS_PLUS) ? value + 1 : value - 1;
    
                Token name = ((Expr.Variable)expr.right).name;
                environment.assign(name, updated);

                return updated;
            default:
                break;
        }
    
        return null;
    }

    @Override
    public Object visitPostfixExpr(Expr.Postfix expr) {
        Object left = evaluate(expr.left);

        if (!(expr.left instanceof Expr.Variable)) {
            throw new RuntimeError(expr.operator, 
                "Operand of '" + expr.operator.lexeme + "' must be a variable.");
        }

        if (!(left instanceof Double)) {
            throw new RuntimeError(expr.operator, "Operand must be a number.");
        }

        double value = (double)left;
        double updated = (expr.operator.type == TokenType.PLUS_PLUS) ? value + 1 : value - 1;

        Token name = ((Expr.Variable)expr.left).name;
        environment.assign(name, updated);

        return value;   
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;

        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                if (left instanceof String && right instanceof Double) {
                    String rightString = right.toString();
                    return (String)left + rightString.substring(0, rightString.length() - 2);
                }

                if (left instanceof Double && right instanceof String) {
                    String leftString = left.toString();
                    return leftString.substring(0, leftString.length() - 2) + (String)right;
                }

                throw new RuntimeError(expr.operator,
            "Operands must be numbers or strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            default:
                break;
        }

        return null;
    }

    private void checkNumberOperands(Token operator,
                                Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
