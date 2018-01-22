/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = "<=^";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
//		show(scanner);
//		checkNext(scanner, SEMI, 0, 1, 1, 1);
//		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testGTLT() throws LexicalException {
		String input = "8<=9\n9<10\n2>=2\n3>1";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, OP_LE, 1, 2, 1, 2);
 		checkNext(scanner, INTEGER_LITERAL, 3, 1, 1, 4);
 		checkNext(scanner, INTEGER_LITERAL, 5, 1, 2, 1);
		checkNext(scanner, OP_LT, 6, 1, 2, 2);
 		checkNext(scanner, INTEGER_LITERAL, 7, 2, 2, 3);
 		checkNext(scanner, INTEGER_LITERAL, 10, 1, 3, 1);
		checkNext(scanner, OP_GE, 11, 2, 3, 2);
 		checkNext(scanner, INTEGER_LITERAL, 13, 1, 3, 4);
 		checkNext(scanner, INTEGER_LITERAL, 15, 1, 4, 1);
		checkNext(scanner, OP_GT, 16, 1, 4, 2);
 		checkNext(scanner, INTEGER_LITERAL, 17, 1, 4, 3);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testMinusRarrow() throws LexicalException {
		String input = "5-4\t5->6";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, OP_MINUS, 1, 1, 1, 2);
 		checkNext(scanner, INTEGER_LITERAL, 2, 1, 1, 3);
 		checkNext(scanner, INTEGER_LITERAL, 4, 1, 1, 5);
		checkNext(scanner, OP_RARROW, 5, 2, 1, 6);
 		checkNext(scanner, INTEGER_LITERAL, 7, 1, 1, 8);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testExclNeq() throws LexicalException {
		String input = "!true\t5!=6";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_EXCL, 0, 1, 1, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 1, 4, 1, 2);
 		checkNext(scanner, INTEGER_LITERAL, 6, 1, 1, 7);
		checkNext(scanner, OP_NEQ, 7, 2, 1, 8);
 		checkNext(scanner, INTEGER_LITERAL, 9, 1, 1, 10);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testTimesPower() throws LexicalException {
		String input = "8*9\n2**3";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, OP_TIMES, 1, 1, 1, 2);
 		checkNext(scanner, INTEGER_LITERAL, 2, 1, 1, 3);
 		checkNext(scanner, INTEGER_LITERAL, 4, 1, 2, 1);
		checkNext(scanner, OP_POWER, 5, 2, 2, 2);
 		checkNext(scanner, INTEGER_LITERAL, 7, 1, 2, 4);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testKeyword() throws LexicalException {
		String input = "DEF_X\r\n\"sin\" cart_y";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_DEF_X, 0, 5, 1, 1);
		checkNext(scanner, STRING_LITERAL, 7, 5, 2, 1);// left ", not first character
 		checkNext(scanner, KW_cart_y, 13, 6, 2, 7);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIdentifier() throws LexicalException {
		String input = "0DEF_X0$ $cart_y2017z";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, IDENTIFIER, 1, 7, 1, 2);
		checkNext(scanner, IDENTIFIER, 9, 12, 1, 10);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testBooleanLiteral() throws LexicalException {
		String input = "true $false false";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
		checkNext(scanner, IDENTIFIER, 5, 6, 1, 6);
		checkNext(scanner, BOOLEAN_LITERAL, 12, 5, 1, 13);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void failStringLiteral() throws LexicalException {
		String input = "\" greet\\ings\"";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {
			show(e);
			assertEquals(8,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void testStringLiteral() throws LexicalException {
		String input = "\" gree\\tings\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 13, 1, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testCommentDivision() throws LexicalException {
		String input = "0//comments\n6/3==2";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 12, 1, 2, 1);
		checkNext(scanner, OP_DIV, 13, 1, 2, 2);
		checkNext(scanner, INTEGER_LITERAL, 14, 1, 2, 3);
		checkNext(scanner, OP_EQ, 15, 2, 2, 4);
		checkNext(scanner, INTEGER_LITERAL, 17, 1, 2, 6);
		checkNextIsEOF(scanner);
	}
	
	@Test 
	public void testPosInLine() throws LexicalException{
		String input = "x=\"\b\";"; 
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_x, 0, 1, 1, 1);
		checkNext(scanner, OP_ASSIGN, 1, 1, 1, 2);
		checkNext(scanner, STRING_LITERAL, 2, 3, 1, 3);
		checkNext(scanner, SEMI, 5, 1, 1, 6);
		checkNextIsEOF(scanner);
		/*
		 * Tokens:
			[KW_x,x,0,1,1,1]
			[OP_ASSIGN,=,1,1,1,2]
			[STRING_LITERAL,"",2,3,1,3]
			[SEMI,;,5,1,1,6]
			[EOF,,6,0,1,7]
		 */
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	

	@Test
	public void testReturn() throws LexicalException{
		String input = "man\ngirl";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
		checkNext(scanner, IDENTIFIER, 4, 4, 2, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test 
	public void testWhiteSpace() throws LexicalException{
		String input = "\tABC";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
	}
	
	
}
