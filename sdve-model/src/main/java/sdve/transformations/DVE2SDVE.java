package sdve.transformations;


import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import SDVE.model.StateType;
import org.eclipse.emf.ecore.EObject;

import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

public class DVE2SDVE {

    public static SDVE.model.System transform(System system) {
        DVE2SDVE dve2SDVE = new DVE2SDVE();
        return (SDVE.model.System)dve2SDVE.modelSwitch.doSwitch(system);
    }

    ModelSwitch<EObject> modelSwitch = new ModelSwitch<EObject>() {
        Map<EObject, EObject> map = new IdentityHashMap<>();
        Stack<EObject> context = new Stack<>();
        ModelFactory dveFactory = ModelFactory.eINSTANCE;
        SDVE.model.ModelFactory sdveFactory = SDVE.model.ModelFactory.eINSTANCE;
        @Override
        public EObject caseSystem(System object) {
            EObject node = map.get(object);
            if (node != null) return node;
            SDVE.model.System element = sdveFactory.createSystem();
            map.put(object, element);

            context.push(object);

            for (EObject decl : object.getDeclarations()) {
                if (!(decl instanceof Process)) {
                    element.getDeclarations().add((NamedDeclaration)doSwitch(decl));
                }
            }

            for (EObject process : object.getProcesses()) {
                SDVE.model.System partialSystem = (SDVE.model.System) doSwitch(process);

                while (!partialSystem.getDeclarations().isEmpty()) {
                    element.getDeclarations().add(partialSystem.getDeclarations().remove(0));
                }

                while (!partialSystem.getTransitions().isEmpty()) {
                    element.getTransitions().add(partialSystem.getTransitions().remove(0));
                }

                while (!partialSystem.getAcceptingConditions().isEmpty()) {
                    element.getAcceptingConditions().add(partialSystem.getAcceptingConditions().remove(0));
                }

                assert partialSystem.getProperties() == null;
            }

            element.setProperties((SDVE.model.SystemProperties) doSwitch(object.getProperties()));


            context.pop();
            return element;
        }

        @Override
        public EObject caseVariableDeclaration(VariableDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableDeclaration element = dveFactory.createVariableDeclaration();
            map.put(object, element);

            if (context.peek() instanceof Process) {
                element.setName(((Process) context.peek()).getName() + "." + object.getName());
            } else {
                element.setName(object.getName());
            }
            element.setType((Type) doSwitch(object.getType()));
            if (object.getInitial() != null) {
                element.setInitial((Expression) doSwitch(object.getInitial()));
            }

            return element;
        }

        @Override
        public EObject caseConstantDeclaration(ConstantDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ConstantDeclaration element = dveFactory.createConstantDeclaration();
            map.put(object, element);

            if (context.peek() instanceof Process) {
                element.setName(((Process) context.peek()).getName() + "." + object.getName());
            } else {
                element.setName(object.getName());
            }
            element.setType((Type) doSwitch(object.getType()));

            element.setInitial((Expression) doSwitch(object.getInitial()));

            return element;
        }

        @Override
        public EObject caseChannelDeclaration(ChannelDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ChannelDeclaration element = dveFactory.createChannelDeclaration();
            map.put(object, element);

            element.setName(object.getName());

            return element;
        }

        @Override
        public EObject caseTypedChannelDeclaration(TypedChannelDeclaration object) {
            EObject node = map.get(object);
            if (node != null) return node;
            TypedChannelDeclaration element = dveFactory.createTypedChannelDeclaration();
            map.put(object, element);

            element.setName(object.getName());
            for (EObject type : object.getTypes()) {
                element.getTypes().add((Type)doSwitch(type));
            }

            element.setBufferSize((Expression) doSwitch(object.getBufferSize()));

            return element;
        }

        @Override
        public EObject caseProcess(Process object) {
            EObject node = map.get(object);
            if (node != null) return node;

            //push the process in the context
            context.push(object);


            //each process maps to a partialSystem, which is then merged with the other (see caseSystem)
            SDVE.model.System partialSystem = sdveFactory.createSystem();
            map.put(object, partialSystem);

            //create a state type, for the control state of the process
            StateType stateType = sdveFactory.createStateType();
            //the state type is pushed on the context, so that each state can add itself (see caseState)
            context.push(stateType);
            for (EObject state : object.getStates()) {
               doSwitch(state);
            }
            //remove the state type from the context
            context.pop();

            // declare the control state and its initial value
            VariableDeclaration stateVariableDeclaration = dveFactory.createVariableDeclaration();
            stateVariableDeclaration.setType(stateType);
            stateVariableDeclaration.setName(object.getName() + ".state");
            NumberLiteral initialStateLiteral = dveFactory.createNumberLiteral();
            initialStateLiteral.setValue(((SDVE.model.State) doSwitch(object.getInitial().getRef())).getValue());
            stateVariableDeclaration.setInitial(initialStateLiteral);

            partialSystem.getDeclarations().add(stateVariableDeclaration);

            //for each accepting state generate an accepting condition in the partial system
            for (StateReference stateReference : object.getAccepting()) {
                VariableReference variableReference = dveFactory.createVariableReference();
                variableReference.setRefName(object.getName() + ".state");
                variableReference.setRef(stateVariableDeclaration);
                NumberLiteral numberLiteral = dveFactory.createNumberLiteral();
                numberLiteral.setValue(((SDVE.model.State) doSwitch(stateReference.getRef())).getValue());
                BinaryExpression acceptingCondition = dveFactory.createBinaryExpression();
                acceptingCondition.setOperator(BinaryOperator.EQ);
                acceptingCondition.getOperands().add(variableReference);
                acceptingCondition.getOperands().add(numberLiteral);

                partialSystem.getAcceptingConditions().add(acceptingCondition);
            }

            //for each committed state add the committed flag
            for (StateReference stateReference : object.getCommited()) {
                SDVE.model.State state = (SDVE.model.State)doSwitch(stateReference.getRef());
                state.setCommitted(true);
            }

            if (!object.getCommited().isEmpty()) {
                VariableDeclaration committedVariableDeclaration = dveFactory.createVariableDeclaration();
                committedVariableDeclaration.setType(sdveFactory.createBitType());
                committedVariableDeclaration.setName(object.getName() + ".committed");
                //if the initial state is a committed state, then the committed variable defaults to true
                if (object.getCommited().stream().anyMatch(c-> c.getRef() == object.getInitial().getRef())) {
                    committedVariableDeclaration.setInitial(dveFactory.createTrueLiteral());
                } else {
                    //the initial state is not a committed state
                    committedVariableDeclaration.setInitial(dveFactory.createFalseLiteral());
                }

                partialSystem.getDeclarations().add(committedVariableDeclaration);
            }

            //add all other variable declarations to the partial system
            for (EObject declaration : object.getDeclarations()) {
                partialSystem.getDeclarations().add((NamedDeclaration) doSwitch(declaration));
            }

            //add the STransitions to the partialSystem
            for (EObject transition : object.getTransitions()) {
                partialSystem.getTransitions().add((SDVE.model.Transition) doSwitch(transition));
            }

            context.pop();
            return partialSystem;
        }

        @Override
        public EObject caseTransition(Transition object) {
            EObject node = map.get(object);
            if (node != null) return node;
            SDVE.model.Transition element = sdveFactory.createTransition();
            map.put(object, element);

            Process process = (Process) context.peek();

            element.setProcess(process.getName());

            //copy the synchronization
            if (object.getSync() != null) {
                element.setSync((Synchronization) doSwitch(object.getSync()));
            }
            BinaryExpression guardPrefix;
            //create the guard for the from state
            SDVE.model.State fromState = (SDVE.model.State) doSwitch(object.getFrom().getRef());

            //process_name.state == fromState.value
            VariableReference stateVariableReference = dveFactory.createVariableReference();
            stateVariableReference.setRefName(process.getName() + ".state");
            NumberLiteral numberLiteral = dveFactory.createNumberLiteral();
            numberLiteral.setValue(fromState.getValue());

            BinaryExpression stateGuard = dveFactory.createBinaryExpression();
            stateGuard.setOperator(BinaryOperator.EQ);
            stateGuard.getOperands().add(stateVariableReference);
            stateGuard.getOperands().add(numberLiteral);

            guardPrefix = stateGuard;
            if (!process.getCommited().isEmpty()) {
                //fromState.committed ? process_name.committed == true : process_name.committed == false
                VariableReference committedVariableReference = dveFactory.createVariableReference();
                committedVariableReference.setRefName(process.getName() + ".committed");

                BinaryExpression committedGuard = dveFactory.createBinaryExpression();
                committedGuard.setOperator(BinaryOperator.EQ);
                committedGuard.getOperands().add(committedVariableReference);
                if (fromState.getCommitted()) {
                    committedGuard.getOperands().add(dveFactory.createTrueLiteral());
                } else {
                    committedGuard.getOperands().add(dveFactory.createFalseLiteral());
                }

                guardPrefix = dveFactory.createBinaryExpression();
                guardPrefix.setOperator(BinaryOperator.AND);
                guardPrefix.getOperands().add(stateGuard);
                guardPrefix.getOperands().add(committedGuard);
            }

            if (object.getGuard() != null) {
                BinaryExpression guard = dveFactory.createBinaryExpression();

                guard.setOperator(BinaryOperator.AND);
                guard.getOperands().add(guardPrefix);
                guard.getOperands().add((Expression) doSwitch(object.getGuard()));

                element.setGuard(guard);
            } else {
                element.setGuard(guardPrefix);
            }

            //get the toState
            SDVE.model.State toState = (SDVE.model.State) doSwitch(object.getTo().getRef());

            //prepend state change
            stateVariableReference = dveFactory.createVariableReference();
            stateVariableReference.setRefName(process.getName() + ".state");

            numberLiteral = dveFactory.createNumberLiteral();
            numberLiteral.setValue(toState.getValue());

            Assignment assignment = dveFactory.createAssignment();
            assignment.setLhs(stateVariableReference);
            assignment.setRhs(numberLiteral);

            element.getEffect().add(assignment);

            //fromState is committed ? process_name.committed := true : process_name.committed := false
            if (!process.getCommited().isEmpty()) {
                VariableReference committedVariableReference = dveFactory.createVariableReference();
                committedVariableReference.setRefName(process.getName() + ".committed");

                assignment = dveFactory.createAssignment();
                assignment.setLhs(committedVariableReference);

                if (toState.getCommitted()) {
                    assignment.setRhs(dveFactory.createTrueLiteral());
                } else {
                    assignment.setRhs(dveFactory.createFalseLiteral());
                }

                element.getEffect().add(assignment);
            }

            //copy effects
            for (EObject assignement : object.getEffect()) {
                element.getEffect().add((Assignment) doSwitch(assignement));
            }

            return element;
        }

        @Override
        public EObject caseVariableReference(VariableReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableReference element = dveFactory.createVariableReference();
            map.put(object, element);

            element.setRef((VariableDeclaration) doSwitch(object.getRef()));
            element.setRefName(object.getRef().getName());

            return element;
        }

        @Override
        public EObject caseState(State object) {
            EObject node = map.get(object);
            if (node != null) return node;
            StateType stateType = (StateType) context.peek();

            for (SDVE.model.State s : stateType.getStates()) {
                if (s.getName().equals(object.getName())) {
                    map.put(object, s);
                    return s;
                }
            }

            SDVE.model.State element = sdveFactory.createState();
            map.put(object, element);

            element.setName(object.getName());
            element.setValue(BigInteger.valueOf(stateType.getStates().size()));
            //by default the state is not committed, the flag might be set by the process (see caseProcess)
            element.setCommitted(false);
            stateType.getStates().add(element);

            return element;
        }

        //DONE -- state references are replaced by a vr = state_value
        @Override
        public EObject caseStateReference(StateReference object) {
            throw new RuntimeException("Unexpected state reference -- they should be already be replaced");
        }

        //DONE -- process states references are replaced by a variable reference to the variable declaration corresponding to the process state
        @Override
        public EObject caseProcessStateReference(ProcessStateReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableReference variableReference = dveFactory.createVariableReference();
            variableReference.setRefName(object.getPrefix().getRefName() + ".state");

            NumberLiteral literal = dveFactory.createNumberLiteral();
            literal.setValue(((SDVE.model.State) doSwitch(object.getRef())).getValue());

            BinaryExpression expression = dveFactory.createBinaryExpression();
            expression.getOperands().add(variableReference);
            expression.setOperator(BinaryOperator.EQ);
            expression.getOperands().add(literal);

            map.put(object, expression);

            return expression;
        }

        @Override
        public EObject caseProcessReference(ProcessReference object) {
            throw new RuntimeException("Unexpected process reference -- they should be already be replaced by the process name");
        }

        @Override //TODO
        public EObject caseChannelReference(ChannelReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ChannelReference element = dveFactory.createChannelReference();
            map.put(object, element);

            element.setRefName(object.getRefName());
            element.setRef((ChannelDeclaration) doSwitch(object.getRef()));

            return element;
        }

        @Override //DONE
        public EObject caseByteType(ByteType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ByteType element = dveFactory.createByteType();

            map.put(object, element);
            return element;
        }

        @Override //DONE
        public EObject caseIntegerType(IntegerType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            IntegerType element = dveFactory.createIntegerType();

            map.put(object, element);
            return element;
        }

        @Override //DONE
        public EObject caseArrayType(ArrayType object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ArrayType element = dveFactory.createArrayType();
            map.put(object, element);

            element.setElementType((Type) doSwitch(object.getElementType()));
            element.setSize((Expression) doSwitch(object.getSize()));

            return element;
        }

        @Override //DONE
        public EObject caseTrueLiteral(TrueLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            TrueLiteral element = dveFactory.createTrueLiteral();

            map.put(object, element);
            return element;
        }

        @Override //DONE
        public EObject caseFalseLiteral(FalseLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            FalseLiteral element = dveFactory.createFalseLiteral();

            map.put(object, element);
            return element;
        }

        @Override //DONE
        public EObject caseNumberLiteral(NumberLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            NumberLiteral element = dveFactory.createNumberLiteral();
            map.put(object, element);

            element.setValue(object.getValue());

            return element;
        }

        @Override //DONE
        public EObject caseArrayLiteral(ArrayLiteral object) {
            EObject node = map.get(object);
            if (node != null) return node;
            ArrayLiteral element = dveFactory.createArrayLiteral();
            map.put(object, element);

            for (Expression value : object.getValues()) {
                element.getValues().add((Expression) doSwitch(value));
            }

            return element;
        }

        @Override //DONE
        public EObject caseAssignment(Assignment object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Assignment element = dveFactory.createAssignment();
            map.put(object, element);

            // lhs can be simplified, but it cannot evaluate to a literal - it evaluate to an address
            element.setLhs((Expression) doSwitch(object.getLhs()));
            element.setRhs((Expression) doSwitch(object.getRhs()));

            return element;
        }

        @Override //DONE
        public EObject caseIndexedExpression(IndexedExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            IndexedExpression element = dveFactory.createIndexedExpression();
            map.put(object, element);

            element.setBase((Expression) doSwitch(object.getBase()));

            element.setIndex((Expression) doSwitch(object.getIndex()));

            return element;
        }

        @Override //DONE
        public EObject caseAsynchronous(Asynchronous object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Asynchronous element = dveFactory.createAsynchronous();

            map.put(object, element);
            return element;
        }

        @Override //DONE
        public EObject caseSynchronous(Synchronous object) {
            EObject node = map.get(object);
            if (node != null) return node;
            Synchronous element = dveFactory.createSynchronous();

            map.put(object, element);
            return element;
        }

        @Override //TODO -- need chanel schedule
        public EObject caseInputSynchronization(InputSynchronization object) {
            EObject node = map.get(object);
            if (node != null) return node;
            InputSynchronization element = dveFactory.createInputSynchronization();
            map.put(object, element);

            element.setChannel((ChannelReference) doSwitch(object.getChannel()));

            if (object.getValue() != null) {
                element.setValue((Expression) doSwitch(object.getValue()));
            }

            return element;
        }

        @Override //TODO -- need chanel schedule
        public EObject caseOutputSynchronization(OutputSynchronization object) {
            EObject node = map.get(object);
            if (node != null) return node;
            OutputSynchronization element = dveFactory.createOutputSynchronization();
            map.put(object, element);

            element.setChannel((ChannelReference) doSwitch(object.getChannel()));

            if (object.getValue() != null) {
                element.setValue((Expression) doSwitch(object.getValue()));
            }

            return element;
        }

        @Override //DONE
        public EObject caseProcessVariableReference(ProcessVariableReference object) {
            EObject node = map.get(object);
            if (node != null) return node;
            VariableReference element = dveFactory.createVariableReference();
            map.put(object, element);

            element.setRefName(object.getPrefix().getRefName() +"."+ object.getRefName());
            element.setRef((VariableDeclaration) doSwitch(object.getRef()));

            return element;
        }

        @Override //DONE
        public EObject caseUnaryExpression(UnaryExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            UnaryExpression element = dveFactory.createUnaryExpression();
            map.put(object, element);

            element.setOperator(object.getOperator());

            element.setOperand((Expression) doSwitch(object.getOperand()));

            return element;
        }

        @Override //DONE
        public EObject caseBinaryExpression(BinaryExpression object) {
            EObject node = map.get(object);
            if (node != null) return node;
            BinaryExpression element = dveFactory.createBinaryExpression();
            map.put(object, element);

            element.setOperator(object.getOperator());

            for (Expression expression : object.getOperands()) {
                element.getOperands().add((Expression) doSwitch(expression));
            }

            return element;
        }

        @Override //DONE
        public EObject caseSystemProperties(SystemProperties object) {
            EObject node = map.get(object);
            if (node != null) return node;
            SDVE.model.SystemProperties element = sdveFactory.createSystemProperties();
            map.put(object, element);

            if (object.getProperty() != null) {
                element.setProperty(object.getProperty().getRefName());
            }
            element.setSystemType((SystemType) doSwitch(object.getSystemType()));

            return element;
        }
    };
}
