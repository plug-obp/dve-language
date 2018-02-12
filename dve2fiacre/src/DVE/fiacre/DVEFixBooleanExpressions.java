package DVE.fiacre;

import DVE.compiler.builder.DVEBuilder;
import org.eclipse.emf.ecore.util.EcoreUtil;

import DVE.model.Expression;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.Transition;
import DVE.model.util.ModelSwitch;

//this class converts variables to boolean expression in boolean context
//byte x = 5;
// guard x becomes guard x != 0
public class DVEFixBooleanExpressions {
	DVEBuilder builder = DVEBuilder.uniqueInstance;
	ModelSwitch<Expression> modelSwitch = new ModelSwitch<Expression>() {
		public Expression caseSystem(DVE.model.System object) {
			for (Process proc : object.getProcesses()) {
				doSwitch(proc);
			}
			return null;
		}
		
		public Expression caseProcess(Process object) {
			for (Transition transition : object.getTransitions()) {
				doSwitch(transition);
			}
			return null;
		}
		
		public Expression caseTransition(Transition object) {

			if (object.getGuard() != null)
				object.setGuard(doSwitch(object.getGuard()));

			return null;
		}
		public Expression caseUnaryExpression(DVE.model.UnaryExpression object) {
			switch (object.getOperator()) {
			case MINUS:
			case BNOT:
				return builder.neq(EcoreUtil.copy(object), builder.literal(0));
			case NOT:
				object.setOperand(doSwitch(object.getOperand()));
				break;
			}
			return object;
		}
		public Expression caseBinaryExpression(DVE.model.BinaryExpression object) {
			switch (object.getOperator()) {
			case BAND:
			case BOR:
			case BXOR:
			case DIV:
			case MINUS:
			case MOD:
			case MULT:
			case PLUS:
			case SHL:
			case SHR:
				//replace exp by (exp neq 0)
				return builder.neq(EcoreUtil.copy(object), builder.literal(0));
			case EQ:
			case NEQ:
			case GEQ:
			case GT:
			case LEQ:
			case LT:
				//nothing to do they already return a boolean
				break;
			case AND:
			case OR:
			case IMPLY:
				
				Expression lhs = doSwitch(object.getOperands().get(0));
				Expression rhs = doSwitch(object.getOperands().get(1));
				
				object.getOperands().set(0, lhs);
				object.getOperands().set(1, rhs);
				break;
			default:
				break;
			}
			return object;
		}
		
		public Expression caseVariableReference(DVE.model.VariableReference object) {
			return builder.neq(EcoreUtil.copy(object), builder.literal(0));
		}
		public Expression caseProcessVariableReference(DVE.model.ProcessVariableReference object) {
			return builder.neq(EcoreUtil.copy(object), builder.literal(0));
		}
		public Expression caseIndexedExpression(DVE.model.IndexedExpression object) {
			return builder.neq(EcoreUtil.copy(object), builder.literal(0));
		}
		public Expression caseNumberLiteral(DVE.model.NumberLiteral object) {
			return builder.neq(EcoreUtil.copy(object), builder.literal(0));
		}
		
		public Expression caseExpression(Expression object) {
			return object;
		}
	};
	
	public void apply(System sys) {
		modelSwitch.doSwitch(sys);
	}
}
