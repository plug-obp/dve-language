package DVE.transformations;

import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;

public class PrettyPrinter extends ModelSwitch<String> {
    public static String toString(EObject element) {
        PrettyPrinter pp = new PrettyPrinter();
        return pp.doSwitch(element);
    }

    int tab = 0;

    void tab(StringBuffer sb) {
        for (int i = 0; i < tab; i++) {
            sb.append("\t");
        }
    }

    @Override
    public String caseSystem(System object) {
        StringBuffer sb = new StringBuffer();

        for (NamedDeclaration decl : object.getDeclarations()) {
            sb.append(doSwitch(decl));
        }

        sb.append(doSwitch(object.getProperties()));

        return sb.toString();
    }

    @Override
    public String caseSystemProperties(SystemProperties object) {
        StringBuffer sb = new StringBuffer();

        sb.append("\nsystem ");
        sb.append(doSwitch(object.getSystemType()));
        if (object.getProperty() != null) {
            sb.append(" property ");
            sb.append(doSwitch(object.getProperty()));
        }
        sb.append(";\n");
        return sb.toString();
    }

    @Override
    public String caseAsynchronous(Asynchronous object) {
        return "async";
    }

    @Override
    public String caseSynchronous(Synchronous object) {
        return "sync";
    }

    @Override
    public String caseProcessReference(ProcessReference object) {
        return object.getRefName();
    }

    @Override
    public String caseVariableDeclaration(VariableDeclaration object) {
        StringBuffer sb = new StringBuffer();

        tab(sb);
        sb.append(doSwitch(object.getType()));
        sb.append(" ");
        sb.append(object.getName());
        if (object.getInitial() != null) {
            sb.append(" = ");
            sb.append(doSwitch(object.getInitial()));
        }
        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public String caseConstantDeclaration(ConstantDeclaration object) {
        StringBuffer sb = new StringBuffer();

        tab(sb);
        sb.append("const ");
        sb.append(doSwitch(object.getType()));
        sb.append(" ");
        sb.append(object.getName());
        if (object.getInitial() != null) {
            sb.append(" = ");
            sb.append(doSwitch(object.getInitial()));
        }
        sb.append(";\n");

        return sb.toString();
    }

    @Override
    public String caseChannelDeclaration(ChannelDeclaration object) {
        return "channel " + object.getName() + ";\n";
    }

    @Override
    public String caseTypedChannelDeclaration(TypedChannelDeclaration object) {
        StringBuffer sb = new StringBuffer();

        sb.append("channel ");

        sb.append("{");
        int n = object.getTypes().size();
        for (Type type : object.getTypes()) {
            n--;
            sb.append(doSwitch(type));
            if (n != 0) {
                sb.append(", ");
            }
        }
        sb.append("} ");

        sb.append(object.getName());

        if (object.getBufferSize() != null) {
            sb.append("[");
            sb.append(doSwitch(object.getBufferSize()));
            sb.append("]");
        }

        sb.append(";\n");
        return sb.toString();
    }

    @Override
    public String caseByteType(ByteType object) {
        return "byte";
    }

    @Override
    public String caseIntegerType(IntegerType object) {
        return "int";
    }

    @Override
    public String caseArrayType(ArrayType object) {
        StringBuffer sb = new StringBuffer();

        sb.append(doSwitch(object.getElementType()));
        sb.append("[");
        sb.append(doSwitch(object.getSize()));
        sb.append("]");

        return sb.toString();
    }

    @Override
    public String caseTrueLiteral(TrueLiteral object) {
        return "true";
    }

    @Override
    public String caseFalseLiteral(FalseLiteral object) {
        return "false";
    }

    @Override
    public String caseNumberLiteral(NumberLiteral object) {
        return object.getValue().toString();
    }

    @Override
    public String caseArrayLiteral(ArrayLiteral object) {
        StringBuffer sb = new StringBuffer();

        sb.append("{");
        boolean first = true;
        for (Expression e : object.getValues()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(doSwitch(e));
        }
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String caseProcess(Process object) {
        StringBuffer sb = new StringBuffer();

        sb.append("\nprocess ");
        sb.append(object.getName());
        sb.append(" {\n");
        tab++;

        for (NamedDeclaration decl : object.getDeclarations()) {
            sb.append(doSwitch(decl));
        }

        tab(sb);
        sb.append("state ");
        boolean first = true;
        for (State state : object.getStates()) {
            if (first) first = false;
            else sb.append(", ");
            sb.append(doSwitch(state));
        }
        sb.append(";\n");

        tab(sb);
        sb.append("init ");
        sb.append(doSwitch(object.getInitial()));
        sb.append(";\n");

        if (!object.getCommited().isEmpty()) {
            tab(sb);
            sb.append("commit ");
            first = true;
            for (StateReference stateReference : object.getCommited()) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(doSwitch(stateReference));
            }
            sb.append(";\n");
        }

        if (!object.getAccepting().isEmpty()) {
            tab(sb);
            sb.append("accept ");
            first = true;
            for (StateReference stateReference : object.getAccepting()) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(doSwitch(stateReference));
            }
            sb.append(";\n");
        }

        tab(sb);
        sb.append("trans\n");
        tab++;
        int n = object.getTransitions().size();
        for (Transition transition : object.getTransitions()) {
            n--;
            sb.append(doSwitch(transition));
            if (n != 0) {
                sb.append(",\n");
            }
        }
        tab--;
        sb.append(";\n");
        tab(sb);
        sb.append("}\n");
        tab--;

        return sb.toString();
    }

