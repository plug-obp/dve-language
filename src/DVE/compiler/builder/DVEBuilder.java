package DVE.compiler.builder;

import java.math.BigInteger;
import java.util.List;

import DVE.model.ArrayLiteral;
import DVE.model.ArrayType;
import DVE.model.Assignment;
import DVE.model.BinaryExpression;
import DVE.model.BinaryOperator;
import DVE.model.ByteType;
import DVE.model.ChannelDeclaration;
import DVE.model.ChannelReference;
import DVE.model.ConstantDeclaration;
import DVE.model.Expression;
import DVE.model.IndexedExpression;
import DVE.model.InputSynchronization;
import DVE.model.IntegerType;
import DVE.model.Literal;
import DVE.model.ModelFactory;
import DVE.model.NumberLiteral;
import DVE.model.OutputSynchronization;
import DVE.model.Process;
import DVE.model.ProcessReference;
import DVE.model.ProcessStateReference;
import DVE.model.State;
import DVE.model.StateReference;
import DVE.model.System;
import DVE.model.Type;
import DVE.model.TypedChannelDeclaration;
import DVE.model.UnaryExpression;
import DVE.model.UnaryOperator;
import DVE.model.VariableDeclaration;
import DVE.model.VariableReference;

public class DVEBuilder {
	public static DVEBuilder uniqueInstance = new DVEBuilder();
	ModelFactory dveFactory = ModelFactory.eINSTANCE;
	public Expression binaryExpression(Expression lhs, BinaryOperator op, Expression rhs) {
		BinaryExpression exp = dveFactory.createBinaryExpression();
		exp.setOperator(op);
		exp.getOperands().add(lhs);
		exp.getOperands().add(rhs);
		return exp;
	}
	public Literal literal(boolean bool) {
		return bool ? dveFactory.createTrueLiteral() : dveFactory.createFalseLiteral();
	}
	public Literal literal(BigInteger i) {
		NumberLiteral num = dveFactory.createNumberLiteral();
		num.setValue(i);
		return num;
	}
	public Literal literal(int i) {
		NumberLiteral num = dveFactory.createNumberLiteral();
		num.setValue(BigInteger.valueOf(i));
		return num;
	}
	public Literal literal(List<Expression> exps) {
		ArrayLiteral lit = dveFactory.createArrayLiteral();
		lit.getValues().addAll(exps);
		return lit;
	}
	public Expression imply(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.IMPLY, rhs);
	}
	
	public Expression or(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.OR, rhs);
	}
	
	public Expression and(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.AND, rhs);
	}
	
	public Expression bor(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.BOR, rhs);
	}
	
	public Expression band(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.BAND, rhs);
	}
	public Expression bxor(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.BXOR, rhs);
	}
	public Expression eq(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.EQ, rhs);
	}
	
	public Expression neq(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.NEQ, rhs);
	}
	public Expression lt(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.LT, rhs);
	}
	public Expression leq(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.LEQ, rhs);
	}
	public Expression gt(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.GT, rhs);
	}
	public Expression geq(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.GEQ, rhs);
	}
	public Expression shl(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.SHL, rhs);
	}
	public Expression shr(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.SHR, rhs);
	}
	public Expression minus(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.MINUS, rhs);
	}
	public Expression plus(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.PLUS, rhs);
	}
	public Expression mult(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.MULT, rhs);
	}
	public Expression div(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.DIV, rhs);
	}
	public Expression mod(Expression lhs, Expression rhs) {
		return binaryExpression(lhs, BinaryOperator.MOD, rhs);
	}
	
	public Expression unaryExpression(UnaryOperator op, Expression rhs) {
		UnaryExpression exp = dveFactory.createUnaryExpression();
		exp.setOperator(op);
		exp.setOperand(rhs);
		return exp;
	}
	public Expression minus(Expression rhs) {
		return unaryExpression(UnaryOperator.MINUS, rhs);
	}
	public Expression bnot(Expression rhs) {
		return unaryExpression(UnaryOperator.BNOT, rhs);
	}
	public Expression not(Expression rhs) {
		return unaryExpression(UnaryOperator.NOT, rhs);
	}
	public Expression indexedExpression(Expression base, Expression index) {
		IndexedExpression exp = dveFactory.createIndexedExpression();
		exp.setBase(base);
		exp.setIndex(index);
		return exp;
	}
	public Expression processStateReference(Process proc, State state) {
		ProcessStateReference exp = dveFactory.createProcessStateReference();
		exp.setPrefix(reference(proc));
		exp.setRef(state);
		return exp;
	}
	public VariableDeclaration variableDeclaration(String name, Type type, Expression initial) {
		VariableDeclaration vdecl = dveFactory.createVariableDeclaration();
		vdecl.setName(name);
		vdecl.setType(type);
		vdecl.setInitial(initial);
		return vdecl;
	}
	public ConstantDeclaration constantDeclaration(String name, Type type, Expression initial) {
		ConstantDeclaration vdecl = dveFactory.createConstantDeclaration();
		vdecl.setName(name);
		vdecl.setType(type);
		vdecl.setInitial(initial);
		return vdecl;
	}
	public IntegerType intType() {
		return dveFactory.createIntegerType();
	}
	public ByteType byteType() {
		return dveFactory.createByteType();
	}
	public ArrayType arrayType(Type etype, int size) {
		ArrayType type = dveFactory.createArrayType();
		type.setElementType(etype);
		type.setSize(literal(size));
		return type;
	}
	public ArrayType arrayType(Type etype, Expression size) {
		ArrayType type = dveFactory.createArrayType();
		type.setElementType(etype);
		type.setSize(size);
		return type;
	}
	public ChannelDeclaration channelDeclaration(String name) {
		ChannelDeclaration c = dveFactory.createChannelDeclaration();
		c.setName(name);
		return c;
	}
	
	public TypedChannelDeclaration typedChannelDeclaration(String name, List<Type> types, int bufferSize) {
		TypedChannelDeclaration tcd = dveFactory.createTypedChannelDeclaration();
		tcd.setName(name);
		tcd.setBufferSize(literal(bufferSize));
		tcd.getTypes().addAll(types);
		return tcd;
	}
	public State state(String name) {
		State s = dveFactory.createState();
		s.setName(name);
		return s;
	}
	public InputSynchronization input(ChannelReference chan, Expression exp) {
		InputSynchronization is = dveFactory.createInputSynchronization();
		is.setChannel(chan);
		is.setValue(exp);
		return is;
	}
	public OutputSynchronization output(ChannelReference chan, Expression exp) {
		OutputSynchronization is = dveFactory.createOutputSynchronization();
		is.setChannel(chan);
		is.setValue(exp);
		return is;
	}
	public ChannelReference reference(ChannelDeclaration decl) {
		ChannelReference cr = dveFactory.createChannelReference();
		cr.setRef(decl);
		return cr;
	}
	public ChannelReference channelReference(String name) {
		ChannelReference cr = dveFactory.createChannelReference();
		cr.setRefName(name);
		return cr;
	}
	public VariableReference reference(VariableDeclaration decl) {
		VariableReference vr = dveFactory.createVariableReference();
		vr.setRef(decl);
		return vr;
	}
	public VariableReference reference(VariableReference vr0) {
		VariableReference vr = dveFactory.createVariableReference();
		vr.setRef(vr0.getRef());
		return vr;
	}
	public VariableReference variableReference(String name) {
		VariableReference vr = dveFactory.createVariableReference();
		vr.setRefName(name);
		return vr;
	}
	public StateReference reference(State s) {
		StateReference sr = dveFactory.createStateReference();
		sr.setRef(s);
		return sr;
	}
	public StateReference stateReference(String s) {
		StateReference sr = dveFactory.createStateReference();
		sr.setRefName(s);
		return sr;
	}
	
	public ProcessReference reference(Process p) {
		ProcessReference pr = dveFactory.createProcessReference();
		pr.setRef(p);
		return pr;
	}
	
	public ProcessReference processReference(String name) {
		ProcessReference pr = dveFactory.createProcessReference();
		pr.setRefName(name);
		return pr;
	}
	public Assignment assign(Expression lhs, Expression rhs) {
		Assignment assign = dveFactory.createAssignment();
		assign.setLhs(lhs);
		assign.setRhs(rhs);
		return assign;
	}
	
	public System system() {
		return dveFactory.createSystem();
	}
}
