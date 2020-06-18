package DVE.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.TerminalNode;
import DVE.compiler.builder.DVEBuilder;
import DVE.grammar.DVEBaseListener;
import DVE.grammar.DVEParser;
import DVE.grammar.DVEParser.ArrayLiteralContext;
import DVE.grammar.DVEParser.BinaryExpressionContext;
import DVE.grammar.DVEParser.BooleanLiteralContext;
import DVE.grammar.DVEParser.ChannelDeclContext;
import DVE.grammar.DVEParser.DeclarationIdentifierContext;
import DVE.grammar.DVEParser.NumberLiteralContext;
import DVE.grammar.DVEParser.ObjectDeclContext;
import DVE.grammar.DVEParser.ProcessDeclContext;
import DVE.grammar.DVEParser.ReferenceContext;
import DVE.grammar.DVEParser.StatesContext;
import DVE.grammar.DVEParser.SyncExpressionContext;
import DVE.grammar.DVEParser.SystemContext;
import DVE.grammar.DVEParser.TypeContext;
import DVE.grammar.DVEParser.UnaryExpressionContext;
import DVE.grammar.DVEParser.VariableDeclContext;
import org.eclipse.emf.ecore.util.EcoreUtil;

import DVE.model.Assignment;
import DVE.model.BinaryOperator;
import DVE.model.ChannelDeclaration;
import DVE.model.CompositeDeclaration;
import DVE.model.Element;
import DVE.model.Expression;
import DVE.model.InputSynchronization;
import DVE.model.Literal;
import DVE.model.ModelFactory;
import DVE.model.OutputSynchronization;
import DVE.model.Process;
import DVE.model.ProcessReference;
import DVE.model.ProcessStateReference;
import DVE.model.ProcessVariableReference;
import DVE.model.State;
import DVE.model.StateReference;
import DVE.model.System;
import DVE.model.SystemProperties;
import DVE.model.SystemType;
import DVE.model.Transition;
import DVE.model.Type;
import DVE.model.TypedChannelDeclaration;
import DVE.model.UnaryOperator;
import DVE.model.VariableDeclaration;
import DVE.model.VariableReference;

public class DVEASTBuilder extends DVEBaseListener {
	ModelFactory dveFactory = ModelFactory.eINSTANCE;
	DVEBuilder builder = new DVEBuilder();
	Stack<Element> declarationContext = new Stack<Element>();
	Stack<Element> context = new Stack<Element>();
	Stack<Expression> exprStack = new Stack<Expression>();

	
	
	public void pushContext(Element node) {
		declarationContext.push(node);
	}

	public <T extends Element> T popContext(Class<T> type) {
		if (declarationContext.size() < 1) return null;
		Element decl = declarationContext.pop();
		if (type.isAssignableFrom(decl.getClass())) {
			return type.cast(decl);
		}
		return null;
	}
	
	private <T extends Element> T peekContext(Class<T> type) {
		if (declarationContext.size() < 1) return null;
		Element decl = declarationContext.peek();
		if (type.isAssignableFrom(decl.getClass())) {
			return type.cast(decl);
		}
		return null;
	}
	
	private void reportError(String description) {
		throw new Error("ASTBuiler:" + description);
	}

	private <T extends Element> T node(Class<T> type) {
		if (context.size() < 1) return null;
		Element decl = context.peek();
		if (type.isAssignableFrom(decl.getClass())) {
			return type.cast(decl);
		}
		return null;
	}

	@Override
	public void enterVariableDecl(VariableDeclContext ctx) {
		if (ctx.CONST() != null) {
			context.push(dveFactory.createConstantDeclaration());
			return;
		}
		context.push(dveFactory.createVariableDeclaration());
	}
	
	@Override
	public void exitVariableDecl(VariableDeclContext ctx) {
		context.pop();
	}

	@Override
	public void enterChannelDecl(ChannelDeclContext ctx) {
		if (ctx.typeList() == null) {
			context.push(dveFactory.createChannelDeclaration());
			return;
		}
		context.push(dveFactory.createTypedChannelDeclaration());
	}
	
	@Override
	public void exitChannelDecl(ChannelDeclContext ctx) {
		context.pop();
	}
	