    @Override
    public String caseTransition(Transition object) {
        StringBuffer sb = new StringBuffer();

        tab(sb);
        sb.append(doSwitch(object.getFrom()));
        sb.append(" -> ");
        sb.append(doSwitch(object.getTo()));
        sb.append(" {\n");
        tab++;
        if (object.getGuard() != null) {
            tab(sb);
            sb.append("guard ");
            sb.append(doSwitch(object.getGuard()));
            sb.append(";\n");
        }
        if (object.getSync() != null) {
            tab(sb);
            sb.append("sync ");
            sb.append(doSwitch(object.getSync()));
            sb.append(";\n");
        }
        if (!object.getEffect().isEmpty()) {
            tab(sb);
            sb.append("effect ");
            tab++;
            int n = object.getEffect().size();
            for (Assignment assignment : object.getEffect()) {
                n--;
                sb.append("\n");
                tab(sb);
                sb.append(doSwitch(assignment));
                if (n != 0) {
                    sb.append(",");
                }
            }
            sb.append(";\n");
            tab--;
        }
        tab--;
        tab(sb);
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String caseAssignment(Assignment object) {
        return doSwitch(object.getLhs()) + " = " + doSwitch(object.getRhs());
    }

    @Override
    public String caseState(State object) {
        return object.getName();
    }

    @Override
    public String caseStateReference(StateReference object) {
        return object.getRefName();
    }

    @Override
    public String caseVariableReference(VariableReference object) {
        return object.getRefName();
    }

    @Override
    public String caseProcessVariableReference(ProcessVariableReference object) {
        return doSwitch(object.getPrefix()) + "." + object.getRefName();
    }

    @Override
    public String caseProcessStateReference(ProcessStateReference object) {
        return doSwitch(object.getPrefix()) + "." + object.getRefName();
    }

    @Override
    public String caseChannelReference(ChannelReference object) {
        return object.getRefName();
    }

    @Override
    public String caseInputSynchronization(InputSynchronization object) {
        return doSwitch(object.getChannel()) + "?" + (object.getValue() != null ? doSwitch(object.getValue()) : "");
    }

    @Override
    public String caseOutputSynchronization(OutputSynchronization object) {
        return doSwitch(object.getChannel()) + "!" + (object.getValue() != null ? doSwitch(object.getValue()) : "");
    }

    @Override
    public String caseIndexedExpression(IndexedExpression object) {
        return doSwitch(object.getBase()) + "[" + doSwitch(object.getIndex()) + "]";
    }

    @Override
    public String caseUnaryExpression(UnaryExpression object) {
        switch (object.getOperator()) {
            case MINUS:
                return "-" + doSwitch(object.getOperand());
            case BNOT:
                return "~" + doSwitch(object.getOperand());
            case NOT:
                return "not " + doSwitch(object.getOperand());
        }
        return "UNARY_EXP_ERROR";
    }

    @Override
    public String caseBinaryExpression(BinaryExpression object) {
        switch (object.getOperator()) {

            case IMPLY:
                return doSwitch(object.getOperands().get(0)) + " imply " + doSwitch(object.getOperands().get(1));
            case OR:
                return doSwitch(object.getOperands().get(0)) + " or " + doSwitch(object.getOperands().get(1));
            case AND:
                return doSwitch(object.getOperands().get(0)) + " and " + doSwitch(object.getOperands().get(1));
            case BOR:
                return doSwitch(object.getOperands().get(0)) + " | " + doSwitch(object.getOperands().get(1));
            case BAND:
                return doSwitch(object.getOperands().get(0)) + " & " + doSwitch(object.getOperands().get(1));
            case BXOR:
                return doSwitch(object.getOperands().get(0)) + " ^ " + doSwitch(object.getOperands().get(1));
            case EQ:
                return doSwitch(object.getOperands().get(0)) + " == " + doSwitch(object.getOperands().get(1));
            case NEQ:
                return doSwitch(object.getOperands().get(0)) + " != " + doSwitch(object.getOperands().get(1));
            case LT:
                return doSwitch(object.getOperands().get(0)) + " < " + doSwitch(object.getOperands().get(1));
            case LEQ:
                return doSwitch(object.getOperands().get(0)) + " <= " + doSwitch(object.getOperands().get(1));
            case GT:
                return doSwitch(object.getOperands().get(0)) + " > " + doSwitch(object.getOperands().get(1));
            case GEQ:
                return doSwitch(object.getOperands().get(0)) + " >= " + doSwitch(object.getOperands().get(1));
            case SHL:
                return doSwitch(object.getOperands().get(0)) + " << " + doSwitch(object.getOperands().get(1));
            case SHR:
                return doSwitch(object.getOperands().get(0)) + " >> " + doSwitch(object.getOperands().get(1));
            case PLUS:
                return doSwitch(object.getOperands().get(0)) + " + " + doSwitch(object.getOperands().get(1));
            case MINUS:
                return doSwitch(object.getOperands().get(0)) + " - " + doSwitch(object.getOperands().get(1));
            case MULT:
                return doSwitch(object.getOperands().get(0)) + " * " + doSwitch(object.getOperands().get(1));
            case DIV:
                return doSwitch(object.getOperands().get(0)) + " / " + doSwitch(object.getOperands().get(1));
            case MOD:
                return doSwitch(object.getOperands().get(0)) + " % " + doSwitch(object.getOperands().get(1));
        }
        return "BINARY_EXP_ERROR";
    }
}
