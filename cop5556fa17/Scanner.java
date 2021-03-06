/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}
	public static enum State{
		START, AFTER_CR/* \r */, IN_STRING, IN_DIGIT, IN_IDENTIFIER, IN_COMMENTS, 
		AFTER_DIV, AFTER_MINUS, AFTER_TIMES, AFTER_ASSIGN, AFTER_GT, AFTER_LT, AFTER_EXCL;
	}
	public static enum Kind {
		IDENTIFIER(""), INTEGER_LITERAL(""), BOOLEAN_LITERAL(""), STRING_LITERAL(""), 
		KW_x("x")/* x */, KW_X("X")/* X */, KW_y("y")/* y */, KW_Y("Y")/* Y */, KW_r("r")/* r */, KW_R("R")/* R */, KW_a("a")/* a */, 
		KW_A("A")/* A */, KW_Z("Z")/* Z */, KW_DEF_X("DEF_X")/* DEF_X */, KW_DEF_Y("DEF_Y")/* DEF_Y */, KW_SCREEN("SCREEN")/* SCREEN */, 
		KW_cart_x("cart_x")/* cart_x */, KW_cart_y("cart_y")/* cart_y */, KW_polar_a("polar_a")/* polar_a */, KW_polar_r("polar_r")/* polar_r */, 
		KW_abs("abs")/* abs */, KW_sin("sin")/* sin */, KW_cos("cos")/* cos */, KW_atan("atan")/* atan */, KW_log("log")/* log */, 
		KW_image("image")/* image */,  KW_int("int")/* int */, 
		KW_boolean("boolean")/* boolean */, KW_url("url")/* url */, KW_file("file")/* file */, 
		OP_ASSIGN("=")/* = */, OP_GT(">")/* > */, OP_LT("<")/* < */, 
		OP_EXCL("!")/* ! */, OP_Q("?")/* ? */, OP_COLON(":")/* : */, OP_EQ("==")/* == */, OP_NEQ("!=")/* != */, OP_GE(">=")/* >= */, OP_LE("<=")/* <= */, 
		OP_AND("&")/* & */, OP_OR("|")/* | */, OP_PLUS("+")/* + */, OP_MINUS("-")/* - */, OP_TIMES("*")/* * */, OP_DIV("/")/* / */, OP_MOD("%")/* % */, 
		OP_POWER("**")/* ** */, OP_AT("@")/* @ */, OP_RARROW("->")/* -> */, OP_LARROW("<-")/* <- */, LPAREN("(")/* ( */, RPAREN(")")/* ) */, 
		LSQUARE("[")/* [ */, RSQUARE("]")/* ] */, SEMI(";")/* ; */, COMMA(",")/* , */, EOF("");


		final String text;
		
		Kind(String text) {
			this.text = text;
		}

		String getText() {
			return text;
		}
	}

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape, first \ is escape character, if write '\', error
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}

	public static HashMap<String, Kind> keyword_map = new HashMap<String, Kind>();
    static{
        	for(Kind k: Kind.values()){
	    	String str = k.getText();
	    	if(str.matches("^[a-zA-Z_]+$")){
	    		keyword_map.put(str, k);
	    	}
    }
    }
    
    
	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;//index of array chars[]
		int line = 1;
		int posInLine = 1;
		//System.out.println("chars.length = " + chars.length);
		State state = State.START;
		int start_pos = 0;
		while(pos < chars.length){
			char ch = chars[pos];
			
			switch(state){
			case START: {
				start_pos = pos;
				switch(ch){
				case ';': tokens.add(new Token(Kind.SEMI, pos ++, 1, line, posInLine ++)); break;
				case ',': tokens.add(new Token(Kind.COMMA, pos ++, 1, line, posInLine ++)); break;
				case '+': tokens.add(new Token(Kind.OP_PLUS, pos ++, 1, line, posInLine ++)); break;
				case '%': tokens.add(new Token(Kind.OP_MOD, pos ++, 1, line, posInLine ++)); break;
				case '@': tokens.add(new Token(Kind.OP_AT, pos ++, 1, line, posInLine ++)); break;
				case ':': tokens.add(new Token(Kind.OP_COLON, pos ++, 1, line, posInLine ++)); break;
				case '?': tokens.add(new Token(Kind.OP_Q, pos ++, 1, line, posInLine ++)); break;
				case '&': tokens.add(new Token(Kind.OP_AND, pos ++, 1, line, posInLine ++)); break;
				case '|': tokens.add(new Token(Kind.OP_OR, pos ++, 1, line, posInLine ++)); break;
				case '(': tokens.add(new Token(Kind.LPAREN, pos ++, 1, line, posInLine ++)); break;
				case ')': tokens.add(new Token(Kind.RPAREN, pos ++, 1, line, posInLine ++)); break;
				case '[': tokens.add(new Token(Kind.LSQUARE, pos ++, 1, line, posInLine ++)); break;
				case ']': tokens.add(new Token(Kind.RSQUARE, pos ++, 1, line, posInLine ++)); break;

				case '/': {pos ++; posInLine ++; state = State.AFTER_DIV; break;}
				case '-': {pos ++; posInLine ++; state = State.AFTER_MINUS; break;}
				case '*': {pos ++; posInLine ++; state = State.AFTER_TIMES; break;}	
				case '=': {pos ++; posInLine ++; state = State.AFTER_ASSIGN; break;}	
				case '>': {pos ++; posInLine ++; state = State.AFTER_GT; break;}	
				case '<': {pos ++; posInLine ++; state = State.AFTER_LT; break;}
				case '!': {pos ++; posInLine ++; state = State.AFTER_EXCL; break;}
				case '\r': {pos ++; posInLine ++; state = State.AFTER_CR; break;}
				case '\n': {pos ++; line ++; posInLine = 1; break;}
				case '\"': {pos ++; state = State.IN_STRING; break;}
				case '0': {tokens.add(new Token(Kind.INTEGER_LITERAL,  pos ++, 1, line, posInLine ++));}break;
				case EOFchar: {pos++; break;}// next iteration should terminate loop
				default: {
					if(Character.isDigit(ch)){
						state = State.IN_DIGIT;
						pos ++;
					}
					else if(Character.isJavaIdentifierStart(ch)){
						state = State.IN_IDENTIFIER;
						pos ++;
					}
					else if(Character.isWhitespace(ch)){
						pos ++;
						posInLine ++;
					}
					else{
						throw new LexicalException("Invalid character " + ch + "at pos " + pos, pos);
					}
				}
				}
				break;
			}
			case IN_IDENTIFIER: {
				if(pos == chars.length - 1 || !Character.isJavaIdentifierPart(ch)){//EOF
					Token temp_token =new Token(Kind.IDENTIFIER, start_pos, pos - start_pos, line, posInLine);
					String temp_str = temp_token.getText();
					//e.g. string "X"
					if(keyword_map.containsKey(temp_str)){//is keyword
						Kind temp_kind = keyword_map.get(temp_str);//e.g. Kind KW_X
						tokens.add(new Token(temp_kind, start_pos, pos - start_pos, line, posInLine));
						state = State.START;
					}
					else if(temp_str.equals("true") || temp_str.equals("false")){
						tokens.add(new Token(Kind.BOOLEAN_LITERAL, start_pos, pos - start_pos, line, posInLine));
                        state = State.START;
					}
					else{//is identifier
						tokens.add(new Token(Kind.IDENTIFIER, start_pos, pos - start_pos, line, posInLine));
                        state = State.START;
					}
					posInLine += pos - start_pos;
				}
				else{
					pos ++;
					//posInLine ++;
				}
			} break;
			case IN_STRING: {
				if(pos == chars.length - 1){
					throw new LexicalException("Invalid STRING_LITERAL: expecting '\"' but nothing", pos);
				}
				else if(ch == '\n' || ch == '\r'){// error if  \n or \r
					throw new LexicalException("Invalid STRING_LITERAL", pos);  
				}
				else if(ch == '\\'){//look at next character
					char next_ch = chars[++ pos];
					if(next_ch == 'b' || next_ch == 't' || next_ch == 'n' || next_ch == 'f' || next_ch == 'r'
							|| next_ch == '"' || next_ch == '\'' || next_ch == '\\'){//ok
						pos ++;
					}
					else{//error
						throw new LexicalException("Invalid STRING_LITERAL", pos); 
					}
				}
				else if(ch != '\"'){
					//System.out.println(ch);
					pos ++;
				}
				else{
					Token temp = new Token(Kind.STRING_LITERAL, start_pos, pos - start_pos + 1, line, posInLine);
					try{
						temp.getText();
						tokens.add(temp);
						state = State.START;
						posInLine += pos - start_pos + 1;
						pos ++;
	                 }catch(Exception NumberFormatException){
	            		throw new LexicalException("Invalid STRING_LITERAL", pos);  
	            	 } 
	            }
			} break;
			case IN_DIGIT: {
				if(Character.isDigit(ch)){
                    pos ++;
                } 
				else{
					Token temp = new Token(Kind.INTEGER_LITERAL, start_pos, pos - start_pos, line, posInLine);
					try{
						temp.intVal();
						tokens.add(temp);
						posInLine += pos - start_pos;
						state = State.START;           
	                 }catch(Exception NumberFormatException){
	            		throw new LexicalException("Invalid INTEGER_LITERAL: out of the range of INTEGER !", start_pos);  
	            	 } 
                 }
			} break;
			case AFTER_DIV: {
				if(ch != '/'){//division
					tokens.add(new Token(Kind.OP_DIV, pos - 1, 1, line, posInLine - 1));
					state = State.START;
				}
				else{//comments
					pos ++;
					posInLine ++;
					state = State.IN_COMMENTS;
				}
				break;
			}
			case IN_COMMENTS: {
				if(ch == '\n'){
					state = State.START;
					pos ++;
					line ++;
					posInLine = 1;
				}
				else if(ch == '\r'){
					state = State.AFTER_CR;
				}
				else{
					pos ++;
					posInLine ++;
				}
				break;
			}
           
           
			case AFTER_CR: {
				state = State.START;
				switch(ch){
				case '\n': {pos ++; line ++; posInLine = 1;} break;
				default: {line ++; posInLine = 1;} break;//stay at this pos
				}
				break;
			} 
			case AFTER_TIMES: {
				state = State.START;
				switch(ch){
				case '*': {tokens.add(new Token(Kind.OP_POWER, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_TIMES, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			case AFTER_ASSIGN: {
				state = State.START;
				switch(ch){
				case '=': {tokens.add(new Token(Kind.OP_EQ, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_ASSIGN, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			case AFTER_GT: {
				state = State.START;
				switch(ch){
				case '=': {tokens.add(new Token(Kind.OP_GE, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_GT, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			case AFTER_LT: {
				state = State.START;
				switch(ch){
				case '=': {tokens.add(new Token(Kind.OP_LE, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				case '-': {tokens.add(new Token(Kind.OP_LARROW, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_LT, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			case AFTER_MINUS: {
				state = State.START;
				switch(ch){
				case '>': {tokens.add(new Token(Kind.OP_RARROW, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_MINUS, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			case AFTER_EXCL: {
				state = State.START;
				switch(ch){
				case '=': {tokens.add(new Token(Kind.OP_NEQ, pos - 1, 2, line, posInLine - 1)); pos ++; posInLine ++;} break;
				default: {tokens.add(new Token(Kind.OP_EXCL, pos - 1, 1, line, posInLine - 1));} break;
				}
				break;
			}
			default:  assert false;
			}
		}
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;

	}

	
	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