	@Override
	public void exitType(TypeContext ctx) {
		Type type = null;
		if (ctx.INT() != null) {
			type = dveFactory.createIntegerType();
		} else if (ctx.BYTE() != null) {
			type = dveFactory.createByteType();
		}
		VariableDeclaration varDecl = node(VariableDeclaration.class);
		if (varDecl != null) {
			varDecl.setType(type);
			return;
		}
		//it is a channel decl
		TypedChannelDeclaration chanDecl = node(TypedChannelDeclaration.class);
		chanDecl.getTypes().add(type);
	}
	
	@Override
	public void exitDeclarationIdentifier(DeclarationIdentifierContext ctx) {
		VariableDeclaration orig  = node(VariableDeclaration.class);
		VariableDeclaration vdecl = EcoreUtil.copy(orig);
		//set name
		String name = ctx.objectDecl().IDENTIFIER().getText();
		vdecl.setName(name);
		if (ctx.variableInitialization() != null) {
			vdecl.setInitial(exprStack.pop());
		}
		if (ctx.objectDecl().arraySelector() != null) {		
			vdecl.setType(builder.arrayType(vdecl.getType(), exprStack.pop()));
		}
		peekContext(CompositeDeclaration.class).getDeclarations().add(vdecl);
	}
	
	@Override
	public void exitObjectDecl(ObjectDeclContext ctx) {
		ChannelDeclaration orig = node(ChannelDeclaration.class);
		if (orig == null) return; //I have something else as context - i.e. variable decl
		ChannelDeclaration chan = EcoreUtil.copy(orig);
		String name = ctx.IDENTIFIER().getText();
		chan.setName(name);
		TypedChannelDeclaration tchan = (TypedChannelDeclaration) ((chan instanceof TypedChannelDeclaration) ? chan : null);
		if (tchan != null) {
			if (ctx.arraySelector() != null) {
				tchan.setBufferSize(exprStack.pop());
			}
			peekContext(CompositeDeclaration.class).getDeclarations().add(tchan);
			return;
		}
		if (ctx.arraySelector() != null) {
			reportError("unexpected buffered synchronization channel");
		}
		peekContext(CompositeDeclaration.class).getDeclarations().add(chan);
	}
	
	@Override
	public void exitNumberLiteral(NumberLiteralContext ctx) {
		Literal exp = builder.literal(Integer.parseInt(ctx.NUMBER().getText()));
		exprStack.push(exp);
	}

	@Override
	public void exitBooleanLiteral(BooleanLiteralContext ctx) {
		switch (ctx.start.getType()) {
		case DVEParser.TRUE:
			exprStack.push(builder.literal(true));
			break;
		case DVEParser.FALSE:
			exprStack.push(builder.literal(false));
			break;
		default:
			reportError("unexpected boolean literal");
		}
	}
	
	@Override
	public void exitArrayLiteral(ArrayLiteralContext ctx) {
		int size = ctx.expressionList().expression().size();
		Expression[] l = new Expression[size];

		for (int i = 0; i < size; i++) {
			l[size - i - 1] = exprStack.pop();
		}
		
		exprStack.push(builder.literal(Arrays.asList(l)));
	}
	
	@Override
	public void exitReference(ReferenceContext ctx) {
		//name resolution happens in post processing
		Expression exp = null;
		if (ctx.selector() != null) {
			//process local variables/constants or states
			String processName = ctx.objectDecl(0).IDENTIFIER().getText();
			switch (ctx.selector().start.getType()) {
			case DVEParser.VARSEL:
				ProcessVariableReference pvr = dveFactory.createProcessVariableReference();
				pvr.setPrefix(builder.processReference(processName));
				String variableName = ctx.objectDecl(1).IDENTIFIER().getText();
				pvr.setRefName(variableName);
				if (ctx.objectDecl(1).arraySelector() != null) {
					exp = builder.indexedExpression(pvr, exprStack.pop());
					return;
				}
				exp = pvr;
				break;
			case DVEParser.STATESEL:
				ProcessStateReference psr = dveFactory.createProcessStateReference();
				psr.setPrefix(builder.processReference(processName));
				String stateName = ctx.objectDecl(1).IDENTIFIER().getText();
				psr.setRefName(stateName);
				exp = psr;
				break;
			default:
			}
			exprStack.push(exp);
			return;
		}
		//name resolution happens in post processing
		//global variable/constant reference
		String varName = ctx.objectDecl(0).IDENTIFIER().getText();
		VariableReference vref = dveFactory.createVariableReference();
		vref.setRefName(varName);
		exp = vref;
		if (ctx.objectDecl(0).arraySelector() != null) {
			//Indexed array
			exp = builder.indexedExpression(vref, exprStack.pop());
		}
		exprStack.push(exp);
	}
	
