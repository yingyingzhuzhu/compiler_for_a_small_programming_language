package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils;

public abstract class Sink extends ASTNode {
	public TypeUtils.Type Type = TypeUtils.Type.NONE;
	
	public Sink(Token firstToken) {
		super(firstToken);
	}
	

}
