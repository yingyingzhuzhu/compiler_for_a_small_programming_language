package cop5556fa17;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.AST.Statement_Assign;


public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	//int slotNumber = 1;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		  cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		  className = program.name;  
		  classDesc = "L" + className + ";";
		  
		  String sourceFileName = (String) arg;
		  cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		  cw.visitSource(sourceFileName, null);
		  // create main method
		  mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		  // initialize
		  //DEF_X
		  mv.visitLdcInsn(new Integer(256));
		  mv.visitVarInsn(ISTORE, 9);
		  //DEF_Y
		  mv.visitLdcInsn(new Integer(256));
		  mv.visitVarInsn(ISTORE, 10);
		  //Z
		  mv.visitLdcInsn(new Integer(16777215));
		  mv.visitVarInsn(ISTORE, 11);
		  
		  mv.visitCode(); 
		  //add label before first instruction
		  Label mainStart = new Label();
		  mv.visitLabel(mainStart); 
		  
		  // if GRADE, generates code to add string to log
	
		  // visit decs and statements to add field to class
		  //  and instructions to main method, respectivley
		  ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		  for (ASTNode node : decsAndStatements) {
		   node.visit(this, arg);
		  }
	
		  //generates code to add string to log
		  
		  //adds the required (by the JVM) return statement to main
		  mv.visitInsn(RETURN);
		  
		  //adds label at end of code
		  Label mainEnd = new Label();
		  mv.visitLabel(mainEnd);
		  
		  //handles parameters and local variables of main. Right now, only args
		  mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		  //x: the locations in the x direction, used as a loop index in assignment statements involving images
		  mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		  //y: the location  in  the  y  direction, used   as  a  loop  index in assignment statements involving images
		  mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		  //X: the   upper  bound   on   the   value   of   loop   index x.  It is   also   the   width   of   the   image.   Obtain   by   invoking the   ImageSupport.getX   method
		  mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		  //Y: the   upper  bound   on   the   value   of   the   loop   index   y.      It   is   also   the   height   of   the   image.      Obtain   by invoking   the   ImageSupport.getY   method.
		  mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		  //r: the   radius   in   the   polar   representation   of   cartesian   location   x   and   y.      Obtain   from   x   and   y   with RuntimeFunctions.polar_r.
		  mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		  //a: the   angle, in   degrees,   in   the   polar   representation   of   cartesian   location   x   and   y.      Obtain   from   x   and   y with   RuntimeFunctions.polar_a.
		  mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 6);
		  //R: the   upper  bound   on   r,   obtain   from   polar_r(X,Y) 
		  mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 7);
		  //A: the   upper   bound   on   a,   obtain   from   polar_a(0,Y)
		  mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);
		  
		  //The final  three   are   fixed   constants.      You   can   handle   this   by   defining   and   initializing   a   variable,   or   just   by letting   visitExpression_PredefinedName   load   the   constant   value.
		  //DEF_X: the default   image   width. For   simplicity,   let   this   be   256. 
		  mv.visitLocalVariable("DEF_X", "I", null, mainStart, mainEnd, 9);
		  
		  //DEF_Y: the   default   image   height. For simplicity, let this be 256. 
		  mv.visitLocalVariable("DEF_Y", "I", null, mainStart, mainEnd, 10);

		  //Z: the   value   of   a   white   pixel. This is 0xFFFFFF or 16777215.
		  mv.visitLocalVariable("Z", "I", null, mainStart, mainEnd, 11);
		  
		  //Sets max stack size and number of local vars.
		  //Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		  //asm will calculate this itself and the parameters are ignored.
		  //If you have trouble with failures in this routine, it may be useful
		  //to temporarily set the parameter in the ClassWriter constructor to 0.
		  //The generated classfile will not be correct, but you will at least be
		  //able to see what is in it.
		  mv.visitMaxs(0, 0);
		  
		  //terminate construction of main method
		  mv.visitEnd();
		  
		  //terminate class construction
		  cw.visitEnd();
	
		  //generate classfile as byte array and r
		  return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		FieldVisitor fv;
		String fieldName = declaration_Variable.name;
		String fieldType;
		Object initValue;		
		if(declaration_Variable.type.getText().equals(Kind.KW_int.getText())){
			fieldType = "I";
			initValue = new Integer(0);
		}
		else{
			fieldType = "Z";
			initValue = new Boolean(false);
		}
		
		fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);//field visitor
		fv.visitEnd();
		// has Expression
		if(declaration_Variable.e != null){
			declaration_Variable.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, fieldType);
		}
		
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		expression_Binary.e0.visit(this,arg);
		expression_Binary.e1.visit(this,arg);
		Label falseLabel = new Label();
		Label trueLabel = new Label();
		if(expression_Binary.kind.getText().equals(Kind.OP_EQ.getText())){
			// = 
			mv.visitJumpInsn(IF_ICMPNE, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_NEQ.getText())){
			// != 
			mv.visitJumpInsn(IF_ICMPEQ, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
		}
        else if(expression_Binary.kind.getText().equals(Kind.OP_GE.getText())){
        		// >= 
        		mv.visitJumpInsn(IF_ICMPLT, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);	
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
        }
        else if(expression_Binary.kind.getText().equals(Kind.OP_GT.getText())){
        		//  > 
        		mv.visitJumpInsn(IF_ICMPLE, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
        }
        else if(expression_Binary.kind.getText().equals(Kind.OP_LE.getText())){
        		// <=
        		mv.visitJumpInsn(IF_ICMPGT, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
        }
        else if(expression_Binary.kind.getText().equals(Kind.OP_LT.getText())){
        		//  <
        		mv.visitJumpInsn(IF_ICMPGE, falseLabel);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, trueLabel);
			mv.visitLabel(falseLabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(trueLabel);
        }
		else if(expression_Binary.kind.getText().equals(Kind.OP_AND.getText())){
			// & 
			mv.visitInsn(IAND);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_OR.getText())){
			// |  
			mv.visitInsn(IOR);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_PLUS.getText())){
			// +
			mv.visitInsn(IADD);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_MINUS.getText())){
			//-
			mv.visitInsn(ISUB);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_TIMES.getText())){
			// *
			mv.visitInsn(IMUL);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_DIV.getText())){
			// /
			mv.visitInsn(IDIV);
		}
		else if(expression_Binary.kind.getText().equals(Kind.OP_MOD.getText())){
			// %
			mv.visitInsn(IREM);
		}
