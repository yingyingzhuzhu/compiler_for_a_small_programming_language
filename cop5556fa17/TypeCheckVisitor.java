package cop5556fa17;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;


public class TypeCheckVisitor implements ASTVisitor {
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
	public SymbolTable table = new SymbolTable();
	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		//system.out.println("in visitDeclaration_Variable ...");
		
		String name = declaration_Variable.name;
		//firstly, visit expression to ensure if all the identifiers in the expression is already declared(in the symbolTable)
		if(declaration_Variable.e != null){
			declaration_Variable.e.visit(this, null);
		}
		//lookup
		if(table.lookupType(name) != null){
			String message = "When visitDeclaration_Variable, " + name + " is already in SymbolTable.";
			throw new SemanticException(declaration_Variable.firstToken, message);
		}
		//insert
		if(!table.insert(name, declaration_Variable)){
			String message = "When visitDeclaration_Variable,  table.insert() failed.";
			throw new SemanticException(declaration_Variable.firstToken, message);
		}
		
		//INTEGER or BOOLEAN
		if(declaration_Variable.type.getText().equals(Kind.KW_int.getText())){
			declaration_Variable.Type = Type.INTEGER;
		}
		else if(declaration_Variable.type.getText().equals(Kind.KW_boolean.getText())){
			declaration_Variable.Type = Type.BOOLEAN;
		}
		else{
			String message = "When visitDeclaration_Variable,  " + declaration_Variable.type.getText() + " is neither INTEGER or BOOLEAN.";
			throw new SemanticException(declaration_Variable.firstToken, message);
		}
		//require
		if(declaration_Variable.e != null){
			if(!declaration_Variable.Type.equals(declaration_Variable.e.Type)){
				String message = "When visitDeclaration_Variable,  " + declaration_Variable.e.Type + " != " + declaration_Variable.Type;
				throw new SemanticException(declaration_Variable.firstToken, message);
			}
		}
		
		return declaration_Variable.Type;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		//system.out.println("in visitExpression_Binary...");
		expression_Binary.e0.visit(this,null);
		expression_Binary.e1.visit(this,null);		
		Type type0 = expression_Binary.e0.Type;
		Type type1 = expression_Binary.e1.Type;
        
		if(expression_Binary.kind.getText().equals(Kind.OP_EQ.getText()) || expression_Binary.kind.getText().equals(Kind.OP_NEQ.getText())){
			expression_Binary.Type = Type.BOOLEAN;
		}
        else if((expression_Binary.kind.getText().equals(Kind.OP_GE.getText()) || expression_Binary.kind.getText().equals(Kind.OP_GT.getText())
        		|| expression_Binary.kind.getText().equals(Kind.OP_LE.getText()) || expression_Binary.kind.getText().equals(Kind.OP_LT.getText()))
        		&& type0.equals(Type.INTEGER)){
        	expression_Binary.Type = Type.BOOLEAN;
        }
		else if((expression_Binary.kind.getText().equals(Kind.OP_AND.getText()) || expression_Binary.kind.getText().equals(Kind.OP_OR.getText()))
				&& (type0.equals(Type.INTEGER) || type0.equals(Type.BOOLEAN))){
			expression_Binary.Type = type0;	
		}
		else if((expression_Binary.kind.getText().equals(Kind.OP_DIV.getText()) || expression_Binary.kind.getText().equals(Kind.OP_MINUS.getText())
        		|| expression_Binary.kind.getText().equals(Kind.OP_MOD.getText()) || expression_Binary.kind.getText().equals(Kind.OP_PLUS.getText())
        		|| expression_Binary.kind.getText().equals(Kind.OP_POWER.getText()) || expression_Binary.kind.getText().equals(Kind.OP_TIMES.getText()))
				&& type0.equals(Type.INTEGER)){
			expression_Binary.Type = Type.INTEGER;
		}
		else{
			expression_Binary.Type = null;
		}
        if(type0 != type1 || expression_Binary.Type == null){
        	String message = "When visitExpression_Binary, type0 = " + type0 + " type1 = " + type1 + " expression_Binary.Type = " + expression_Binary.Type;
        	throw new SemanticException(expression_Binary.firstToken, message);
        }
        return expression_Binary.Type;
		
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		//system.out.println("in visitExpression_Unary...");
		expression_Unary.e.visit(this, null);
		Type t = expression_Unary.e.Type;
		Kind op = expression_Unary.kind;
		if(op.getText().equals(Kind.OP_EXCL.getText()) 
				&& (t.equals(Type.BOOLEAN) || t.equals(Type.INTEGER))){
			expression_Unary.Type = t;
		}
		else if((op.getText().equals(Kind.OP_PLUS.getText()) || op.getText().equals(Kind.OP_MINUS.getText()))
				&& t.equals(Type.INTEGER)){
			expression_Unary.Type = Type.INTEGER;
		}
		else{
			expression_Unary.Type = null;
		}
		if(expression_Unary.Type == null){
			String message = "When visitExpression_Unary, expression_Unary.Type is " + expression_Unary.Type;
        	throw new SemanticException(expression_Unary.firstToken, message);
		}
		return expression_Unary.Type;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		index.e0.visit(this, null);
		index.e1.visit(this, null);
		Type type0 = index.e0.Type;
		Type type1 = index.e1.Type;
		if(type0.equals(Type.INTEGER) && type1.equals(Type.INTEGER)){
			boolean isCartesian = ! (index.e0.firstToken.kind.equals(Kind.KW_r) && index.e1.firstToken.kind.equals(Kind.KW_a));	
			index.setCartesian(isCartesian);
		}
		else{
			String message = "When visitIndex, type0 = " + type0 + " or type1 = " + type1 + "is not INTEGER. ";
			throw new SemanticException(index.firstToken, message);
		}
		return index.isCartesian();
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		//system.out.println("in visitExpression_PixelSelector...");
		expression_PixelSelector.index.visit(this, null);
		Index index = expression_PixelSelector.index;   //index
		String ident_name = expression_PixelSelector.name; // IDENTIFIER
		Type ident_type = table.lookupType(ident_name);
		if(ident_type == null){
			String message = "When visitExpression_Unary, " + ident_name + " is not in symbolTable. ";
	    		throw new SemanticException(expression_PixelSelector.firstToken, message);
		}
		if(ident_type.equals(Type.IMAGE)){
			expression_PixelSelector.Type = Type.INTEGER;
		}
		else if(index == null){
			expression_PixelSelector.Type = ident_type;
		}
		else{
			expression_PixelSelector.Type = null;
		}
		if(expression_PixelSelector.Type != null){
			return expression_PixelSelector.Type;
		}
		String message = "When visitExpression_Unary, expression_PixelSelector.Type = " + expression_PixelSelector.Type;
    	throw new SemanticException(expression_PixelSelector.firstToken, message);
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		//system.out.println("in visitExpression_Conditional...");
		expression_Conditional.falseExpression.visit(this, null);
		expression_Conditional.trueExpression.visit(this, null);
		expression_Conditional.condition.visit(this,null);

