package cop5556fa17.AST;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;

public class Expression_Unary extends Expression {
	
	public final Expression e;

	public Expression_Unary(Token firstToken, Token op, Expression e) {
		super(firstToken);
		this.e = e;
		this.kind = op.kind;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpression_Unary(this,arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression_Unary other = (Expression_Unary) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Expression_Unary [op=");
		builder.append(kind);
		builder.append(", e=");
		builder.append(e);
		builder.append("]");
		return builder.toString();
	}
	
	

}