	@Override
	public void exitUnaryExpression(UnaryExpressionContext ctx) {
		UnaryOperator op;
		switch (ctx.operator.getType()) {
		case DVEParser.NOT:
			op = UnaryOperator.NOT;
			break;
		case DVEParser.MINUS:
			op = UnaryOperator.MINUS;
			break;
		case DVEParser.BNOT:
			op = UnaryOperator.BNOT;
			break;
		default:
			reportError("unexpected unary operator");
			return;
		}
		exprStack.push(builder.unaryExpression(op, exprStack.pop()));
	}

	@Override
	public void exitBinaryExpression(BinaryExpressionContext ctx) {
		BinaryOperator op;
		switch (ctx.operator.getType()) {
		case DVEParser.MULT:
			op = BinaryOperator.MULT;
			break;
		case DVEParser.DIV:
			op = BinaryOperator.DIV;
			break;
		case DVEParser.MOD:
			op = BinaryOperator.MOD;
			break;
		case DVEParser.PLUS:
			op = BinaryOperator.PLUS;
			break;
		case DVEParser.MINUS:
			op = BinaryOperator.MINUS;
			break;
		case DVEParser.SHL:
			op = BinaryOperator.SHL;
			break;
		case DVEParser.SHR:
			op = BinaryOperator.SHR;
			break;
		case DVEParser.LT:
			op = BinaryOperator.LT;
			break;
		case DVEParser.LE:
			op = BinaryOperator.LEQ;
			break;
		case DVEParser.GT:
			op = BinaryOperator.GT;
			break;
		case DVEParser.GE:
			op = BinaryOperator.GEQ;
			break;
		case DVEParser.EQ:
			op = BinaryOperator.EQ;
			break;
		case DVEParser.NEQ:
			op = BinaryOperator.NEQ;
			break;
		case DVEParser.BOR:
			op = BinaryOperator.BOR;
			break;
		case DVEParser.BAND:
			op = BinaryOperator.BAND;
			break;
		case DVEParser.BXOR:
			op = BinaryOperator.BXOR;
			break;
		case DVEParser.OR:
			op = BinaryOperator.OR;
			break;
		case DVEParser.AND:
			op = BinaryOperator.AND;
			break;
		case DVEParser.IMPLY:
			op = BinaryOperator.IMPLY;
			break;
		default:
			reportError("unexpected binary operator");
			return;
		}
		Expression right = exprStack.pop(); // the top of the stack has the right operand
		Expression left = exprStack.pop();
		
		exprStack.push(builder.binaryExpression(left, op, right));
	}
	
	@Override
	public void exitAssignment(DVE.grammar.DVEParser.AssignmentContext ctx) {
		Transition tran = peekContext(Transition.class);
		Assignment assign = dveFactory.createAssignment();
		assign.setRhs(exprStack.pop());
		assign.setLhs(exprStack.pop());
		tran.getEffect().add(assign);
	}
	
	@Override
	public void exitSyncExpression(SyncExpressionContext ctx) {
		Transition tran = peekContext(Transition.class);
		String channelName = ctx.IDENTIFIER().getText();

		if (ctx.syncOperator().INPUT() != null) {
			InputSynchronization isync = dveFactory.createInputSynchronization();
			isync.setChannel(builder.channelReference(channelName));
			if (ctx.expression() != null) {
				isync.setValue(exprStack.pop());
			}
			
			tran.setSync(isync);
			return;
		}
		
		OutputSynchronization isync = dveFactory.createOutputSynchronization();
		isync.setChannel(builder.channelReference(channelName));
		if (ctx.expression() != null) {
			isync.setValue(exprStack.pop());
		}
		
		tran.setSync(isync);
		return;
	}
	