		if(Type.BOOLEAN.equals(expression_Conditional.condition.Type)
				&& expression_Conditional.trueExpression.Type != null && expression_Conditional.trueExpression.Type.equals(expression_Conditional.falseExpression.Type)){
			expression_Conditional.Type = expression_Conditional.trueExpression.Type;
		}
		else{
			String message = "When visitExpression_Conditional, expression_Conditional.condition.Type is " + expression_Conditional.condition.Type
					+ ", expression_Conditional.trueExpression.Type is " + expression_Conditional.trueExpression.Type
					+ ", expression_Conditional.falseExpression.Type is " + expression_Conditional.falseExpression.Type;
			
	    	throw new SemanticException(expression_Conditional.firstToken, message);
		}
		return expression_Conditional.Type;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		//system.out.println("in visitDeclaration_Image...");
		String name = declaration_Image.name;
		//source visit
		if(declaration_Image.source != null){
			declaration_Image.source.visit(this, null); 
		}
		//xSize visit
		if(declaration_Image.xSize != null && declaration_Image.ySize != null){
			declaration_Image.xSize.visit(this, null);
			//ySize visit
			declaration_Image.ySize.visit(this, null);
			if(!declaration_Image.xSize.Type.equals(Type.INTEGER) || !declaration_Image.ySize.Type.equals(Type.INTEGER)){
				String message = "When visitDeclaration_Image, declaration_Image.xSize.Type = " + declaration_Image.xSize.Type
						+ " and declaration_Image.ySize.Type = " + declaration_Image.ySize.Type;
		    		throw new SemanticException(declaration_Image.firstToken, message);
			}
		}
		else if(declaration_Image.xSize != null || declaration_Image.ySize != null){
			// one of xSize or ySize is null
			String message = "When visitDeclaration_Image, declaration_Image.xSize = " + declaration_Image.xSize
					+ " and declaration_Image.ySize = " + declaration_Image.ySize;
	    		throw new SemanticException(declaration_Image.firstToken, message);
		}
		
		//lookup
		if(table.lookupType(name) != null){
			String message = "When visitDeclaration_Image, declaration_Image.name = " + name
					+ " is already in SymbolTalbe.";
	    		throw new SemanticException(declaration_Image.firstToken, message);
		}
		//insert
		if(!table.insert(name, declaration_Image)){
			String message = "When visitDeclaration_Image, symbolTable.insert(" + name + ", " + declaration_Image + ") failed. ";
	    	throw new SemanticException(declaration_Image.firstToken, message);
		}
		declaration_Image.Type = Type.IMAGE;
		return declaration_Image.Type;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		//system.out.println("in visitSource_StringLiteral...");
		String name = source_StringLiteral.fileOrUrl;
		try{
			new java.net.URL(name);
			//System.out.println("URL");
			source_StringLiteral.Type = Type.URL;
		}
		catch(Exception e){
			//System.out.println("FILE");
			source_StringLiteral.Type = Type.FILE;
		}
		
