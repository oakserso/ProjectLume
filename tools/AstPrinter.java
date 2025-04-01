package lumeProject.tools;

// this file was made for the purposes of printing the syntax tree nodes
// after they were parsed during the early stages of development

// ---------------------------------------------------------------------

// package lumeProject.lume;

// import lumeProject.lume.Expr.Binary;
// import lumeProject.lume.Expr.Grouping;
// import lumeProject.lume.Expr.Literal;
// import lumeProject.lume.Expr.Unary;
// import lumeProject.lume.Expr.Visitor;

// class AstPrinter implements Expr.Visitor<String> {
//     String print(Expr expr) {
//         return expr.accept(this);
//     }

//     @Override
//     public String visitBinaryExpr(Expr.Binary expr) {
//         return parenthesize(expr.operator.lexeme,
//                           expr.left, expr.right);
//     }

//     @Override
//     public String visitGroupingExpr(Expr.Grouping expr) {
//         return parenthesize("group", expr.expression);
//     }
  
//     @Override
//     public String visitLiteralExpr(Expr.Literal expr) {
//         if(expr.value == null) return "nil";
//             return expr.value.toString();
//     }

//     @Override
//     public String visitUnaryExpr(Expr.Unary expr) {
//         return parenthesize(expr.operator.lexeme, expr.right);
//     }

//     private String parenthesize(String name, Expr... exprs) {
//         StringBuilder builder = new StringBuilder();
    
//         builder.append("(").append(name);
//         for(Expr expr : exprs) {
//           builder.append(" ");
//           builder.append(expr.accept(this));
//         }
//         builder.append(")");
    
//         return builder.toString();
//     }

//     public static void main(String[] args) {
//         Expr expression = new Expr.Binary(
//             new Expr.Unary(
//                 new Token(TokenType.MINUS, "-", null, 1),
//                 new Expr.Literal(32123)),
//             new Token(TokenType.STAR, "*", null, 1),
//             new Expr.Grouping(
//                 new Expr.Literal(45.6217)));

//         System.out.println(new AstPrinter().print(expression));
//     }
// }
