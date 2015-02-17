grammar DVE;

LINE_COMMENT : '//' .*? '\n' -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;
WS : [ \r\t\n]+ -> skip ;

system : declaration* processDecl+ systemProperties;
declaration : variableDecl
			| channelDecl;
processDecl : PROCESS IDENTIFIER '{' processBody '}';
variableDecl : CONST? type declarationIdentifierList ';';
declarationIdentifierList : declarationIdentifier (',' declarationIdentifier)*;
declarationIdentifier : objectDecl variableInitialization?;
objectDecl : IDENTIFIER arraySelector?;
objectDeclList : objectDecl (',' objectDecl)*;
arraySelector : '[' expression ']';
variableInitialization : '=' expression;
type : INT
	| BYTE;
typeList : type (',' type)*;

channelDecl : CHANNEL ('{' typeList '}')? objectDeclList ';';

literal : booleanLiteral
		| numberLiteral
		| arrayLiteral
		;
booleanLiteral : FALSE | TRUE;
numberLiteral : NUMBER;
arrayLiteral : '{' expressionList '}';

reference : objectDecl (selector objectDecl)?;
selector : VARSEL | STATESEL;

expressionList : expression (',' expression)*;
expression : literal												#LiteralExpression
			| reference 											#ReferenceExpression
			| '(' expression ')'									#ParenExpression
			| operator=(MINUS | BNOT | NOT) expression				#UnaryExpression
			| expression operator=(MULT | DIV | MOD) expression		#BinaryExpression
			| expression operator=(PLUS | MINUS) expression			#BinaryExpression
			| expression operator=(SHL | SHR) expression			#BinaryExpression
			| expression operator=(LE | LT | GE | GT) expression	#BinaryExpression
			| expression operator=(EQ | NEQ) expression				#BinaryExpression
			| expression operator=(BOR | BAND | BXOR) expression	#BinaryExpression
			| expression operator=(OR | AND) expression				#BinaryExpression
			| expression operator=(IMPLY | IMPLY) expression		#BinaryExpression
			;
 
processBody : variableDecl* states stateTypeDecls transitions?;
states : STATE identifierList ';';
stateTypeDecls :  initDecl commitDecl? acceptDecl?
				| initDecl acceptDecl? commitDecl?
				| commitDecl? initDecl acceptDecl?
				| acceptDecl? initDecl commitDecl?
				| commitDecl? acceptDecl? initDecl
				| acceptDecl? commitDecl? initDecl
				;
initDecl : INIT IDENTIFIER ';';
commitDecl : COMMIT identifierList ';';
acceptDecl : ACCEPT identifierList ';';

transitions: TRANS transitionList ';';
transitionList : transition (',' transition)*;
transition : IDENTIFIER? VARSEL IDENTIFIER '{' guard? sync? effect? '}';
guard : GUARD expression ';';
sync : SYNC syncExpression ';';
syncExpression : IDENTIFIER syncOperator expression?;
syncOperator : INPUT | OUTPUT;
effect : EFFECT assignmentList ';';
assignmentList : assignment (',' assignment)*;
assignment : expression '=' expression;

systemProperties: SYSTEM systemType property? ';';
systemType : ASYNC | SYNC;
property : PROPERTY IDENTIFIER;

identifierList : IDENTIFIER (',' IDENTIFIER)*;

PROCESS: 'process';
FALSE : 'false';
TRUE : 'true';
VARSEL : '->';
STATESEL : '.';
STATE : 'state';
INIT : 'init';
COMMIT : 'commit';
ACCEPT : 'accept';
TRANS : 'trans';
GUARD : 'guard';
SYNC : 'sync';
INPUT : '?';
OUTPUT : '!';
EFFECT : 'effect';
SYSTEM : 'system';
ASYNC : 'async';
PROPERTY : 'property';
CONST : 'const';
INT : 'int';
BYTE : 'byte';
CHANNEL : 'channel';
MINUS : '-';
BNOT: '~';
NOT: 'not';
MULT: '*'; 
DIV: '/'; 
MOD: '%'; 
PLUS: '+'; 
SHL: '<<'; 
SHR: '>>'; 
LE: '<='; 
LT: '<'; 
GE: '>='; 
GT: '>';
EQ: '=='; 
NEQ: '!=';
BOR: '|';
BAND: '&';
BXOR: '^';
OR: 'or' | '||'; 
AND:'and' | '&&';
IMPLY:'imply';

IDENTIFIER : [a-zA-Z_][a-zA-Z_0-9]*;
NUMBER : [0-9]+;



