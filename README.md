# compiler_for_a_small_programming_language

## Implement a scanner for the programming language with the following lexical structure:

#### RawInputCharacter ::=  any ASCII character 

#### LineTerminator   ::=  LF  |  CR  |  CR LF

LF   is   the   ASCII   character   also   known   as   “newline”,   in   java   \n

CR   is   the   ASII   character   also   known   as   “return”,   in   Java,   the   char   \r CR   immediately   followed   by   LF   counts   as   one   line   terminator,   not   two

#### InputCharacter ::=  RawInputCharacter,   but   not   CR   or   LF 

#### Input   ::=   (WhiteSpace   |   Comment   |   Token)*

#### Token ::=   Identifier | Keyword | Literal | Separator | Operator 

#### WhiteSpace   ::=   SP   |  HT  |  FF  | LineTerminator

SP   is   the   ASCII   character   also   known   as   “space”

HT   is   the   ASCII   character   also   known   as   “horizontal   tab” 

FF   is   the   ASCII   character   known   also   known   as   “form   feed”

#### Comment  ::=  //InputCharacter*

#### Identifier  ::=  IdentifierChars   but   not   a   Keyword   or   BooleanLiteral 

#### IdentifierChars   ::=   IdentiferStart   IdentifierPart*

#### IdentifierStart  ::=    A .. Z    |    a .. z    |    _    |  $  

#### IdentifierPart   ::=   IdentifierStart  |  Digit

#### Literal  ::=  IntegerLiteral   |   BooleanLiteral   |  StringLiteral 

#### IntegerLiteral ::=  0 | NonZeroDigit  Digit*

#### NonZeroDigit  ::=   1  ..  9

#### Digit ::=  NonZeroDigit |  0

#### BooleanLiteral  ::=   true | false

#### StringLiteral   ::=   “ StringCharacter* “

#### StringCharacter ::=  InputCharacter  but  not  “  or  \|  EscapeSequence 

#### EscapeSequence   ::=  \b | \t | \n | \f | \r | \” | \’ | \\ 

#### Separators  ::=    (  |  )  |  [ | ]  |  ;  |  ,


#### Operators ::= = | > | < | ! | ? | : | == | != |  <=  |  >=  |  &  |  |  | + |  - |  *  |  / |  % |  **  | -> | <- | @

#### Keywords ::=  x | X | y | Y | r | R | a | A | Z | DEF_X | DEF_Y | SCREEN | cart_x |  cart_y  | polar_a | polar_r | abs | sin |  cos | atan | log | image | int | boolean | url | file



## Implement  a   recursive   descent   parser   for   the   following   context-free   grammar. The   parser   should   simply   determine   whether   the   given   sentence   is   legal   or   not.    If   not, the   parser   should   throw   a   SyntaxException.    If   the   sentence   is   legal,   the   parse   method should   simply   return.

#### Program   ::=    IDENTIFIER   (   Declaration   SEMI   |   Statement   SEMI   )*

#### Declaration   ::   =      VariableDeclaration       |     ImageDeclaration     |    SourceSinkDeclaration 

#### VariableDeclaration   ::=      VarType   IDENTIFIER    (    OP_ASSIGN      Expression    |   ε   )

#### VarType   ::=   KW_int   |   KW_boolean

#### SourceSinkDeclaration   ::=   SourceSinkType  IDENTIFIER   OP_ASSIGN    Source

#### Source   ::=   STRING_LITERAL

#### Source   ::=   OP_AT   Expression

#### Source   ::=   IDENTIFIER

#### SourceSinkType   :=   KW_url   |   KW_file

#### ImageDeclaration  ::=   KW_image    (LSQUARE   Expression   COMMA   Expression   RSQUARE   |   ε)                                                   IDENTIFIER  (  OP_LARROW  Source  |  ε  )

#### Statement  ::=   AssignmentStatement   |   ImageOutStatement   |   ImageInStatement

#### ImageOutStatement   ::=   IDENTIFIER   OP_RARROW   Sink

#### Sink   ::=   IDENTIFIER   |   KW_SCREEN    //ident   must   be   file

#### ImageInStatement   ::=   IDENTIFIER   OP_LARROW   Source

#### AssignmentStatement   ::=   Lhs   OP_ASSIGN   Expression