	@Override
	public void exitGuard(DVE.grammar.DVEParser.GuardContext ctx) {
		Transition tran = peekContext(Transition.class);
		tran.setGuard(exprStack.pop());
	}
	
	@Override
	public void enterTransition(DVE.grammar.DVEParser.TransitionContext ctx) {
		pushContext(dveFactory.createTransition());
	}
	
	@Override
	public void exitTransition(DVE.grammar.DVEParser.TransitionContext ctx) {
		Transition tran = popContext(Transition.class);
		if (ctx.IDENTIFIER(0) != null) {
			tran.setFrom(builder.stateReference(ctx.IDENTIFIER(0).getText()));
		}
		tran.setTo(builder.stateReference(ctx.IDENTIFIER(1).getText()));
		Process proc = peekContext(Process.class);
		proc.getTransitions().add(tran);
	}
	
	@Override
	public void exitAcceptDecl(DVE.grammar.DVEParser.AcceptDeclContext ctx) {
		List<StateReference> stateList = peekContext(Process.class).getAccepting();
		for (TerminalNode id : ctx.identifierList().IDENTIFIER()) {
			StateReference ref = dveFactory.createStateReference();
			ref.setRefName(id.getText());
			stateList.add(ref);
		}
	}
	
	@Override
	public void exitCommitDecl(DVE.grammar.DVEParser.CommitDeclContext ctx) {
		List<StateReference> stateList = peekContext(Process.class).getCommited();
		for (TerminalNode id : ctx.identifierList().IDENTIFIER()) {
			StateReference ref = dveFactory.createStateReference();
			ref.setRefName(id.getText());
			stateList.add(ref);
		}
	}
	
	@Override
	public void exitInitDecl(DVE.grammar.DVEParser.InitDeclContext ctx) {
		StateReference sr = dveFactory.createStateReference();
		sr.setRefName(ctx.IDENTIFIER().getText());
		peekContext(Process.class).setInitial(sr);
	}
	
	@Override
	public void exitStates(StatesContext ctx) {
		List<State> stateList = peekContext(Process.class).getStates();
		for (TerminalNode id : ctx.identifierList().IDENTIFIER()) {
			State state = builder.state(id.getText());
			stateList.add(state);
		}
	}
	
	@Override
	public void enterProcessDecl(ProcessDeclContext ctx) {
		pushContext(dveFactory.createProcess());
	}
	
	@Override
	public void exitProcessDecl(ProcessDeclContext ctx) {
		Process proc = popContext(Process.class);
		proc.setName(ctx.IDENTIFIER().getText());
		System sys = peekContext(System.class);
		sys.getDeclarations().add(proc);
		sys.getProcesses().add(proc);
	}
	
	@Override
	public void exitProperty(DVE.grammar.DVEParser.PropertyContext ctx) {
		ProcessReference pr = builder.processReference(ctx.IDENTIFIER().getText());
		peekContext(SystemProperties.class).setProperty(pr);
	}
	
	@Override
	public void exitSystemType(DVE.grammar.DVEParser.SystemTypeContext ctx) {
		SystemType st = null;
		if (ctx.ASYNC() != null) {
			st = dveFactory.createAsynchronous();
		}
		else if (ctx.SYNC() != null) {
			st = dveFactory.createSynchronous();
		}
		peekContext(SystemProperties.class).setSystemType(st);
	}
	
	@Override
	public void enterSystemProperties(DVE.grammar.DVEParser.SystemPropertiesContext ctx) {
		pushContext(dveFactory.createSystemProperties());
	}
	
	@Override
	public void exitSystemProperties(DVE.grammar.DVEParser.SystemPropertiesContext ctx) {
		SystemProperties props = popContext(SystemProperties.class);
		peekContext(System.class).setProperties(props);
	}
	
	@Override
	public void enterSystem(SystemContext ctx) {
		pushContext(dveFactory.createSystem());
	}
	System system;
	@Override
	public void exitSystem(SystemContext ctx) {
		system = popContext(System.class);
	}

}
