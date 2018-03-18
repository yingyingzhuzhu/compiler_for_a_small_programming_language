# compiler_for_a_small_programming_language

## Implement a scanner for the programming language with the following lexical structure:

RawInputCharacter ::=  any ASCII character 

LineTerminator   ::=  LF  |  CR  |  CR LF

LF   is   the   ASCII   character   also   known   as   “newline”,   in   java   \n

CR   is   the   ASII   character   also   known   as   “return”,   in   Java,   the   char   \r CR   immediately   followed   by   LF   counts   as   one   line   terminator,   not   two

InputCharacter ::=  RawInputCharacter,   but   not   CR   or   LF 

Input   ::=   (WhiteSpace   |   Comment   |   Token)*

Token ::=   Identifier | Keyword | Literal | Separator | Operator 

WhiteSpace   ::=   SP   |  HT  |  FF  | LineTerminator

SP   is   the   ASCII   character   also   known   as   “space”

HT   is   the   ASCII   character   also   known   as   “horizontal   tab” 

FF   is   the   ASCII   character   known   also   known   as   “form   feed”

Comment  ::=  //InputCharacter*

Identifier  ::=  IdentifierChars   but   not   a   Keyword   or   BooleanLiteral 

IdentifierChars   ::=   IdentiferStart   IdentifierPart*

IdentifierStart  ::=    A .. Z    |    a .. z    |    _    |  $  

IdentifierPart   ::=   IdentifierStart  |  Digit

Literal  ::=  IntegerLiteral   |   BooleanLiteral   |  StringLiteral 

IntegerLiteral ::=  0 | NonZeroDigit  Digit*

NonZeroDigit  ::=   1  ..  9

Digit ::=  NonZeroDigit |  0

BooleanLiteral  ::=   true | false

StringLiteral   ::=   “ StringCharacter* “

StringCharacter ::=  InputCharacter  but  not  “  or  \|  EscapeSequence 

EscapeSequence   ::=  \b | \t | \n | \f | \r | \” | \’ | \\ 

Separators  ::=    (  |  )  |  [ | ]  |  ;  |  ,


Operators  ::= =  | > | < | ! | ? | : |  ==  |  !=  |  <=  |  >=  |  &  |  |  | + |  - |  *  |  / |  % |  **  | -> | <- | @

Keywords ::=  x | X | y | Y | r | R | a | A | Z | DEF_X | DEF_Y | SCREEN | cart_x |  cart_y  | polar_a | polar_r | abs | sin |  cos | atan | log | image | int | boolean | url | file



## Implement  a   recursive   descent   parser   for   the   following   context-free   grammar. The   parser   should   simply   determine   whether   the   given   sentence   is   legal   or   not.    If   not, the   parser   should   throw   a   SyntaxException.    If   the   sentence   is   legal,   the   parse   method should   simply   return.

Program   ::=    IDENTIFIER   (   Declaration   SEMI   |   Statement   SEMI   )*

Declaration   ::   =      VariableDeclaration       |     ImageDeclaration     |    SourceSinkDeclaration 

VariableDeclaration   ::=      VarType   IDENTIFIER    (    OP_ASSIGN      Expression    |   ε   )

VarType   ::=   KW_int   |   KW_boolean

SourceSinkDeclaration   ::=   SourceSinkType  IDENTIFIER   OP_ASSIGN    Source

Source   ::=   STRING_LITERAL

Source   ::=   OP_AT   Expression

Source   ::=   IDENTIFIER

SourceSinkType   :=   KW_url   |   KW_file

ImageDeclaration  ::=   KW_image    (LSQUARE   Expression   COMMA   Expression   RSQUARE   |   ε)                                                   IDENTIFIER  (  OP_LARROW  Source  |  ε  )

Statement      ::=   AssignmentStatement
|   ImageOutStatement
|   ImageInStatement

ImageOutStatement   ::=   IDENTIFIER   OP_RARROW   Sink

Sink   ::=   IDENTIFIER   |   KW_SCREEN    //ident   must   be   file

ImageInStatement   ::=   IDENTIFIER   OP_LARROW   Source

AssignmentStatement   ::=   Lhs   OP_ASSIGN   Expression

Expression   ::=      OrExpression    OP_Q    Expression   OP_COLON   Expression  |   OrExpression

OrExpression   ::=   AndExpression   (   OP_OR  AndExpression)*

AndExpression   ::=   EqExpression   (   OP_AND  EqExpression   )*

