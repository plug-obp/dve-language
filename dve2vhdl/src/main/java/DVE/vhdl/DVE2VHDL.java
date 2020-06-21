package DVE.vhdl;

import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import DVE.transformations.DVEFixBooleanExpressions;
import DVE.vhdl.frames.*;
import DVE.vhdl.frames.values.types.SStateType;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

public class DVE2VHDL {


    GlobalFrame globalContext;

    ModelSwitch<String> modelSwitch = new ModelSwitch<String>() {

        Stack<CompositeFrame> context = new Stack<>();
        @Override
        public String caseSystem(System object) {
            context.push(globalContext);
            StringBuilder sb = new StringBuilder();

            for (NamedDeclaration decl : object.getDeclarations()) {
                sb.append("\n----------------");
                sb.append(decl.getName());
                sb.append("----------------\n");
                sb.append(doSwitch(decl));
            }
            context.pop();
            return sb.toString();
        }

        @Override
        public String caseProcess(Process object) {
            ProcessFrame frame = (ProcessFrame)context.peek().lookup(object.getName());
            context.push(frame);
            StringBuilder sb = new StringBuilder();
            for (Transition transition : object.getTransitions()) {
                sb.append(doSwitch(transition));
            }
            context.pop();
            return sb.toString();
        }

        @Override
        public String caseTransition(Transition object) {
            StringBuilder sb = new StringBuilder();
            LeafFrame stateFrame = (LeafFrame) context.peek().lookup("%state%");
            SStateType stateType = (SStateType) stateFrame.type;

            sb.append("if (");
            //check if the process is in the from state of the transition
            sb.append("to_integer(unsigned(current(");
            sb.append(stateFrame.offset() + stateFrame.size());
            sb.append(" DOWNTO ");
            sb.append(stateFrame.offset());
            sb.append("))) = ");
            sb.append(stateType.get(doSwitch(object.getFrom())));

            //check if the guard is true
            if (object.getGuard() != null) {
                sb.append(" AND ");
                sb.append(doSwitch(object.getGuard()));
            }
            sb.append(") THEN \n");

            //generate state change
            sb.append("current(");
            sb.append(stateFrame.offset() + stateFrame.size());
            sb.append(" DOWNTO ");
            sb.append(stateFrame.offset());
            sb.append(") := to_unsigned(");
            sb.append(stateType.get(doSwitch(object.getTo())));
            sb.append(", ");
            sb.append(stateFrame.size());
            sb.append(");\n");

            //generate the effect
            for (Assignment assignment : object.getEffect()) {
                sb.append(doSwitch(assignment));
                sb.append("\n");
            }

            sb.append("\nEND IF;\n");

            return sb.toString();
        }

        @Override
        public String caseStateReference(StateReference object) {
            return doSwitch(object.getRef());
        }

        @Override
        public String caseState(State object) {
            return object.getName();
        }

        @Override
        public String caseVariableDeclaration(VariableDeclaration object) {
            return object.getName();
        }

        @Override
        public String caseConstantDeclaration(ConstantDeclaration object) {
            return object.getName();
        }

        @Override
        public String caseExpression(Expression object) {
            reportError("Expression " + object + " not supported yet");
            return "TODO: EXPRESSION";
        }

        @Override
        public String caseUnaryExpression(UnaryExpression object) {
            if (object.getOperand() instanceof StateReference) {
                reportError("State expressions are not supported yet");
                return "(TODO: STATE_EXP)";
            }
            switch (object.getOperator()) {
                case BNOT:
                    reportError("Fiacre does not have the binary not operator");
                    return "DVE_BINARY_NOT (" + doSwitch(object.getOperand()) + ")";
                case MINUS:
                    return "DVE_UNARY_MINUS (" + doSwitch(object.getOperand()) + ")";
                case NOT:
                    return "DVE_LOGICAL_NOT (" + doSwitch(object.getOperand()) + ")";
                default:
                    break;
            }
            return null;
        }

        @Override
        public String caseBinaryExpression(BinaryExpression object) {
            if (object.getOperands().get(0) instanceof StateReference
                    || object.getOperands().get(1) instanceof StateReference) {
                reportError("State expressions are not supported yet");
                return "(TODO: STATE_EXP)";
            }
            String operator = "";
            switch (object.getOperator()) {

                case IMPLY: operator = "DVE_IMPLY";
                    break;
                case OR: operator = "DVE_LOGICAL_OR";
                    break;
                case AND: operator = "DVE_LOGICAL_AND";
                    break;
                case BOR: operator = "DVE_BINARY_OR";
                    break;
                case BAND: operator = "DVE_BINARY_OR";
                    break;
                case BXOR: operator = "DVE_BINARY_XOR";
                    break;
                case EQ: operator = "DVE_EQUALS";
                    break;
                case NEQ: operator = "DVE_NOT_EQUALS";
                    break;
                case LT: operator = "DVE_LT";
                    break;
                case LEQ: operator = "DVE_LEQ";
                    break;
                case GT: operator = "DVE_GT";
                    break;
                case GEQ: operator = "DVE_GEQ";
                    break;
                case SHL: operator = "DVE_SHL";
                    break;
                case SHR: operator = "DVE_SHR";
                    break;
                case PLUS: operator = "DVE_PLUS";
                    break;
                case MINUS: operator = "DVE_MINUS";
                    break;
                case MULT: operator = "DVE_MULTIPLICATION";
                    break;
                case DIV: operator = "DVE_DIVISION";
                    break;
                case MOD: operator = "DVE_MODULO";
                    break;
            }
            return operator + "(" + doSwitch(object.getOperands().get(0)) + ", " + doSwitch(object.getOperands().get(1)) + ")";
        }

        @Override
        public String caseVariableReference(VariableReference object) {
            StringBuilder sb = new StringBuilder();
            LeafFrame frame = (LeafFrame) context.peek().lookup(doSwitch(object.getRef()));

            if (frame instanceof ConstantFrame) {
                sb.append("constants(");
                sb.append(frame.offset() + frame.size());
                sb.append(" DOWNTO ");
                sb.append(frame.offset());
                sb.append(")");
            } else {
                sb.append("current(");
                sb.append(frame.offset() + frame.size());
                sb.append(" DOWNTO ");
                sb.append(frame.offset());
                sb.append(")");
            }

            return sb.toString();
        }

        @Override
        public String caseNumberLiteral(NumberLiteral object) {
            return "" + object.getValue();
        }

        @Override
        public String caseTrueLiteral(TrueLiteral object) {
            return "'1'";
        }

        @Override
        public String caseFalseLiteral(FalseLiteral object) {
            return "'0'";
        }

        @Override
        public String caseArrayLiteral(ArrayLiteral object) {
            reportError("ArrayLiterals are not supported yet");
            return "(TODO: ARRAY_LITERAL)";
        }

        @Override
        public String caseIndexedExpression(IndexedExpression object) {
            return doSwitch(object.getBase()) + "( to_integer(" + doSwitch(object.getIndex()) + ") * ELEMENT_TYPE_SIZE + ELEMENT_TYPE_SIZE DOWNTO to_integer(" + doSwitch(object.getIndex()) + ") * ELEMENT_TYPE_SIZE)";
        }

        @Override
        public String caseProcessStateReference(ProcessStateReference object) {
            reportError("ProcessStateReference are not supported yet");
            return "(TODO: ProcessStateReference)";
        }

        @Override
        public String caseAssignment(Assignment object) {
            return doSwitch(object.getLhs()) + " := " + doSwitch(object.getRhs()) + ";";
        }
    };

    Set<String> errorMessages = new LinkedHashSet<>();

    void reportError(String error) {
        errorMessages.add(error);
    }

    public static String transform(System model) {
        DVEFixBooleanExpressions booleanFixer = new DVEFixBooleanExpressions();
        booleanFixer.apply(model);

        DVE2VHDL dve2VHDL = new DVE2VHDL();
        dve2VHDL.globalContext = DVEFrameBuilder.process(model);
        String vhdlCode = dve2VHDL.modelSwitch.doSwitch(model);
        for (String errorM : dve2VHDL.errorMessages) {
            java.lang.System.err.println(errorM);
        }
        if (!dve2VHDL.errorMessages.isEmpty()) return null;
        return vhdlCode;
    }
}
