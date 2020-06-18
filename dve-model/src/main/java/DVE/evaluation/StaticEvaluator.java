package DVE.evaluation;

import java.util.List;
import java.math.BigInteger;

import DVE.compiler.builder.DVEBuilder;

import DVE.model.ArrayLiteral;
import DVE.model.Expression;
import DVE.model.Literal;
import DVE.model.NumberLiteral;
import DVE.model.util.ModelSwitch;

public class StaticEvaluator {
	DVEBuilder builder = DVEBuilder.uniqueInstance;
	ModelSwitch<Literal> modelSwitch = new ModelSwitch<Literal>() {
		public Literal caseUnaryExpression(DVE.model.UnaryExpression object) {
			NumberLiteral lit = (NumberLiteral)doSwitch(object.getOperand());
			switch (object.getOperator()) {
			case BNOT:
				return builder.literal(lit.getValue().not());
			case MINUS:
				return builder.literal(lit.getValue().negate());
			case NOT:
				return builder.literal((lit.getValue().equals(BigInteger.ZERO)) ? 1 : 0);
			default:
				break;
			
			}
			return null;
		}
		public Literal caseBinaryExpression(DVE.model.BinaryExpression object) {
			NumberLiteral lhs = (NumberLiteral)doSwitch(object.getOperands().get(0));
			NumberLiteral rhs = (NumberLiteral)doSwitch(object.getOperands().get(1));
			BigInteger lhsV = lhs.getValue();
			BigInteger rhsV = rhs.getValue();
			boolean lhsIsTrue = ! lhsV.equals(BigInteger.ZERO);
			boolean rhsIsTrue = ! rhsV.equals(BigInteger.ZERO);
			switch(object.getOperator()) {
			case AND:
				return builder.literal(lhsIsTrue && rhsIsTrue);
			case BAND:
				return builder.literal(lhsV.and(rhsV));
			case BOR:
				return builder.literal(lhsV.or(rhsV));
			case BXOR:
				return builder.literal(lhsV.xor(rhsV));
			case DIV:
				return builder.literal(lhsV.divide(rhsV));
			case EQ:
				return builder.literal(lhsV.compareTo(rhsV) == 0);
			case GEQ:
				return builder.literal(lhsV.compareTo(rhsV) >= 0);
			case GT:
				return builder.literal(lhsV.compareTo(rhsV) > 0);
			case IMPLY:
				break;
			case LEQ:
				return builder.literal(lhsV.compareTo(rhsV) <= 0);
			case LT:
				return builder.literal(lhsV.compareTo(rhsV) < 0);
			case MINUS:
				return builder.literal(lhsV.subtract(rhsV));
			case MOD:
				return builder.literal(lhsV.mod(rhsV));
			case MULT:
				return builder.literal(lhsV.multiply(rhsV));
			case NEQ:
				return builder.literal(lhsV.compareTo(rhsV) != 0);
			case OR:
				return builder.literal(lhsIsTrue || rhsIsTrue);
			case PLUS:
				return builder.literal(lhsV.add(rhsV));
			case SHL:
				return builder.literal(lhsV.shiftLeft(rhsV.intValue()));
			case SHR:
				return builder.literal(lhsV.shiftRight(rhsV.intValue()));
			default:
				break;
				
			}
			return null;
		}
		
		public Literal caseVariableReference(DVE.model.VariableReference object) {
			return doSwitch(object.getRef().getInitial());
		}
		
		public Literal caseIndexedExpression(DVE.model.IndexedExpression object) {
			NumberLiteral idx = (NumberLiteral) doSwitch(object.getIndex());
			ArrayLiteral aL = (ArrayLiteral) doSwitch(object.getBase());
			return doSwitch(aL.getValues().get(idx.getValue().intValue()));
		}
		
		public Literal caseArrayLiteral(ArrayLiteral object) {
			List<Expression> exps = object.getValues();
			for (int idx = 0; idx < exps.size(); idx ++ ){
				Literal lit = doSwitch(exps.get(idx));
				exps.set(idx, lit);
			}
			return object;
		}
		public Literal caseLiteral(Literal object) { 
			return object;
		}
		public Literal caseExpression(Expression object) {
			System.err.println("the object is not a static expression");
			return null;
		}
	};
	
	public Literal eval(Expression exp) {
		return modelSwitch.doSwitch(exp);
	}
	
	static StaticEvaluator uniqueInstance = new StaticEvaluator();
	
	public static Literal evaluate(Expression exp) {
		return uniqueInstance.eval(exp);
	}
}