#### Expression   ::=      OrExpression    OP_Q    Expression   OP_COLON   Expression  |   OrExpression

#### OrExpression   ::=   AndExpression   (   OP_OR  AndExpression)*

#### AndExpression   ::=   EqExpression   (   OP_AND  EqExpression   )*

#### EqExpression   ::=   RelExpression   ( (OP_EQ  |  OP_NEQ ) RelExpression  )*

#### RelExpression   ::=   AddExpression   ( (OP_LT  |   OP_GT  |  OP_LE  | OP_GE  ) AddExpression)* 

#### AddExpression   ::=   MultExpression  ( (OP_PLUS   |   OP_MINUS )  MultExpression )* 

#### MultExpression   :=   UnaryExpression   ( ( OP_TIMES | OP_DIV | OP_MOD ) UnaryExpression )*

#### UnaryExpression   ::=   OP_PLUS   UnaryExpression  |   OP_MINUS   UnaryExpression  |   UnaryExpressionNotPlusMinus

#### UnaryExpressionNotPlusMinus   ::=   OP_EXCL  UnaryExpression |   Primary |  IdentOrPixelSelectorExpression   |   KW_x   |   
KW_y   |   KW_r   |   KW_a   |   KW_X   |   KW_Y   |   KW_Z   | KW_A   |   KW_R   |   KW_DEF_X   |   KW_DEF_Y

#### Primary  ::=   INTEGER_LITERAL   |   LPAREN   Expression   RPAREN   |   FunctionApplication   |    BOOLEAN_LITERAL 

#### IdentOrPixelSelectorExpression::=   IDENTIFIER  LSQUARE  Selector  RSQUARE  |  IDENTIFIER

#### Lhs::=  IDENTIFIER (  LSQUARE  LhsSelector  RSQUARE  |  ε  )

#### FunctionApplication  ::=   FunctionName   LPAREN   Expression   RPAREN  |   FunctionName      LSQUARE   Selector   RSQUARE

#### FunctionName   ::=   KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r

#### LhsSelector   ::=   LSQUARE ( XySelector | RaSelector ) RSQUARE 

#### XySelector   ::=   KW_x   COMMA   KW_y

#### RaSelector   ::=   KW_r   COMMA   KW_A

#### Selector   ::=   Expression   COMMA   Expression


## Implement an ASTVisitor to annotate and type check the abstract syntax tree generated by the parser. The following attribute grammar specifies the type system.

#### Program ::=  name  (Declaration  |  Statement)* Program.name  <=  name

#### Declaration  ::=  Declaration_Image   |   Declaration_SourceSink   |   Declaration_Variable 

#### Declaration_Image ::=   name   (  xSize   ySize | ε)  Source

REQUIRE:  symbolTable.lookupType(name)  =  symbolTable.insert(name, Declaration_Image)

Declaration_Image.Type  <=   IMAGE

REQUIRE  if   xSize   !=   ε   then   ySize   !=   ε   &&   xSize.Type   ==   INTEGER   &&   ySize.type   ==   INTEGER 

#### Declaration_SourceSink  ::=   Type name Source

REQUIRE:  symbolTable.lookupType(name)   =  null

symbolTable.insert(name,   Declaration_SourceSink )

Declaration_SourceSink.Type   <=   Type

REQUIRE  Source.Type  ==  Declaration_SourceSink.Type  | Declaration_SourceSink.Type   ==  null 

#### Declaration_Variable  ::=  Type name (Expression | ε)

REQUIRE: symbolTable.lookupType(name)   =  null

symbolTable.insert(name,    Declaration_Variable )

Declaration_Variable.Type   <=   Type

REQUIRE   if (Expression !=  ε)   Declaration_Variable.Type   ==   Expression.Type 

#### Statement  ::=   Statement_Assign   |   Statement_In   |   Statement_Out

#### Statement_Assign  ::=  LHS  Expression

REQUIRE:  LHS.Type   ==   Expression.Type

StatementAssign.isCartesian   <=   LHS.isCartesian 


#### Statement_In   ::=   name  Source

Statement_In.Declaration   <=   name.Declaration

REQUIRE:  (name.Declaration   !=   null)   &   (name.type   ==   Source.type) 



#### Statement_Out  ::=   name   Sink

Statement_ Out .Declaration   <=   name.Declaration