//		else if(expression_Binary.kind.getText().equals(Kind.OP_POWER.getText())){
//			// ^
//			//mv.visitInsn();
//		}
		else{
			throw new Exception("Undefined kind of Operation : " + expression_Binary.kind.getText());
		}
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		expression_Unary.e.visit(this, arg);
		if(expression_Unary.kind.equals(Kind.OP_EXCL)) { // !
			if(expression_Unary.Type.equals(Type.BOOLEAN)){
				//BOOLEAN
				Label falseLabel = new Label();
				Label trueLabel = new Label();
				mv.visitInsn(ICONST_0); // compare with false
				mv.visitJumpInsn(IF_ICMPEQ, falseLabel); //false
				mv.visitInsn(ICONST_0);//change to false
				mv.visitJumpInsn(GOTO, trueLabel); //true
				mv.visitLabel(falseLabel);
				mv.visitInsn(ICONST_1); // change to true
				mv.visitLabel(trueLabel);
			}
			else{
				//INTEGER  8bits
				mv.visitLdcInsn(new Integer(Integer.MAX_VALUE));
				mv.visitInsn(IXOR);
			}
		}
		else if(expression_Unary.kind.equals(Kind.OP_MINUS)){//  - minus
			mv.visitInsn(INEG);
		}
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		if(!index.isCartesian()){ // r, a
			//r,a => x,y using cart_x, cart_y
//			index.e0.visit(this, arg); // get r
//			index.e1.visit(this, arg); // get a
//			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
//			index.e0.visit(this, arg); // get r
//			index.e1.visit(this, arg); // get a
//			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
		}
		else{// x, y
			index.e0.visit(this, arg);
			index.e1.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		//load image
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, ImageSupport.ImageDesc);
		// ? Visit   the   index   to   generate   code to leave   Cartesian   location   of   index   on   the   stack. 
		// ? get x, y
		expression_PixelSelector.index.visit(this, arg);
		//getPixel()
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		Label falseLabel = new Label();
		Label trueLabel = new Label();
		expression_Conditional.condition.visit(this, arg);
		mv.visitInsn(ICONST_0); // compare with
		mv.visitJumpInsn(IF_ICMPEQ, falseLabel); //false
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, trueLabel); //true	
		mv.visitLabel(falseLabel);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitLabel(trueLabel);
		
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		FieldVisitor fv;
		fv = cw.visitField (ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
		if(declaration_Image.source != null){
			// load the String containing the URL of file name onto the stack
			declaration_Image.source.visit(this, arg);
			//xSize, ySize
			if(declaration_Image.xSize == null || declaration_Image.ySize == null){
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else{
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
			}
			//read image
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			//mv.visitInsn(DUP);
			//static field
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
			
		}
		else{
			//make image
			if(declaration_Image.xSize == null || declaration_Image.ySize == null){
				mv.visitVarInsn(ILOAD, 9);
				mv.visitVarInsn(ILOAD, 10);
			}
			else{
				//System.out.println("source is null but size not null ... ");
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
			//mv.visitInsn(DUP);
			//static field
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
			
		}
		return null;
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0); //load args[] to stack top
		//Generate   code   to   evaluate   the   expression 
		source_CommandLineParam.paramNum.visit(this, arg);
		//use   aaload   to   read   the   element   from   the command   line   array 
		
		mv.visitInsn(AALOAD);
		//using   the   expression   value   as   the   index.      
		//The   command   line   array   is   the String[]   args   param   passed   to   main.
		
		
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc);
		return null;
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// URL or FILE
		FieldVisitor fv;
		fv = cw.visitField (ACC_STATIC, declaration_SourceSink.name, ImageSupport.StringDesc, null, null);
		fv.visitEnd();
		if(declaration_SourceSink.source != null){
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name, ImageSupport.StringDesc);
		}
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.kind.equals(Kind.KW_abs)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
		}
		else if(expression_FunctionAppWithExprArg.kind.equals(Kind.KW_log)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", RuntimeFunctions.logSig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.kind.equals(Kind.KW_cart_x)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
		}
		else if(expression_FunctionAppWithIndexArg.kind.equals(Kind.KW_cart_y)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		else if(expression_FunctionAppWithIndexArg.kind.equals(Kind.KW_polar_r)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		}
		else if(expression_FunctionAppWithIndexArg.kind.equals(Kind.KW_polar_a)){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		if(expression_PredefinedName.kind.equals(Kind.KW_x)){
			mv.visitVarInsn(ILOAD, 1);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_y)){
			mv.visitVarInsn(ILOAD, 2);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_X)){
			mv.visitVarInsn(ILOAD, 3);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_Y)){
			mv.visitVarInsn(ILOAD, 4);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_r)){
			mv.visitVarInsn(ILOAD, 1); // x
			mv.visitVarInsn(ILOAD, 2); // y
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
//			mv.visitVarInsn(ISTORE, 5); // r
//			mv.visitVarInsn(ILOAD, 5);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_a)){
			mv.visitVarInsn(ILOAD, 1); // x
			mv.visitVarInsn(ILOAD, 2); // y
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
//			mv.visitVarInsn(ISTORE, 6); // a
//			mv.visitVarInsn(ILOAD, 6);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_R)){
			mv.visitVarInsn(ILOAD, 3); // X
			mv.visitVarInsn(ILOAD, 4); // Y
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			mv.visitVarInsn(ISTORE, 7); // R
			mv.visitVarInsn(ILOAD, 7);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_A)){
			mv.visitInsn(ICONST_0); // 0
			mv.visitVarInsn(ILOAD, 4); // Y
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_rSig, false);
			mv.visitVarInsn(ISTORE, 8); // A
			mv.visitVarInsn(ILOAD, 8);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_DEF_X)){
			mv.visitVarInsn(ILOAD, 9);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_DEF_Y)){
			mv.visitVarInsn(ILOAD, 10);
		}
		else if(expression_PredefinedName.kind.equals(Kind.KW_Z)){
			mv.visitVarInsn(ILOAD, 11);
		}
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		if(statement_Out.getDec().Type.equals(Type.INTEGER)){
			//integer or boolean   =>   only   “sink”   is   the   screen
			//Use   java.io.PrintStream.println.  
			//This is a virtual method, you can use the static field PrintStream “out” from class java.lang.System as the object.
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "I");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		}
		else if(statement_Out.getDec().Type.equals(Type.BOOLEAN)){
			//integer or boolean   =>   only   “sink”   is   the   screen
			//Use   java.io.PrintStream.println.  
			//This is a virtual method, you can use the static field PrintStream “out” from class java.lang.System as the object.
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Z");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
		}
		else{
			//IF IMAGE, load the image and visit the sink.
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);
			mv.visitInsn(DUP);
			statement_Out.sink.visit(this, arg);
			//write Image reference to image log, leaving stack in original state
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
		}
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		if(statement_In.getDec().Type.equals(Type.INTEGER)){
			if(statement_In.source.Type == null){// @...
				//System.out.println("INTEGER");
				//integer => invoke Java.lang.Integer.parseInt.
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name, "I");
				statement_In.source.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
			}
		}
		else if(statement_In.getDec().Type.equals(Type.BOOLEAN)){
			if(statement_In.source.Type == null){// @...
				//System.out.println("BOOLEAN");
				//boolean =>  invoke   java/lang/Boolean.parseBoolean	
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name, "Z");
				statement_In.source.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
			}
		}
		else{
			//IMAGE
			mv.visitFieldInsn(GETSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
			// load the String containing the URL of file name onto the stack
			statement_In.source.visit(this, arg);
			//xSize, ySize
			Declaration_Image di = (Declaration_Image) statement_In.getDec();
			if(di.xSize == null || di.ySize == null){
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else{
				di.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				di.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			//read image
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			//static field
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);

		}
		return null;
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		if(statement_Assign.lhs.Type.equals(Type.BOOLEAN) || statement_Assign.lhs.Type.equals(Type.INTEGER)){
			//System.out.println("visitStatement_Assign...");
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);//neibu qu zhanding fuzhi
		}
		else{
			//Label L0 = new Label();
			//Label L1 = new Label();
			Label L2 = new Label();
			Label L3 = new Label();
			//Label L4 = new Label();
			Label L5 = new Label();
			Label L6 = new Label();
			//Label L7 = new Label();
			Label L8 = new Label();
			Label L9 = new Label();
			// IMAGE for loop
			// X
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitVarInsn(ISTORE, 3);
			// Y
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitVarInsn(ISTORE, 4);
			// x = 0
		    //mv.visitLabel(L0);
		    mv.visitInsn(ICONST_0); 
		    mv.visitVarInsn(ISTORE, 1); 
		    //mv.visitLabel(L1);
		    mv.visitJumpInsn(GOTO, L2);
		    // y = 0
		    mv.visitLabel(L3);
		    mv.visitInsn(ICONST_0); 
		    mv.visitVarInsn(ISTORE, 2); 
		    mv.visitJumpInsn(GOTO, L5);
		    mv.visitLabel(L6);
		    statement_Assign.e.visit(this, arg);
		    statement_Assign.lhs.visit(this, arg);
		    // y ++ 
		    mv.visitLabel(L8);
		    mv.visitIincInsn(2, 1); 
		    // y < Y
		    mv.visitLabel(L5);
		    mv.visitVarInsn(ILOAD, 2); // y
		    mv.visitVarInsn(ILOAD, 4); // Y
	    		mv.visitJumpInsn(IF_ICMPLT, L6);
	    		// x ++ 
	    		mv.visitLabel(L9);
	    		mv.visitIincInsn(1, 1); 
	    		// x < X
	    		mv.visitLabel(L2);
	    		mv.visitVarInsn(ILOAD, 1); // x
			mv.visitVarInsn(ILOAD, 3); // X
		    mv.visitJumpInsn(IF_ICMPLT, L3);
		}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if(lhs.Type.equals(Type.INTEGER)){
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		}
		else if(lhs.Type.equals(Type.BOOLEAN)){
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		}
		else{
			// IMAGE
			//load image reference
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			//load x,y on the top of stack
			//lhs.index.visit(this, arg);
			mv.visitVarInsn(ILOAD, 1); // x 
			mv.visitVarInsn(ILOAD, 2); // y
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
		}
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		mv.visitMethodInsn(INVOKESTATIC, ImageFrame.className, "makeFrame", "(Ljava/awt/image/BufferedImage;)"+ImageFrame.JFrameDesc, false);
		mv.visitInsn(POP); 
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//mv.visitLdcInsn(sink_Ident.name);
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, ImageSupport.StringDesc);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		if(expression_BooleanLit.value){
			mv.visitInsn(ICONST_1); //false
		}else{
		    mv.visitInsn(ICONST_0); // true
		}
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		if(expression_Ident.Type.equals(Type.INTEGER)){
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		}else if(expression_Ident.Type.equals(Type.BOOLEAN)){
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		}else if(expression_Ident.Type.equals(Type.IMAGE)){
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, ImageSupport.ImageDesc);
		}else{
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, ImageSupport.StringDesc);
		}
		return null;
	}

	

}
