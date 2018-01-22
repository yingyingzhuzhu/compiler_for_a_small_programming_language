# compiler_for_a_small_programming_language

## Implement a scanner for the programming language with the following lexical structure:

RawInputCharacter ::=  any ASCII character LineTerminator   ::=  LF  |  CR  |  CR LF

LF   is   the   ASCII   character   also   known   as   “newline”,   in   java   \n

CR   is   the   ASII   character   also   known   as   “return”,   in   Java,   the   char   \r CR   immediately   followed   by   LF   counts   as   one   line   terminator,   not   two

InputCharacter ::=  RawInputCharacter,   but   not   CR   or   LF Input   ::=   (WhiteSpace   |   Comment   |   Token)*

Token ::=   Identifier | Keyword | Literal | Separator | Operator WhiteSpace   ::=   SP   |  HT  |  FF  | LineTerminator

SP   is   the   ASCII   character   also   known   as   “space”

HT   is   the   ASCII   character   also   known   as   “horizontal   tab” FF   is   the   ASCII   character   known   also   known   as   “form   feed”

Comment  ::=  //InputCharacter*

Identifier  ::=  IdentifierChars   but   not   a   Keyword   or   BooleanLiteral 

IdentifierChars   ::=   IdentiferStart   IdentifierPart*

IdentifierStart  ::=    A .. Z    |    a .. z    |    _    |  $  

IdentifierPart   ::=   IdentifierStart   |      Digit

Literal  ::=  IntegerLiteral   |   BooleanLiteral   |  StringLiteral 

IntegerLiteral ::=  0 |NonZeroDigit  Digit*

NonZeroDigit  ::=    1    ..    9

Digit ::=   NonZeroDigit   |    0

BooleanLiteral  ::=    true | false

StringLiteral   ::=    “ StringCharacter* “

StringCharacter ::=  InputCharacter  but  not  “  or  \|  EscapeSequence 

EscapeSequence   ::=  \b | \t | \n | \f | \r | \” | \’ | \\ 

Separators  ::=    (  |  )  |  [ | ]  |  ;  |  ,


Operators  ::= =  | > | < | ! | ? | : |  ==  |  !=  |  <=  |  >=  |  &  |  |  | + |  - |  *  |  / |  % |  **  | -> | <- | @

Keywords ::=  x | X | y | Y | r | R | a | A | Z | DEF_X | DEF_Y | SCREEN | cart_x |  cart_y  | polar_a | polar_r | abs | sin |  cos | atan | log | image | int | boolean | url | file
