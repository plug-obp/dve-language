package DVE.transformations;

import DVE.evaluation.StaticEvaluator;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Simplifies all  static expressions by replacing them with literals
 */
public class StaticSimplifier {
    public static System simplify(System system) {
        StaticSimplifier simplifier = new StaticSimplifier();
        return (System) simplifier.modelSwitch.doSwitch(system);
    }
    ModelSwitch<EObject> modelSwitch = new ModelSwitch<EObject>() {
        Map<EObject, EObject> map = new IdentityHashMap<>();
        ModelFactory factory = ModelFactory.eINSTANCE;
        @Override
        public EObject caseSystem(System object) {
            EObject node = map.get(object);
            if (node != null) return node;
            System element = factory.createSystem();
            map.put(object, element);

            for (EObject decl : object.getDeclarations()) {
                element.getDeclarations().add((NamedDeclaration) doSwitch(decl));
            }

            for (EObject process : object.getProcesses()) {
                element.getProcesses().add((Process) doSwitch(process));
            }

            element.setProperties((SystemProperties) doSwitch(object.getProperties()));

            element.setName(object.getName());

            return element;
        }

        @Override
        public EObject caseVariableDeclaration(VariableDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableDeclaration element = factory.createVariableDeclaration();
            map.put(object, element);

            element.setName(object.getName());
            element.setType((Type) doSwitch(object.getType()));
            if (object.getInitial() != null) {
                Expression result = StaticEvaluator.evaluate(object.getInitial());
                if (result != null) {
                    element.setInitial(result);
                } else {
                    element.setInitial((Expression) doSwitch(object.getInitial()));
                }
            }

            return element;
        }

        @Override
        public EObject caseConstantDeclaration(ConstantDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ConstantDeclaration element = factory.createConstantDeclaration();
            map.put(object, element);

            element.setName(object.getName());
            element.setType((Type) doSwitch(object.getType()));
            Expression result = StaticEvaluator.evaluate(object.getInitial());
            if (result != null) {
                element.setInitial(result);
            } else {
                element.setInitial((Expression) doSwitch(object.getInitial()));
            }

            return element;
        }

        @Override
        public EObject caseChannelDeclaration(ChannelDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ChannelDeclaration element = factory.createChannelDeclaration();
            map.put(object, element);

            element.setName(object.getName());

            return element;
        }

        @Override
        public EObject caseTypedChannelDeclaration(TypedChannelDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            TypedChannelDeclaration element = factory.createTypedChannelDeclaration();
            map.put(object, element);

            element.setName(object.getName());
            for (EObject type : object.getTypes()) {
                element.getTypes().add((Type)doSwitch(type));
            }

            Expression result = StaticEvaluator.evaluate(object.getBufferSize());
            if (result != null) {
                element.setBufferSize(result);
            } else {
                element.setBufferSize((Expression) doSwitch(object.getBufferSize()));
            }

            return element;
        }

        @Override
        public EObject caseProcess(Process object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Process element = factory.createProcess();
            map.put(object, element);

            element.setName(object.getName());
            element.setSystem((System) doSwitch(object.getSystem()));
            for (EObject state : object.getStates()) {
                element.getStates().add((State)doSwitch(state));
            }
            for (EObject stateReference : object.getCommited()) {
                element.getCommited().add((StateReference) doSwitch(stateReference));
            }
            for (EObject stateReference : object.getAccepting()) {
                element.getAccepting().add((StateReference) doSwitch(stateReference));
            }
            element.setInitial((StateReference) doSwitch(object.getInitial()));

            for (EObject declaration : object.getDeclarations()) {
                element.getDeclarations().add((NamedDeclaration) doSwitch(declaration));
            }

            for (EObject transition : object.getTransitions()) {
                element.getTransitions().add((Transition) doSwitch(transition));
            }

            return element;
        }

        @Override
        public EObject caseTransition(Transition object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Transition element = factory.createTransition();
            map.put(object, element);

            element.setFrom((StateReference) doSwitch(object.getFrom()));
            element.setTo ((StateReference) doSwitch(object.getTo()));
            if (object.getSync() != null) {
                element.setSync((Synchronization) doSwitch(object.getSync()));
            }

            if (object.getGuard() != null) {
                Expression result = StaticEvaluator.evaluate(object.getGuard());
                if (result != null) {
                    element.setGuard(result);
                } else {
                    element.setGuard((Expression) doSwitch(object.getGuard()));
                }
            }

            for (EObject assignement : object.getEffect()) {
                element.getEffect().add((Assignment) doSwitch(assignement));
            }

            return element;
        }

        @Override
        public EObject caseVariableReference(VariableReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableReference element = factory.createVariableReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((VariableDeclaration) doSwitch(object.getRef()));

            return element;
        }

        @Override
        public EObject caseState(State object) {
            EObject node = map.get(object);
            if (node != null) return node;
            State element = factory.createState();
            map.put(object, element);

            element.setName(object.getName());

            return element;
        }

        @Override
        public EObject caseStateReference(StateReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            StateReference element = factory.createStateReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((State) doSwitch(object.getRef()));

            return element;
        }

        @Override
        public EObject caseProcessStateReference(ProcessStateReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ProcessStateReference element = factory.createProcessStateReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((State) doSwitch(object.getRef()));
            element.setPrefix((ProcessReference) doSwitch(object.getPrefix()));

            return element;
        }

        @Override
        public EObject caseProcessReference(ProcessReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ProcessReference element = factory.createProcessReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((Process) doSwitch(object.getRef()));

            return element;
        }

        @Override
        public EObject caseChannelReference(ChannelReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ChannelReference element = factory.createChannelReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((ChannelDeclaration) doSwitch(object.getRef()));

            return element;
        }

        @Override
        public EObject caseByteType(ByteType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ByteType element = factory.createByteType();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseIntegerType(IntegerType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            IntegerType element = factory.createIntegerType();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseArrayType(ArrayType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ArrayType element = factory.createArrayType();
            map.put(object, element);

            element.setElementType((Type) doSwitch(object.getElementType()));

            Expression result = StaticEvaluator.evaluate(object.getSize());
            if (result != null) {
                element.setSize(result);
            } else {
                element.setSize((Expression) doSwitch(object.getSize()));
            }

            return element;
        }

        @Override
        public EObject caseTrueLiteral(TrueLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            TrueLiteral element = factory.createTrueLiteral();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseFalseLiteral(FalseLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            FalseLiteral element = factory.createFalseLiteral();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseNumberLiteral(NumberLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            NumberLiteral element = factory.createNumberLiteral();
            map.put(object, element);

            element.setValue(object.getValue());

            return element;
        }

        @Override
        public EObject caseArrayLiteral(ArrayLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ArrayLiteral element = factory.createArrayLiteral();
            map.put(object, element);

            for (Expression value : object.getValues()) {
                Expression result = StaticEvaluator.evaluate(EcoreUtil.copy(value));
                if (result != null) {
                    element.getValues().add(result);
                } else {
                    element.getValues().add((Expression) doSwitch(value));
                }
            }

            return element;
        }

        @Override
        public EObject caseAssignment(Assignment object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Assignment element = factory.createAssignment();
            map.put(object, element);

            // lhs can be simplified, but it cannot evaluate to a literal - it evaluate to an address
            element.setLhs((Expression) doSwitch(object.getLhs()));

            Expression result = StaticEvaluator.evaluate(object.getRhs());
            if (result != null) {
                element.setRhs(result);
            } else {
                element.setRhs((Expression) doSwitch(object.getRhs()));
            }

            return element;
        }

        @Override
        public EObject caseIndexedExpression(IndexedExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            IndexedExpression element = factory.createIndexedExpression();
            map.put(object, element);

            Expression result = StaticEvaluator.evaluate(object.getBase());
            if (result != null) {
                element.setBase(result);
            } else {
                element.setBase((Expression) doSwitch(object.getBase()));
            }

            result = StaticEvaluator.evaluate(object.getIndex());
            if (result != null) {
                element.setIndex(result);
            } else {
                element.setIndex((Expression) doSwitch(object.getIndex()));
            }

            return element;
        }

        @Override
        public EObject caseAsynchronous(Asynchronous object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Asynchronous element =factory.createAsynchronous();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseSynchronous(Synchronous object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Synchronous element =factory.createSynchronous();

            map.put(object, element);
            return element;
        }

        @Override
        public EObject caseInputSynchronization(InputSynchronization object) {
            EObject node = map.get(object);
            if (node != null) return node;
            InputSynchronization element =factory.createInputSynchronization();
            map.put(object, element);

            element.setChannel((ChannelReference) doSwitch(object.getChannel()));

            if (object.getValue() != null) {
                Expression result = StaticEvaluator.evaluate(object.getValue());
                if (result != null) {
                    element.setValue(result);
                } else {
                    element.setValue((Expression) doSwitch(object.getValue()));
                }
            }

            return element;
        }

        @Override
        public EObject caseOutputSynchronization(OutputSynchronization object) {
            EObject node = map.get(object);
            if (node != null) return node;
            OutputSynchronization element =factory.createOutputSynchronization();
            map.put(object, element);

            element.setChannel((ChannelReference) doSwitch(object.getChannel()));

            if (object.getValue() != null) {
                Expression result = StaticEvaluator.evaluate(object.getValue());
                if (result != null) {
                    element.setValue(result);
                } else {
                    element.setValue((Expression) doSwitch(object.getValue()));
                }
            }

            return element;
        }

        @Override
        public EObject caseProcessVariableReference(ProcessVariableReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ProcessVariableReference element = factory.createProcessVariableReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((VariableDeclaration) doSwitch(object.getRef()));
            element.setPrefix((ProcessReference) doSwitch(object.getPrefix()));

            return element;
        }

        @Override
        public EObject caseUnaryExpression(UnaryExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            UnaryExpression element = factory.createUnaryExpression();
            map.put(object, element);

            element.setOperator(object.getOperator());

            Expression result = StaticEvaluator.evaluate(object.getOperand());
            if (result != null) {
                element.setOperand(result);
            } else {
                element.setOperand((Expression) doSwitch(object.getOperand()));
            }

            return element;
        }

        @Override
        public EObject caseBinaryExpression(BinaryExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            BinaryExpression element = factory.createBinaryExpression();
            map.put(object, element);

            element.setOperator(object.getOperator());

            for (Expression expression : object.getOperands()) {
                Expression result = StaticEvaluator.evaluate(EcoreUtil.copy(expression));
                if (result != null) {
                    element.getOperands().add(result);
                } else {
                    element.getOperands().add((Expression) doSwitch(expression));
                }
            }

            return element;
        }

        @Override
        public EObject caseSystemProperties(SystemProperties object) {
            EObject node = map.get(object);
            if (node != null) return node;
            SystemProperties element = factory.createSystemProperties();
            map.put(object, element);

            if (object.getProperty() != null) {
                element.setProperty((ProcessReference) doSwitch(object.getProperty()));
            }
            element.setSystemType((SystemType) doSwitch(object.getSystemType()));

            return element;
        }
    };
}