REQUIRE:  (name.Declaration   !=   null)

REQUIRE:  ((name.Type == INTEGER || name.Type == BOOLEAN) && Sink.Type ==  SCREEN) || (name.Type == IMAGE  && (Sink.Type  ==FILE || Sink.Type == SCREEN)) 

#### Expression   ::=   Expression_Binary   |   Expression_BooleanLit   |   Expression_Conditional  | Expression_FunctionApp  |  

Expression_FunctionAppWithExprArg  | Expression_FunctionAppWithIndexArg   |  Expression_Ident  |  Expression_IntLit  | 

Expression_PixelSelector  |  Expression_PredefinedName  _  Expression_Unary

Expression.Type  <=  Expression_X.Type 

#### Expression_Binary  ::=  Expression0    op   Expression1

REQUIRE:  Expression0.Type  ==  Expression1.Type &&  Expression_Binary.Type  ≠  null

Expression_Binary.type  <= 

if   op   ∈   {EQ,  NEQ} then BOOLEAN 

else   if   (op   ∈   {GE,   GT, LT,   LE}   &&   Expression0.Type   ==   INTEGER)  then   BOOLEAN 

else  if  (op  ∈  {AND,  OR}) && (Expression0.Type   ==   INTEGER   ||   Expression0.Type   ==BOOLEAN) then   Expression0.Type 

else   if   op   ∈   {DIV,   MINUS,   MOD,   PLUS,   POWER,   TIMES}   &&   Expression0.Type   == INTEGER  

else  null  

#### Expression_BooleanLit ::=  value Expression_BooleanLit.Type   <=   BOOLEAN

#### Expression_Conditional ::=  Expression condition  Expression true  Expression false

REQUIRE:  Expressioncondition.Type   ==   BOOLEAN   &&   Expressiontrue.Type  ==Expressionfalse.Type
          
Expression_Conditional.Type   <=   Expressiontrue.Type 

#### Expression_FunctionApp  ::=   Expression_FunctionAppWithExprArg  |  Expression_FunctionAppWithIndexArg 

Expression_FunctionApp.Type   <=   Expression_FunctionAppWithXArg.Type

#### Expression_FunctionAppWithExprArg   ::=   function   Expression

REQUIRE: Expression.Type   ==   INTEGER
         
Expression_FunctionAppWithExprArg.Type   <=   INTEGER 
         
#### Expression_FunctionAppWithIndexArg  ::=  function   Index

Expression_FunctionAppWithIndexArg.Type  <=  INTEGER

#### Expression_Ident   ::=  name

Expression_Ident.Type   <=   symbolTable.lookupType(name)

#### Expression_IntLit  ::=  value 

Expression_IntLIt.Type   <=   INTEGER

#### Expression_PixelSelector ::=  name Index

name.Type   <=   SymbolTable.lookupType(name)

Expression_PixelSelector.Type   <=      

if   name.Type   ==   IMAGE   then   INTEGER                                                                         

else  if  Index  ==  null  then  name.Type                                                                         

else  null  

REQUIRE:  Expression_PixelSelector.Type ≠ null

#### Expression_PredefinedName  ::=  predefNameKind 

Expression_PredefinedName.TYPE   <=   INTEGER

#### Expression_Unary   ::=   op   Expression

Expression_Unary.Type   <=

let  t  =  Expression.Type  in 

if  op  ∈  {EXCL}  &&  (t  ==  BOOLEAN  ||  t  ==  INTEGER)  then  t                                                          

else  if  op  {PLUS,  MINUS}  &&  t  ==  INTEGER  then  INTEGER

else  null

REQUIRE:    Expression_  Unary.Type  ≠   null

#### Index   ::=   Expression 0    Expression 1

REQUIRE:   Expression0.Type   ==   INTEGER   &&      Expression1.Type   ==   INTEGER

Index.isCartesian   <=   !(Expression0   ==   KW_r   &&   Expression1   ==   KW_a) 

#### LHS   ::=   name   Index

LHS.Declaration   <=   symbolTable.lookupDec(name)                                           

LHS.Type   <=   LHS.Declaration.Type

LHS.isCarteisan   <=   Index.isCartesian

#### Sink   ::=   Sink_Ident   |   Sink_SCREEN 

Sink.Type   <=   Sink_X.Type

#### Sink_Ident   ::=   name

