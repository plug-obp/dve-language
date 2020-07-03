package sdve.transformations;

import DVE.model.Assignment;
import DVE.model.Expression;
import DVE.model.NamedDeclaration;
import DVE.model.Type;
import SDVE.model.System;
import SDVE.model.*;
import SDVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;

public class PrettyPrinter {
    public static String toString(DVE.model.System system) {
        PrettyPrinter pp = new PrettyPrinter();
        return pp.modelSwitch.doSwitch(system);
    }

    public static String toString(EObject element) {
        PrettyPrinter pp = new PrettyPrinter();
        return pp.modelSwitch.doSwitch(element);
    }
    ModelSwitch<String> modelSwitch = new ModelSwitch<String>() {
        @Override
        public String caseSystem(System object) {
            StringBuffer sb = new StringBuffer();
            for (NamedDeclaration decl : object.getDeclarations()) {
                sb.append(doSwitch(decl));
            }

            for (Transition transition : object.getTransitions()) {
                sb.append(doSwitch(transition));
            }

            if (!object.getAcceptingConditions().isEmpty()) {
                sb.append("accepting conditions\n");
                for (Expression acceptingCondition : object.getAcceptingConditions()) {
                    sb.append("\t").append(doSwitch(acceptingCondition)).append("\n");
                }
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
                sb.append(object.getProperty());
            }
            sb.append(";\n");
            return sb.toString();
        }

        @Override
        public String caseTransition(Transition object) {
            StringBuilder sb = new StringBuilder();

            sb.append("\tProcess ").append(object.getProcess()).append(" ");

            sb.append("\n\t\t");
            if (object.getGuard() != null) {
                sb.append("guard ");
                sb.append(doSwitch(object.getGuard()));
                sb.append(";");
            }

            sb.append("\n\t\t");
            if (object.getSync() != null) {
                sb.append("sync ");
                sb.append(doSwitch(object.getSync()));
                sb.append(";");
            }
            sb.append("\n\t\t");
            if (!object.getEffect().isEmpty()) {
                sb.append("effect ");

                int n = object.getEffect().size();
                for (Assignment assignment : object.getEffect()) {
                    n--;
                    sb.append("\n");
                    sb.append("\t\t\t");
                    sb.append(doSwitch(assignment));
                    if (n != 0) {
                        sb.append(",");
                    }
                }
                sb.append(";\n");
            }
            sb.append("\n");
            return sb.toString();
        }

        @Override
        public String caseStateType(StateType object) {
            StringBuilder sb = new StringBuilder();
            sb.append("StateType {");
            boolean first = true;
            for (State state : object.getStates()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(doSwitch(state));
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public String caseState(State object) {
            return object.getName() + "(" + object.getValue() + ")";
        }

        @Override
        public String caseBufferType(BufferType object) {
            StringBuilder sb = new StringBuilder();
            sb.append("buffer ");
            sb.append(doSwitch(object.getSize()));
            sb.append(" of {");
            boolean first = true;
            for (Type type : object.getType()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(doSwitch(type));
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public String caseBitType(BitType object) {
            return "Bit";
        }

        @Override
        public String defaultCase(EObject object) {
            DVE.transformations.PrettyPrinter pp = new DVE.transformations.PrettyPrinter() {
                @Override
                public String defaultCase(EObject object) {
                    return modelSwitch.doSwitch(object);
                }
            };
            return pp.doSwitch(object);
        }
    };
}
