package cop5556fa17.AST;

import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;

public abstract class Expression extends ASTNode {
	public TypeUtils.Type Type = TypeUtils.Type.NONE;
	public Kind kind = null;
	
	public Expression(Token firstToken) {
		super(firstToken);
	}
	
	//add in HW5
	public TypeUtils.Type getType(){
		return Type;
	}

}