Sink_Ident.Type   <=   symbolTable.lookupType(name)   

REQUIRE: Sink_Ident.Type      ==   FILE

#### Sink_SCREEN   ::=   SCREEN

Sink_SCREEN.Type   <=   SCREEN

#### Source   ::=   Source_CommandLineParam      |   Source_Ident   |   Source_StringLiteral 

#### Source_CommandLineParam  ::=   Expression paramNum

Source_CommandLindParam.Type   <=   null

REQUIRE:  Expression paramNum.Type   ==   INTEGER

#### Source_Ident   ::=   name

Source_Ident.Type   <=   symbolTable.lookupType(name)

REQUIRE:   Source_ Ident.Type   ==   FILE   ||    Source_ Ident.Type   ==   URL 

#### Source_StringLiteral  ::=  fileOrURL

Source_StringLIteral.Type   <=   if   isValidURL(fileOrURL)   then   URL   else   FILE


## Implement a CodeGenVisitor class to traverse the decorated AST and generate code. The abstract syntax has been annotated how it maps into JVM elements.

#### Program ::= name (Declaration | Statement)* 

Generate code for a class called name.                                  
statements are added to main method.

#### Declaration  ::=  Declaration_Image | Declaration_SourceSink | Declaration_Variable 

#### Declaration_Image  ::=   name ( xSize ySize | ε) Source

Add   a   field   to   the   class   with   type   java.awt.image.BufferedImage.  If   there   is   a   source,   visit   the AST   node   to   load   the   String   containing   the   URL   of   file   name   onto   the   stack.      Use   the cop5556fa17.ImageSupport.readImage   method   to   read   the   image.  If   no   index   is   given   pass   null for   the   xSize   and   ySize   (called   X   and   Y   elsewhere)      parameters,   otherwise   visit   the   index   to   leave the   values   on   top   of   the   stack.      These   are   ints,   use   java.lang.Integer.valueOf   to   convert   to Integer.

If   no   source   is   given   use   the   makeImage   method   to   create   an   image. If   no   size   is   given   use values   of   the   predefined   constants   def_X   and   def_Y.

Store   the   image   reference   in   the   field.

#### Declaration_SourceSink ::= Type name Source

Add   a   field   to   the   class   with   the   given   name. If   there   is   a   Source,   visit   it   to   generate   code   to leave   a   String   describing   the   sport   on   top   of   the   stack   and   then   write   it   to   the   field.

#### Declaration_Variable ::=  Type  name  (Expression   |   ε  )

Add   field,   name,   as   static   member   of   class.

If   there   is   an   expression,   generate   code   to   evaluate   it   and   store   the   results   in   the   field. See   comment   about   this   below.

#### Statement   ::=   Statement_Assign   |   Statement_In   |   Statement_Out 

#### Statement_Assign  ::=  LHS  Expression

REQUIRE: LHS.Type  ==  Expression.Type

StatementAssign.isCartesian <= LHS.isCartesian 

If   the   type   is   integer   or   boolean   visit   the   expression   to   generate   code   to   leave   its   value   on   top   of   the   stack.   Then   visit   the LHS   to   generate   code   to   store   the   top   of   the   stack   in  the   lhs   variable.

If   the   type   is   Image,   generate   code   to   loop   over   the   pixels   of   the   image.      For   each   pixel, visit   the   expression   to   generate   code   to   leave   its   value   on   top   of   the   stack   and   visit   LHS to   generate   code   to   store   the   value   in   the   image.  The   range   of   x   is  [0, X]   and   y   is   [0, Y] where   X   and   Y   can   be   obtained   from   the   image   object   using   methods ImageSupport.getX   and   imageSupport.getY.  Note   that   x,   y,   X,   and   Y   are   all   predefined variables   that   need   to   be   added   to   the   class.

To   handle   polar   coordinates,   you   still   loop   over   x   and   y,      but   calculate   r   and   a   using RuntimeFunctions.polar_r   and   RuntimeFunctions.polar_a.

#### Statement_In  ::=  name   Source

Generate   code   to   get   value   from   the   source   and   store   it   in   variable   name.

Visit   source   to   leave   string   representation   of   the   value   on   top   of   stack

Convert   to   a   value   of   correct   type:      

If   name.type   ==   INTEGER   generate   code   to   invoke Java.lang.Integer.parseInt.         