		return source_StringLiteral.Type;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		//system.out.println("in visitSource_CommandLineParam...");
		source_CommandLineParam.paramNum.visit(this, null);
		source_CommandLineParam.Type = null;
		if(source_CommandLineParam.paramNum.Type.equals(Type.INTEGER)){
			return source_CommandLineParam.Type;
		}
		String message = "When visitSource_CommandLineParam, source_CommandLineParam.Type is " + source_CommandLineParam.Type
				+ ", not INTEGER";
    		throw new SemanticException(source_CommandLineParam.firstToken, message);
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		//system.out.println("in visitSource_Ident...");
		String name = source_Ident.name;
		source_Ident.Type = table.lookupType(name);
		if(source_Ident.Type != null && (source_Ident.Type.equals(Type.FILE) || source_Ident.Type.equals(Type.URL))){
			return source_Ident.Type;
		}
		String message = "When visitSource_Ident, source_Ident.Type is " + source_Ident.Type + ", not FILE or URL";
    	throw new SemanticException(source_Ident.firstToken, message);
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		//system.out.println("in visitDeclaration_SourceSink...");
		String name = declaration_SourceSink.name;
		Kind type = declaration_SourceSink.type;
		declaration_SourceSink.source.visit(this, null);
		//lookup
		if(table.lookupType(name) == null){
			//insert
			if(!table.insert(name, declaration_SourceSink)){
				String message = "When visitDeclaration_SourceSink, symbolTable.insert(" + name + ", " + declaration_SourceSink + ") failed. ";
				throw new SemanticException(declaration_SourceSink.firstToken, message);
			}
			//URL or FILE
			if(type.getText().equals(Kind.KW_url.getText())){
				declaration_SourceSink.Type = Type.URL;
			}
			else if(type.getText().equals(Kind.KW_file.getText())){
				declaration_SourceSink.Type = Type.FILE;				
			}
			else{
				String message = "When visitDeclaration_SourceSink, declaration_SourceSink.type is " + type
						+ ", not FILE or URL";
				throw new SemanticException(declaration_SourceSink.firstToken, message);
			}
			if(declaration_SourceSink.source.Type != null
					&& !declaration_SourceSink.source.Type.equals(declaration_SourceSink.Type)){
				String message = "When visitDeclaration_SourceSink, declaration_SourceSink.Type is " + declaration_SourceSink.Type
						+ ", declaration_SourceSink.source.Type is: " + declaration_SourceSink.source.Type;
		    		throw new SemanticException(declaration_SourceSink.firstToken, message);
			}
			return declaration_SourceSink.type;
		}
		else{
			String message = "When visitDeclaration_SourceSink, " + name
					+ " is already in SymbolTable.";
	    	throw new SemanticException(declaration_SourceSink.firstToken, message);			
		}
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		//system.out.println("in visitExpression_IntLit...");
		expression_IntLit.Type = Type.INTEGER;  //decorate node
	    return expression_IntLit.Type; 
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {		
		//system.out.println("in visitExpression_FunctionAppWithExprArg...");
		
		if(expression_FunctionAppWithExprArg.arg != null){
			//Expression arg visit
			expression_FunctionAppWithExprArg.arg.visit(this, null);
			//Expression Type is INTEGER
			if(expression_FunctionAppWithExprArg.arg.Type.equals(Type.INTEGER)){
				expression_FunctionAppWithExprArg.Type = Type.INTEGER;
			}
			else{
				String message = "When visitExpression_FunctionAppWithExprArg, " + expression_FunctionAppWithExprArg.arg.Type
						+ " is not INTEGER.";
		    	throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, message);
			}
		}
		return expression_FunctionAppWithExprArg.Type;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		//system.out.println("in visitExpression_FunctionAppWithIndexArg...");
		//index visit
		if(expression_FunctionAppWithIndexArg.arg != null){
			expression_FunctionAppWithIndexArg.arg.visit(this, null);
		}
		expression_FunctionAppWithIndexArg.Type = Type.INTEGER;
		return expression_FunctionAppWithIndexArg.Type;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		//system.out.println("in visitExpression_PredefinedName...");

		expression_PredefinedName.Type = Type.INTEGER;
		return expression_PredefinedName.Type;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		//system.out.println("in visitStatement_Out...");