EqExpression   ::=   RelExpression   ( (OP_EQ  |  OP_NEQ ) RelExpression  )*

RelExpression   ::=   AddExpression   ( (OP_LT  |   OP_GT  |  OP_LE  | OP_GE  ) AddExpression)* 

AddExpression   ::=   MultExpression  ( (OP_PLUS   |   OP_MINUS )  MultExpression )* 

MultExpression   :=   UnaryExpression   ( ( OP_TIMES | OP_DIV | OP_MOD ) UnaryExpression )*

UnaryExpression   ::=   OP_PLUS   UnaryExpression
                      |   OP_MINUS   UnaryExpression
                       |   UnaryExpressionNotPlusMinus

UnaryExpressionNotPlusMinus   ::=   OP_EXCL  UnaryExpression |   Primary |  IdentOrPixelSelectorExpression   |   KW_x   |   
KW_y   |   KW_r   |   KW_a   |   KW_X   |   KW_Y   |   KW_Z   | KW_A   |   KW_R   |   KW_DEF_X   |   KW_DEF_Y

Primary  ::=   INTEGER_LITERAL   |   LPAREN   Expression   RPAREN   |   FunctionApplication   |    BOOLEAN_LITERAL 

IdentOrPixelSelectorExpression::=   IDENTIFIER  LSQUARE  Selector  RSQUARE  |  IDENTIFIER

Lhs::=  IDENTIFIER (  LSQUARE  LhsSelector  RSQUARE  |  ε  )

FunctionApplication  ::=   FunctionName   LPAREN   Expression   RPAREN  |   FunctionName      LSQUARE   Selector   RSQUARE

FunctionName   ::=   KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r

LhsSelector   ::=   LSQUARE ( XySelector | RaSelector ) RSQUARE 

XySelector   ::=   KW_x   COMMA   KW_y

RaSelector   ::=   KW_r   COMMA   KW_A

Selector   ::=   Expression   COMMA   Expression


## Implement an ASTVisitor to annotate and type check the abstract syntax tree generated by the parser. The following attribute grammar specifies the type system.

Program ::=  name  (Declaration  |  Statement)* Program.name  <=  name

Declaration  ::=  Declaration_Image   |   Declaration_SourceSink   |   Declaration_Variable 

Declaration_Image ::=   name   (  xSize   ySize | ε)  Source

REQUIRE:  symbolTable.lookupType(name)  =  symbolTable.insert(name, Declaration_Image)

Declaration_Image.Type  <=   IMAGE

REQUIRE  if   xSize   !=   ε   then   ySize   !=   ε   &&   xSize.Type   ==   INTEGER   &&   ySize.type   ==   INTEGER 

Declaration_SourceSink  ::=   Type name Source

REQUIRE:  symbolTable.lookupType(name)   =  null

symbolTable.insert(name,   Declaration_SourceSink )

Declaration_SourceSink.Type   <=   Type

REQUIRE  Source.Type  ==  Declaration_SourceSink.Type  | Declaration_SourceSink.Type   ==  null 

Declaration_Variable  ::=  Type name (Expression | ε)

REQUIRE: symbolTable.lookupType(name)   =  null

symbolTable.insert(name,    Declaration_Variable )

Declaration_Variable.Type   <=   Type

REQUIRE   if (Expression !=  ε)   Declaration_Variable.Type   ==   Expression.Type 

Statement  ::=   Statement_Assign   |   Statement_In   |   Statement_Out

Statement_Assign  ::=  LHS  Expression

REQUIRE:  LHS.Type   ==   Expression.Type

StatementAssign.isCartesian   <=   LHS.isCartesian 


Statement_In   ::=   name  Source

Statement_In.Declaration   <=   name.Declaration

REQUIRE:  (name.Declaration   !=   null)   &   (name.type   ==   Source.type) 



Statement_Out  ::=   name   Sink

Statement_ Out .Declaration   <=   name.Declaration

REQUIRE:  (name.Declaration   !=   null)

REQUIRE:  ((name.Type == INTEGER || name.Type == BOOLEAN) && Sink.Type ==  SCREEN) || (name.Type == IMAGE  && (Sink.Type  ==FILE || Sink.Type == SCREEN)) 

Expression   ::=   Expression_Binary   |   Expression_BooleanLit   |   Expression_Conditional  | Expression_FunctionApp  |  

Expression_FunctionAppWithExprArg  | Expression_FunctionAppWithIndexArg   |  Expression_Ident  |  Expression_IntLit  | 

Expression_PixelSelector  |  Expression_PredefinedName  _  Expression_Unary