If   BOOLEAN,   invoke   java/lang/Boolean.parseBoolean 

If   Image,   you   have   already   generated   code   to   put   string   with   image   source   on   stack.  Handle   it similarly   to   declaration   with   source.

Statement_In.Declaration   <=   name.Declaration

#### Statement_Out   ::=   name   Sink

For   INTEGERS   and   BOOLEANS,   the   only   “sink”   is   the   screen,   so   generate   code   to   print   to   the console   here.      Use   java.io.PrintStream   .println.      This   is   a   virtual   method,   you   can   use   the   static field   PrintStream   “out”   from   class   java.lang.System   as   the   object.

IF   IMAGE,   load   the   image   and   visit   the   sink.

#### Expression   ::=   Expression_Binary   |   Expression_BooleanLit   |   Expression_Conditional   | Expression_FunctionApp   |   Expression_FunctionAppWithExprArg   | Expression_FunctionAppWithIndexArg   |   Expression_Ident   |   Expression_IntLit   | Expression_PixelSelector   |   Expression_PredefinedName_Expression_Unary

For   each   expression   kind,   generate   code   to   leave   the   value   of   the   expression   on   top   of   the   stack.

#### Expression_Binary   ::=   Expression0    op   Expression1

Generate   code   to   evaluate   the   expression   and   leave   the   value   on   top   of   the   stack. 
Visiting  the  nodes  for  Expression0   and  Expression1  will  generate  code  to  leave  those  values  on the  stack.      Then   just   generate   code   to   perform   the   op.

#### Expression_BooleanLit ::=  value

Generate   code   to   leave   the   value   of   the   literal   on   top   of   the   stack

#### Expression_Conditional ::= Expression_condition  Expression_true  Expression_false

Generate   code   to   evaluate   the   Expression_condition  and   depending   on   its Value,  to  leave  the  value  of  either  Expression_true  or  Expression_false on  top  of  the  stack.

#### Expression_FunctionApp  ::=  Expression_FunctionAppWithExprArg | Expression_FunctionAppWithIndexArg
                                                                
#### Expression_FunctionAppWithExprArg   ::=    function   Expression

Visit   the   expression   to   generate   code   to   leave   its   value   on   top   of   the   stack.      Then   invoke   the corresponding   function   in   RuntimeFunctions.   The   functions   that   belong   here   are   abs   and   log. (You   do   not   need   to   implement   sin,  cos,  or  atan)

#### Expression_FunctionAppWithIndexArg  ::=  function Index

Visit   the   index   to   leave   two   values   on   top   of   the   stack.  Then   invoke   the   corresponding   function in   RuntimeFunctions.      The   functions   that   belong   here   are   cart_x,   cart_y,   polar_r,   and   polar_a. These   functions   convert   between   the   cartesian   (x,y)   and   polar   (r,a)   (i.e.   radius   and   angle   in degrees)   representations   of   the   location   in   the   image.

#### Expression_Ident      ::=         name

Generate   code   to   get   the   value   of   the   variable   and   leave   it   on   top   of   the   stack.

#### Expression_IntLit   ::=   value

Generate   code   to   leave   constant   on   stack.

#### Expression_PixelSelector   ::=   name   Index

Generate   code   to   load   the   image   reference   on   the   stack.      Visit   the   index   to   generate   code   to leave   Cartesian   location   of   index   on   the   stack.      Then      invoke   ImageSupport.getPixel   which generates   code   to   leave   the   value   of   the   pixel   on   the   stack.

#### Expression_PredefinedName   ::=      predefNameKind

Generate   code   to   load   value   of   variable   onto   the   stack.    

#### Expression_Unary   ::=   op   Expression

Generate   code   to   evaluate   the   unary   expression   and   leave   its   value   on   top   of   the   stack. Which   code   is   generated   will   depend   on   the   operator.      If   the   op   is   OP_PLUS,   the   value that   should   be   left   on   the   stack   is   just   the   value   of   Expression

#### Index   ::=   Expression0    Expression1

Visit   the   expressions   to   leave   the   values   on   top   of   the   stack.      If   isCartesian,   you   are   done.      If   not, generate   code   to   convert   r   and   a   to   x   and   y   using   (cart_x   and   cart_y).      Hint:      you   will   need   to manipulate   the   stack   a   little   bit   to   handle   the   two   values.         You   may   find      DUP2,   DUP_X2,   and POP   useful.