		String name = statement_Out.name;
		Sink sink = statement_Out.sink;
		sink.visit(this, null);
		//REQUIRE:  (name.Declaration != null)
		if(table.lookupDec(name) == null){
			String message = "When visitStatement_Out, " + name + " is not in symbolTable. ";
	    	throw new SemanticException(statement_Out.firstToken, message);
		}
		//Statement_Out.Declaration <= name.Declaration
		statement_Out.setDec(table.lookupDec(name));
		
		//REQUIRE:((name.Type == INTEGER || name.Type == BOOLEAN) && Sink.Type == SCREEN) ||  (name.Type == IMAGE && (Sink.Type ==FILE || Sink.Type == SCREEN))
		Type name_type = table.lookupType(name);
		if(((name_type.equals(Type.INTEGER) || name_type.equals(Type.BOOLEAN))
				&& Type.SCREEN.equals(sink.Type))
				|| (name_type.equals(Type.IMAGE) && (Type.FILE.equals(sink.Type) || Type.SCREEN.equals(sink.Type)))){
			return statement_Out.getDec();
		}
		String message = "When visitStatement_Out, " + name + "'s type is "
				+ name_type + ", sink.Type is " + sink.Type;
    	throw new SemanticException(statement_Out.firstToken, message);
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		//system.out.println("in visitStatement_In...");

		String name = statement_In.name;
		//lookup
		if(table.lookupDec(name) == null){
			String message = "When visitStatement_In, " + name + " is not in SymbolTable.";
	    		throw new SemanticException(statement_In.firstToken, message);
		}
//		Type name_type = table.lookupType(name);
		//source cannot be null
		if(statement_In.source == null){
			String message = "When visitStatement_In, statement_In.source is " + statement_In.source;
			throw new SemanticException(statement_In.firstToken, message);
		}
		//source visit
		statement_In.source.visit(this, null);
//		Type source_type = statement_In.source.Type;
		//delete the following requirement in HW5
//		if(!name_type.equals(source_type)){
//			String message = "When visitStatement_In, " + name_type + " !=  "
//					+ source_type;
//	    	throw new SemanticException(statement_In.firstToken, message);
//		}
		statement_In.setDec(table.lookupDec(name));
		return statement_In.getDec();
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		//system.out.println("in visitStatement_Assign ...");
		//visit
		statement_Assign.lhs.visit(this, null);
		statement_Assign.e.visit(this, null);
		//Type
		Type type0 = statement_Assign.lhs.Type;
		Type type1 = statement_Assign.e.Type;
		if((type0.equals(Type.IMAGE) && type1.equals(Type.INTEGER)) || type0.equals(type1)){
			//The test for statement_Assign should be either the types are the same, 
			//or lhs is an image and the expression on the right is an integer.
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
			statement_Assign.setDec(table.lookupDec(statement_Assign.lhs.name));
			return null;
		}
		String message = "When visitStatement_Assign, " + type0 + " !=  "
				+ type1;
    		throw new SemanticException(statement_Assign.firstToken, message);
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//system.out.println("in visitLHS ...");
		String name = lhs.name;
		if(lhs.index != null){
			lhs.index.visit(this, null);
		}
		
		Index index = lhs.index;
//		System.out.println("name: " + name);
//		System.out.println("index: " + index);
//		System.out.println(table.lookupDec(name));
		if(table.lookupDec(name) != null){
			lhs.declaration = table.lookupDec(name);
			lhs.Type = lhs.declaration.Type;
			if(index != null){
				lhs.setCartesian(index.isCartesian());
			}
			lhs.setCartesian(false);
			return lhs.Type;
		}
		else{
			String message = "When visitLHS, " + name + " is not in SymbolTable. ";
	    	throw new SemanticException(lhs.firstToken, message);
		}
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		sink_SCREEN.Type = Type.SCREEN;
		return sink_SCREEN.Type;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		String name = sink_Ident.name;
		if(table.lookupDec(name) == null){
			String message = "When visitSink_Ident, " + name + " is not in SymbolTable.  ";
	    	throw new SemanticException(sink_Ident.firstToken, message);
		}
		sink_Ident.Type = table.lookupType(name);
		if(sink_Ident.Type.equals(Type.FILE)){
			return sink_Ident.Type;
		}
		String message = "When visitSink_Ident, " + sink_Ident.Type + " !=  "
				+ "FILE";
    	throw new SemanticException(sink_Ident.firstToken, message);
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		expression_BooleanLit.Type = Type.BOOLEAN;
		return expression_BooleanLit.Type;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		String name = expression_Ident.firstToken.getText();
		if(table.lookupDec(name) == null){
			String message = "When visitExpression_Ident, " + name + " is not in SymbolTable.  ";
	    	throw new SemanticException(expression_Ident.firstToken, message);
		}
		expression_Ident.Type = table.lookupType(name);
		return expression_Ident.Type;
	}

}