Expression.Type  <=  Expression_X.Type 

Expression_Binary  ::=  Expression0    op   Expression1

REQUIRE:  Expression0.Type  ==  Expression1.Type &&  Expression_Binary.Type  ≠  null

Expression_Binary.type  <= 

if   op   ∈   {EQ,  NEQ} then BOOLEAN 

else   if   (op   ∈   {GE,   GT, LT,   LE}   &&   Expression0.Type   ==   INTEGER)  then   BOOLEAN 

else  if  (op  ∈  {AND,  OR}) && (Expression0.Type   ==   INTEGER   ||   Expression0.Type   ==BOOLEAN) then   Expression0.Type 

else   if   op   ∈   {DIV,   MINUS,   MOD,   PLUS,   POWER,   TIMES}   &&   Expression0.Type   == INTEGER  

else  null  

Expression_BooleanLit ::=  value Expression_BooleanLit.Type   <=   BOOLEAN

Expression_Conditional ::=  Expression condition  Expression true  Expression false

REQUIRE:  Expressioncondition.Type   ==   BOOLEAN   &&   Expressiontrue.Type  ==Expressionfalse.Type
          
Expression_Conditional.Type   <=   Expressiontrue.Type 

Expression_FunctionApp  ::=   Expression_FunctionAppWithExprArg  |  Expression_FunctionAppWithIndexArg 

Expression_FunctionApp.Type   <=   Expression_FunctionAppWithXArg.Type

Expression_FunctionAppWithExprArg   ::=   function   Expression

REQUIRE: Expression.Type   ==   INTEGER
         
Expression_FunctionAppWithExprArg.Type   <=   INTEGER 
         
Expression_FunctionAppWithIndexArg  ::=  function   Index

Expression_FunctionAppWithIndexArg.Type  <=  INTEGER

Expression_Ident   ::=  name

Expression_Ident.Type   <=   symbolTable.lookupType(name)

Expression_IntLit  ::=  value 

Expression_IntLIt.Type   <=   INTEGER

Expression_PixelSelector ::=  name Index

name.Type   <=   SymbolTable.lookupType(name)

Expression_PixelSelector.Type   <=      

if   name.Type   ==   IMAGE   then   INTEGER                                                                         

else  if  Index  ==  null  then  name.Type                                                                         

else  null  

REQUIRE:  Expression_PixelSelector.Type ≠ null

Expression_PredefinedName  ::=  predefNameKind 

Expression_PredefinedName.TYPE   <=   INTEGER

Expression_Unary   ::=   op   Expression

Expression_Unary.Type   <=

let  t  =  Expression.Type  in 

if  op  ∈  {EXCL}  &&  (t  ==  BOOLEAN  ||  t  ==  INTEGER)  then  t                                                          

else  if  op  {PLUS,  MINUS}  &&  t  ==  INTEGER  then  INTEGER

else  null

REQUIRE:    Expression_  Unary.Type  ≠   null

Index   ::=   Expression 0    Expression 1

REQUIRE:   Expression0.Type   ==   INTEGER   &&      Expression1.Type   ==   INTEGER

Index.isCartesian   <=   !(Expression0   ==   KW_r   &&   Expression1   ==   KW_a) 

LHS   ::=   name   Index

LHS.Declaration   <=   symbolTable.lookupDec(name)                                           

LHS.Type   <=   LHS.Declaration.Type

LHS.isCarteisan   <=   Index.isCartesian

Sink   ::=   Sink_Ident   |   Sink_SCREEN 

Sink.Type   <=   Sink_X.Type

Sink_Ident   ::=   name

Sink_Ident.Type   <=   symbolTable.lookupType(name)   

REQUIRE: Sink_Ident.Type      ==   FILE

Sink_SCREEN   ::=   SCREEN

Sink_SCREEN.Type   <=   SCREEN

Source   ::=   Source_CommandLineParam      |   Source_Ident   |   Source_StringLiteral 

Source_CommandLineParam  ::=   Expression paramNum

Source_CommandLindParam.Type   <=   null

REQUIRE:  Expression paramNum.Type   ==   INTEGER

Source_Ident   ::=   name

Source_Ident.Type   <=   symbolTable.lookupType(name)

REQUIRE:   Source_ Ident.Type   ==   FILE   ||    Source_ Ident.Type   ==   URL 

Source_StringLiteral  ::=  fileOrURL

Source_StringLIteral.Type   <=   if   isValidURL(fileOrURL)   then   URL   else   FILE