#### LHS   ::=   name   Index

If   LHS.Type      is   INTEGER   or   BOOLEAN,   generate   code   to store   the   value   on   top   of   the   stack   in   variable   name.

If   LHS.Type   is   IMAGE,   a      pixel   is   on   top   of   the   stack.      Generate   code   to   store   it   in   the   image   at location   (x,y).      (Load   the   image   ref,   load   x   and   y,   invoke   ImageSupport.setPixel)

#### Sink   ::=   Sink_Ident   |   Sink_SCREEN

#### Sink_Ident   ::=   name

The   identifier   should   contain   a   reference   to   a   String   representing   a   filename.      Generate   code   to write   the   image   to   the   file.      The   image   reference   should   already   be   on   the   stack,   so   load   the filename   and   invoke   ImageSupport.write.

#### Sink_SCREEN   ::=   SCREEN

Generate   code   to   display   the   image   (whose   ref   should   be   on   top   of   the   stack   already)   on   the screen.      Call   ImageFrame.makeFrame.      Note   that   this   method   returns   a   reference   to   the   frame which   is   not   needed,   so   pop   it   off   the   stack.

#### Source   ::=   Source_CommandLineParam      |   Source_Ident   |   Source_StringLiteral

#### Source_CommandLineParam  ::= Expression_paramNum

Generate   code   to   evaluate   the   expression   and   use   aaload   to   read   the   element   from   the   command   line   array   using   the   expression value   as   the   index.      The   command   line   array   is   the   String[]   args   param   passed   to   main.

#### Source_Ident   ::=   name

This   identifier   refers   to   a   String.      Load   it   onto   the   stack.

#### Source_StringLiteral   ::=      fileOrURL 

Load   the   String   onto   the   stack

### Implementing   Predefined   Variables

You   need   to   implement   the   predefined   (integer)   variables   x,y,X,Y,r,a,R,A,   and   Z   in   this   assignment. 
You may   add   static   fields   to   the   class,   like   you   have   done   for   the   declared   variable,   or   you   may   define   them as   local   variables   in   main.      The   former   you   already   know   how   to   do,   the   latter   is   more   convenient   during code   generation.      
If   you   make   them   local   variable,   you   need   to   assign   each   one   a   slot   number   in   the   local variable   array   and   call   mv.visitLocalVariable   before   calling   mv.visitMaxs.      
For   example,   if   x   is   assigned slot   1,   then   invoke   mv.visitLocalVariable("x",   "I",   null,   mainStart,   mainEnd,   1); (Remember   that   the String[]   parameter   in   main   which      contains   the   command   line   parameters   is   in   slot   0).

x:         the   locations   in   the   x   direction,   used   as   a   loop   index   in   assignment   statements   involving   images 

y:      the   location   in   the   y   direction,   used   as   a   loop   index   in   assignment   statements   involving   images

X:      the   upper   bound   on   the   value   of   loop   index   x.      It   is   also   the   width   of   the   image.   Obtain   by   invoking the   ImageSupport.getX   method

Y:      the   upper   bound   on   the   value   of   the   loop   index   y.      It   is   also   the   height   of   the   image.      Obtain   by invoking   the   ImageSupport.getY   method.

r:      the   radius   in   the   polar   representation   of   cartesian   location   x   and   y.      Obtain   from   x   and   y   with RuntimeFunctions.polar_r.

a:      the   angle,   in   degrees,   in   the   polar   representation   of   cartesian   location   x   and   y.      Obtain   from   x   and   y with   RuntimeFunctions.polar_a.

R:      the   upper   bound   on   r,   obtain   from   polar_r(X,Y) 

A:      the   upper   bound   on   a,   obtain   from   polar_a(0,Y)

The   final   three   are   fixed   constants.      You   can   handle   this   by   defining   and   initializing   a   variable,   or   just   by letting   visitExpression_PredefinedName   load   the   constant   value.

DEF_X:      the   default   image   width.      For   simplicity,   let   this   be   256. 

DEF_Y:      the   default   image   height.      For   simplicity,   let   this   be   256. 

Z:      the   value   of   a   white   pixel.      This   is   0xFFFFFF   or      16777215.






                                                                
