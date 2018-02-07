package cop5556fa17;



import java.util.ArrayList;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

import cop5556fa17.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * return Program object
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
//		program();
//		matchEOF();
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		Program e0 = null;
		Token first = t;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		Kind kind = t.kind;
		Token name = match(IDENTIFIER);
		kind = t.kind;
		while(kind.equals(KW_int) || kind.equals(KW_boolean) || kind.equals(KW_image) || kind.equals(KW_url) || kind.equals(KW_file) || kind.equals(IDENTIFIER)){
			if(kind.equals(KW_int) || kind.equals(KW_boolean) || kind.equals(KW_image) || kind.equals(KW_url) || kind.equals(KW_file)){//Declaration
				Declaration e1 = Declaration();
				decsAndStatements.add(e1);
				match(SEMI);
				kind = t.kind;
			}
			else if(kind.equals(IDENTIFIER)){//Statement
				Statement e1 = Statement();
				decsAndStatements.add(e1);
				match(SEMI);
				kind = t.kind;
			}
		}
		e0 = new Program(first, name, decsAndStatements);
		return e0;
	}
	
	Statement Statement() throws SyntaxException{
		Statement e0 = null;
		Token first = t;
		Token name = match(IDENTIFIER);
		//System.out.println(name);
		Kind kind = t.kind;
		if(kind.equals(OP_RARROW)){//ImageOutStatement
			consume();
			Sink sink = Sink();
			e0 = new Statement_Out(first, name, sink);
		}
		else if(kind.equals(OP_LARROW)){//ImageInStatement
			consume();
			Source source = Source();
			e0 = new Statement_In(first, name, source);
		}
		else if(kind.equals(LSQUARE)){//lhs
			consume();
			Index index= LhsSelector();
			LHS lhs = new LHS(first, name, index);
			match(RSQUARE);
			match(OP_ASSIGN);
			Expression e1 = expression();
			e0 = new Statement_Assign(first, lhs, e1);
		}
		else if(kind.equals(OP_ASSIGN)){//lhs
			consume();
			Expression e1 = expression();
			LHS lhs = new LHS(first, name, null);
			e0 = new Statement_Assign(first, lhs, e1);
		}
		else{
			String message = "Expected OP_RARROW or OP_LARROW or LSQUARE or OP_ASSIGN but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e0;
	}
	
	//LhsSelector ::= [XySelector | RaSelector]
	Index LhsSelector() throws SyntaxException{
		Index e = null;
		match(LSQUARE);
		Kind kind = t.kind;
		if(kind.equals(KW_x)){
			e = XySelector();
			match(RSQUARE);
		}
		else if(kind.equals(KW_r)){
			e = RaSelector();
			match(RSQUARE);
		}
		else{
			String message = "Expected KW_x or KW_r but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}
	
	//XySelector ::= KW_x COMMA KW_y
	Index XySelector() throws SyntaxException{
		Token first = t;
		Index e0 = null;
		Expression e1 = null;// not null
		Kind kind = t.kind;
		if(kind.equals(KW_x)){
			e1 = new Expression_PredefinedName(t, kind);
			consume();
		}
		match(COMMA);
		Expression e2 = expression();	
		//match(KW_y);
		if(kind.equals(KW_y)){
			e2 = new Expression_PredefinedName(t, kind);
			consume();
		}
		e0 = new Index(first, e1, e2);
		return e0;
	}
	//RaSelector ::= KW_r COMMA KW_A
	Index RaSelector() throws SyntaxException{
		Index e0 = null;
		Token first = t;
		Expression e1 = null;// not null
		Kind kind = t.kind;
		if(kind.equals(KW_r)){
			e1 = new Expression_PredefinedName(t, kind);
			consume();
		}
		match(COMMA);
		Expression e2 = expression();	
		//match(KW_A);
		if(kind.equals(KW_A)){
			e2 = new Expression_PredefinedName(t, kind);
			consume();
		}
		e0 = new Index(first, e1, e2);
		return e0;
	}
	
	Sink Sink() throws SyntaxException{
		Sink e = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(IDENTIFIER)){//IDENT must be file, in the future
			Token name = consume();
			e = new Sink_Ident(first, name);
		}
		else if(kind.equals(KW_SCREEN)){
			consume();
			e = new Sink_SCREEN(first);
		}
		else{
			String message = "Expected IDENTIFIER or KW_SCREEN but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}

	Declaration Declaration() throws SyntaxException{
		Declaration e = null;
		Kind kind = t.kind;
		if(kind.equals(KW_int) || kind.equals(KW_boolean)){//VariableDeclaration
			e = VariableDeclaration();
		}
		else if(kind.equals(KW_image)){//ImageDeclaration
			e = ImageDeclaration();
		}
		else if(kind.equals(KW_url) || kind.equals(KW_file)){//SourceSinkDeclaration
			e = SourceSinkDeclaration();
		}
		else{
			String message = "Expected KW_int or KW_boolean or KW_image or KW_url or KW_file or but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}
	
	Declaration_Variable VariableDeclaration() throws SyntaxException{
		Declaration_Variable e0 = null;
		Expression e1 = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(KW_int) || kind.equals(KW_boolean)){
			Token type = t;
			consume();
			Token name = t;
			match(IDENTIFIER);
			kind = t.kind;
			if(kind.equals(OP_ASSIGN)){
				consume();
				e1 = expression();
			}
			e0 = new Declaration_Variable(first, type, name, e1);
		}
		else{
			String message = "Expected KW_int or KW_boolean but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e0;
	}
	
	Declaration_Image ImageDeclaration() throws SyntaxException {
		Declaration_Image e0 = null;
		Expression xSize = null;
		Expression ySize = null;
		Source source = null;
		Token first = t;
		match(KW_image);
		Kind kind = t.kind;
		if(kind.equals(LSQUARE)){
			consume();
			xSize = expression();
			match(COMMA);
			ySize = expression();
			match(RSQUARE);
		}
		Token name = t;
		match(IDENTIFIER);
		kind = t.kind;
		if(kind.equals(OP_LARROW)){
			consume();
			source = Source();
		}
		e0 = new Declaration_Image(first, xSize, ySize, name, source);
		return e0;
	}
	
	Declaration_SourceSink SourceSinkDeclaration() throws SyntaxException{
		Declaration_SourceSink e0 = null;
		Token type = null;
		Token name = null;
		Source source = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(KW_url) || kind.equals(KW_file)){
			type = consume();
			name = match(IDENTIFIER);//return Token
			match(OP_ASSIGN);
			source = Source();
		}
		else{
			String message = "Expected KW_url or KW_file but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		e0 = new Declaration_SourceSink(first, type, name, source);
		return e0;
	}
	
	Source Source() throws SyntaxException {
		Source e0 = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(STRING_LITERAL)){
			Token url = consume();
			e0 = new Source_StringLiteral(first, url.getText());
		}
		else if(kind.equals(IDENTIFIER)){
			Token name = consume();
			e0 = new Source_Ident(first, name);
		}
		else if(kind.equals(OP_AT)){
			consume();
			Expression e1 = expression();
			e0 = new Source_CommandLineParam(first, e1);
		}
		else{
			String message = "Expected STRING_LITERAL or IDENTIFIER or OP_AT but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e0;
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * return Expression object
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		Expression ex = null;
		Token first = t;
		Expression or = OrExpression();//condition
		Kind kind = t.kind;
		if(kind.equals(OP_Q)){// "?"
			consume();
			Expression e1 = expression();
			match(OP_COLON);// ":"
			Expression e2 = expression();
			ex = new Expression_Conditional(first, or, e1, e2);
		}
		else{
			ex = or;
		}
		return ex;
	}

	Expression OrExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = AndExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_OR)){
			Token op = t;
			consume();
			e1 = AndExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression AndExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = EqExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_AND)){
			Token op = t;
			consume();
			e1 = EqExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression EqExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = RelExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_EQ) || kind.equals(OP_NEQ)){
			Token op = t;
			consume();
			e1 = RelExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression RelExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = AddExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_LT) || kind.equals(OP_GT) || kind.equals(OP_LE) || kind.equals(OP_GE)){
			Token op = t;
			consume();
			e1 = AddExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression AddExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = MultExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_PLUS) || kind.equals(OP_MINUS)){
			Token op = t;
			consume();
			e1 = MultExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression MultExpression() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token first = t;
		e0 = UnaryExpression();
		Kind kind = t.kind;
		while(kind.equals(OP_TIMES) || kind.equals(OP_DIV) || kind.equals(OP_MOD)){
			Token op = t;
			consume();
			e1 = UnaryExpression();
			e0 = new Expression_Binary(first, e0, op, e1);
			kind = t.kind;
		}
		return e0;
	}
	
	Expression UnaryExpression() throws SyntaxException{
		Expression e = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(OP_PLUS) || kind.equals(OP_MINUS)){
			Token op = t;
			consume(); 
			e = UnaryExpression();
			e = new Expression_Unary(first, op, e);
		}
		else{
			e = UnaryExpressionNotPlusMinus();
		}
		return e;
	}
	
	Expression UnaryExpressionNotPlusMinus() throws SyntaxException{
		Expression e = null;
		Token first = t;
		Kind kind = t.kind;
		if(kind.equals(OP_EXCL)){
			Token op = t;
			consume(); 
			e = UnaryExpression(); 
			e = new Expression_Unary(first, op, e);
		}
		else if(kind.equals(KW_x) || kind.equals(KW_y) || kind.equals(KW_r) || kind.equals(KW_a) 
				|| kind.equals(KW_X) || kind.equals(KW_Y) || kind.equals(KW_Z) 
				|| kind.equals(KW_A) || kind.equals(KW_R) || kind.equals(KW_DEF_X) 
				|| kind.equals(KW_DEF_Y)){
			e = new Expression_PredefinedName(first, kind);
			consume();
		}
		else if(kind.equals(IDENTIFIER)){
			//IdentOrPixelSelectorExpression
			e = IdentOrPixelSelectorExpression();
		}
		else if(kind.equals(INTEGER_LITERAL) || kind.equals(LPAREN) || kind.equals(KW_sin)
				|| kind.equals(KW_cos) || kind.equals(KW_atan) || kind.equals(KW_abs) || kind.equals(KW_log)
				|| kind.equals(KW_cart_x) || kind.equals(KW_cart_y) || kind.equals(KW_polar_a)
				|| kind.equals(KW_polar_r) || kind.equals(BOOLEAN_LITERAL)){
			//Primary
			e = Primary();
		}		
		else{
			String message = "Unexpected " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}
	
	
	Expression IdentOrPixelSelectorExpression() throws SyntaxException {
		Expression e = null;
		Token first = t;
		Kind kind = t.kind;
		if(!kind.equals(IDENTIFIER)){//not identifier => error
			String message = "Expected IDENTIFIER but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		Token ident = consume();//identifier
		kind = t.kind;
		if(kind.equals(LSQUARE)){
			consume();
			Index index = Selector();
			kind = t.kind;
			match(RSQUARE);
			e = new Expression_PixelSelector(first, ident, index);//name is ident?
		}
		else{
			e = new Expression_Ident(first, ident);
		}
		return e;
	}
	
	Index Selector() throws SyntaxException{
		Index e = null;
		Token first = t;
		Expression e0 = expression();
		match(COMMA);
		Expression e1 = expression();
		e = new Index(first, e0, e1);
		return e;
	}
	
	Expression Primary() throws SyntaxException {
		Expression e = null;
		Kind kind = t.kind;
		if(kind.equals(INTEGER_LITERAL)){
			e = new Expression_IntLit(t, Integer.parseInt(t.getText()));//String Token.getTest(); int val
			consume();
		}
		else if(kind.equals(BOOLEAN_LITERAL)){
			if(t.getText().equals("true")){
				e = new Expression_BooleanLit(t, true);//boolean val
			}
			else{
				e = new Expression_BooleanLit(t, false);//boolean val
			}
			consume();
		}
		else if(kind.equals(LPAREN)){
			consume();
			kind = t.kind;
			e = expression();
			kind = t.kind;
			if(kind.equals(RPAREN)){
				consume();
			}
			else{
				String message = "Expected RPAREN but " + kind + " at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		}
		else{
			e = FunctionApplication();
		}
		return e;
	}
	
	Expression_FunctionApp FunctionApplication() throws SyntaxException{
		Expression_FunctionApp e = null;
		Kind kind = t.kind;
		Kind function = t.kind;
		if(kind.equals(KW_sin) || kind.equals(KW_cos) || kind.equals(KW_atan) || kind.equals(KW_abs) || kind.equals(KW_log) 
				|| kind.equals(KW_cart_x) || kind.equals(KW_cart_y) || kind.equals(KW_polar_a) || kind.equals(KW_polar_r)){//FunctionName
			consume();
			kind = t.kind;
			if(kind.equals(LPAREN)){
				consume();
				Expression arg = expression();
				match(RPAREN);
				e = new Expression_FunctionAppWithExprArg(t, function, arg);
			}
			else if(kind.equals(LSQUARE)){
				consume();
				Index arg = Selector();
				match(RSQUARE);
				e = new Expression_FunctionAppWithIndexArg(t, function, arg);
			}
			else{
				String message = "Expected LPAREN or LSQUARE but " + kind + " at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		}
		else{
			String message = "Expected FunctionName but " + kind + " at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return e;
	}
	
	private Token consume(){
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}
	
	private Token match(Kind kind) throws SyntaxException {
		  Kind tkind = t.kind;
		  if (tkind.equals(kind)) {
		   return consume();
		  }
		  String message = t.kind + " but expected " + kind + " at " + t.line + "line" + t.pos_in_line + "pos_in_line";
		  throw new SyntaxException(t, message);
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
