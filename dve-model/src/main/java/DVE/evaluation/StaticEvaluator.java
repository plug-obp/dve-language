package DVE.evaluation;

import DVE.compiler.builder.DVEBuilder;
import DVE.model.*;
import DVE.model.util.ModelSwitch;

import java.math.BigInteger;
import java.util.List;

public class StaticEvaluator {
	DVEBuilder builder = DVEBuilder.uniqueInstance;
	ModelSwitch<Literal> modelSwitch = new ModelSwitch<Literal>() {
		public Literal caseUnaryExpression(DVE.model.UnaryExpression object) {
			NumberLiteral lit = (NumberLiteral)doSwitch(object.getOperand());
			if (lit == null) return null;
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
			BigInteger lhsV;
			BigInteger rhsV;
			Literal lhsLit = doSwitch(object.getOperands().get(0));
			Literal rhsLit = doSwitch(object.getOperands().get(1));
			if (lhsLit == null || rhsLit == null) return null;
			if (lhsLit instanceof ArrayLiteral || rhsLit instanceof ArrayLiteral) return null;
			if (lhsLit instanceof FalseLiteral) {
				lhsV = BigInteger.ZERO;
			} else if (lhsLit instanceof TrueLiteral) {
				lhsV = BigInteger.ONE;
			} else {
				lhsV = ((NumberLiteral)lhsLit).getValue();
			}

			if (rhsLit instanceof FalseLiteral) {
				rhsV = BigInteger.ZERO;
			} else if (rhsLit instanceof TrueLiteral) {
				rhsV = BigInteger.ONE;
			} else {
				rhsV = ((NumberLiteral)rhsLit).getValue();
			}

//			NumberLiteral lhs = (NumberLiteral)doSwitch(object.getOperands().get(0));
//			NumberLiteral rhs = (NumberLiteral)doSwitch(object.getOperands().get(1));
//			if (lhs == null || rhs == null) return null;
//			BigInteger lhsV = lhs.getValue();
//			BigInteger rhsV = rhs.getValue();
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
			if (object.getRef().getInitial() != null) {
				return doSwitch(object.getRef().getInitial());
			}
			return null;
		}
		
		public Literal caseIndexedExpression(DVE.model.IndexedExpression object) {
			NumberLiteral idx = (NumberLiteral) doSwitch(object.getIndex());
			ArrayLiteral aL = (ArrayLiteral) doSwitch(object.getBase());
			if (aL != null && idx != null) {
				return doSwitch(aL.getValues().get(idx.getValue().intValue()));
			}
			return null;
		}
		
		public Literal caseArrayLiteral(ArrayLiteral object) {

			List<Expression> exps = object.getValues();
			for (int idx = 0; idx < exps.size(); idx ++ ){
				Literal lit = doSwitch(exps.get(idx));
				if (lit != null) {
					exps.set(idx, lit);
				}
			}
			return object;
		}
		public Literal caseLiteral(Literal object) { 
			return object;
		}
		public Literal caseExpression(Expression object) {
			//System.err.println("the object is not a static expression");
			return null;
		}
	};
	
	public Literal eval(Expression exp) {
		return modelSwitch.doSwitch(exp);
	}
	
	static StaticEvaluator uniqueInstance = new StaticEvaluator();
	
	public static Literal evaluate(Expression exp) {
		if (exp == null) return null;
		return uniqueInstance.eval(exp);
	}
}
