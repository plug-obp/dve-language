package sdve.transformations;

import DVE.model.ModelFactory;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import SDVE.model.System;
import SDVE.model.Transition;
import SDVE.model.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import sdve.extractions.TypeInference;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FlattenTransitions {

    public static System flatten(System originalSystem) {
        return (System)new FlattenTransitions().sdveModelSwitch.doSwitch(originalSystem);
    }

    SDVE.model.ModelFactory sdveModelFactory = SDVE.model.ModelFactory.eINSTANCE;
    ModelFactory dveModelFactory = ModelFactory.eINSTANCE;

    Stack<Type> typingContext = new Stack<>();
    int id = 0;

    System system = sdveModelFactory.createSystem();

    VariableReference createTemp(Type type) {
        TransientVariableDeclaration transientVariableDeclaration = sdveModelFactory.createTransientVariableDeclaration();
        transientVariableDeclaration.setName("t_"+id);
        transientVariableDeclaration.setType(type);

        system.getDeclarations().add(transientVariableDeclaration);
        VariableReference reference = dveModelFactory.createVariableReference();
        reference.setRefName("t_"+id++);
        reference.setRef(transientVariableDeclaration);
        return reference;
    }

    List<Assignment> currentEffectBlock = null;

    SDVE.model.util.ModelSwitch<EObject> sdveModelSwitch = new SDVE.model.util.ModelSwitch<EObject>() {

        @Override
        public EObject caseSystem(System object) {


            system.getDeclarations().addAll(object.getDeclarations());

            system.getAcceptingConditions().addAll(object.getAcceptingConditions());

            system.setProperties(object.getProperties());

            for (AbstractTransition transition : object.getTransitions()) {
                system.getTransitions().add((FlatTransition)doSwitch(transition));
            }

            return system;
        }

        @Override
        public EObject caseTransition(Transition object) {
            FlatTransition flatTransition = sdveModelFactory.createFlatTransition();
            flatTransition.setProcess(object.getProcess());

            assert(object.getSync() == null);

            currentEffectBlock = new ArrayList<>();
            typingContext.push(sdveModelFactory.createBitType());
            flatTransition.setGuard((VariableReference)doSwitch(object.getGuard()));
            flatTransition.getGuardBlock().addAll(currentEffectBlock);

            typingContext.pop();

            currentEffectBlock = new ArrayList<>();
            for (Assignment assignment : object.getEffect()) {
                currentEffectBlock.add((Assignment) doSwitch(assignment));
            }
            flatTransition.getEffect().addAll(currentEffectBlock);
            currentEffectBlock = null;

            return flatTransition;
        }

        @Override
        public EObject caseFlatTransition(FlatTransition object) {
            throw new RuntimeException("Unexpected Flat Transition during TransitionFlattening");
        }

        @Override
        public EObject caseBufferWrite(BufferWrite object) {
            typingContext.push(object.getBuffer().getRef().getType());
            Expression reference = (Expression) doSwitch(object.getValue());
            object.setValue(reference);
            typingContext.pop();
            return object;
        }

        @Override
        public EObject caseBufferRead(BufferRead object) {
            return object;
        }

        @Override
        public EObject caseBufferIsEmpty(BufferIsEmpty object) {
            return object;
        }

        @Override
        public EObject caseBufferIsFull(BufferIsFull object) {
            return object;
        }

        @Override
        public EObject defaultCase(EObject object) {
            return dveModelSwitch.doSwitch(object);
        }
    };
    ModelSwitch<EObject> dveModelSwitch = new ModelSwitch<EObject>() {
        boolean inLhs = false;
        @Override
        public EObject caseAssignment(Assignment object) {
            inLhs = true;
            object.setLhs((Expression) doSwitch(object.getLhs()));
            inLhs = false;
            object.setRhs((Expression) doSwitch(object.getRhs()));
            return object;
        }

        @Override
        public EObject caseVariableReference(VariableReference object) {
            return object;
        }

        @Override
        public EObject caseUnaryExpression(UnaryExpression object) {
            Expression operand = (Expression) doSwitch(object.getOperand());

            UnaryExpression expression = dveModelFactory.createUnaryExpression();
            expression.setOperator(object.getOperator());
            expression.setOperand(operand);

            VariableReference temp = createTemp(TypeInference.getType(expression));

            Assignment assignment  = dveModelFactory.createAssignment();
            assignment.setLhs(temp);
            assignment.setRhs(expression);

            currentEffectBlock.add(assignment);

            return EcoreUtil.copy(temp);
        }

        @Override
        public EObject caseBinaryExpression(BinaryExpression object) {
            Expression operand0 = (Expression) doSwitch(object.getOperands().get(0));
            Expression operand1 = (Expression) doSwitch(object.getOperands().get(1));

            BinaryExpression expression = dveModelFactory.createBinaryExpression();
            expression.setOperator(object.getOperator());
            expression.getOperands().add(operand0);
            expression.getOperands().add(operand1);

            Type type = TypeInference.getType(expression);
            if (type == null) {
                java.lang.System.out.println("error");
            }

            VariableReference temp = createTemp(type);
            Assignment assignment = dveModelFactory.createAssignment();
            assignment.setLhs(temp);
            assignment.setRhs(expression);

            currentEffectBlock.add(assignment);

            return EcoreUtil.copy(temp);
        }

        @Override
        public EObject caseIndexedExpression(IndexedExpression object) {
            Expression index = (Expression) doSwitch(object.getIndex());
            Expression base = (Expression) doSwitch(object.getBase());

            IndexedExpression expression = dveModelFactory.createIndexedExpression();
            expression.setIndex(index);
            expression.setBase(base);

            if (inLhs == true) {
                return expression;
            }

            VariableReference temp = createTemp(TypeInference.getType(expression));
            Assignment assignment = dveModelFactory.createAssignment();
            assignment.setLhs(temp);
            assignment.setRhs(expression);

            currentEffectBlock.add(assignment);

            return EcoreUtil.copy(temp);
        }

        @Override
        public EObject caseLiteral(Literal object) {
            return object;
        }

        @Override
        public EObject defaultCase(EObject object) {
            if (object instanceof SElement) {
                return sdveModelSwitch.doSwitch(object);
            }
            return null;
        }
    };

}
