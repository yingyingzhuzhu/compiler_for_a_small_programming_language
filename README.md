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
