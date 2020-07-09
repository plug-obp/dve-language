package sdve.transformations;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import SDVE.model.ModelFactory;
import SDVE.model.System;
import SDVE.model.Transition;
import SDVE.model.*;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class BufferedChannelFlattening {
    ModelFactory sdveFactory = ModelFactory.eINSTANCE;
    DVE.model.ModelFactory dveFactory = DVE.model.ModelFactory.eINSTANCE;
    System originalSystem;
    System flatSystem;

    public static System flatten(System system) {
        return new BufferedChannelFlattening(system).flattenBufferedChannels();
    }

    public BufferedChannelFlattening(System system) {
        this.originalSystem = system;
        this.flatSystem = sdveFactory.createSystem();
    }

    public System flattenBufferedChannels() {
        Map<ChannelDeclaration, VariableDeclaration> channel2variable = new IdentityHashMap<>();

        while (!originalSystem.getTransitions().isEmpty()) {
            Transition currentTransition = (Transition)originalSystem.getTransitions().remove(0);

            if (currentTransition.getSync() == null) {
                //if no synchronization just add the transition to the system
                flatSystem.getTransitions().add(currentTransition);
                continue;
            }
            ChannelDeclaration channelDeclaration = currentTransition.getSync().getChannel().getRef();
            if (isSynchronous(channelDeclaration)) {
                //synchronous channel are not handled by this transformations
                flatSystem.getTransitions().add(currentTransition);
                continue;
            }

            VariableDeclaration bufferVariable = channel2variable.get(channelDeclaration);
            if (bufferVariable == null) {
                channel2variable.put(channelDeclaration, bufferVariable = dveFactory.createVariableDeclaration());
                bufferVariable.setName(channelDeclaration.getName());

                TypedChannelDeclaration typedChannelDeclaration = (TypedChannelDeclaration) channelDeclaration;

                BufferType bufferType = sdveFactory.createBufferType();
                bufferType.setSize(EcoreUtil.copy(typedChannelDeclaration.getBufferSize()));
                if (typedChannelDeclaration.getTypes().size() == 1) {
                    bufferType.setType(EcoreUtil.copy(typedChannelDeclaration.getTypes().get(0)));
                } else {
                    TupleType tupleType = sdveFactory.createTupleType();
                    tupleType.getTypes().addAll(EcoreUtil.copyAll(typedChannelDeclaration.getTypes()));
                    bufferType.setType(tupleType);
                }
                bufferVariable.setType(bufferType);
                flatSystem.getDeclarations().add(bufferVariable);
            }
            VariableReference reference = dveFactory.createVariableReference();
            reference.setRefName(bufferVariable.getName());
            reference.setRef(bufferVariable);

            if (currentTransition.getSync() instanceof InputSynchronization) {
                BufferIsEmpty bufferIsEmpty = sdveFactory.createBufferIsEmpty();
                VariableReference bufferReference = dveFactory.createVariableReference();
                bufferReference.setRefName(bufferVariable.getName());
                bufferReference.setRef(bufferVariable);
                bufferIsEmpty.setBuffer(bufferReference);

                UnaryExpression notEmpty = dveFactory.createUnaryExpression();
                notEmpty.setOperator(UnaryOperator.NOT);
                notEmpty.setOperand(bufferIsEmpty);

                BinaryExpression guard = dveFactory.createBinaryExpression();
                guard.setOperator(BinaryOperator.AND);
                guard.getOperands().add(currentTransition.getGuard());
                guard.getOperands().add(notEmpty);
                currentTransition.setGuard(guard);

                BufferRead readBuffer = sdveFactory.createBufferRead();
                readBuffer.setBuffer(reference);

                Assignment readAssignement = dveFactory.createAssignment();
                readAssignement.setLhs(currentTransition.getSync().getValue());
                readAssignement.setRhs(readBuffer);

                currentTransition.setSync(null);
                List<Assignment> effects = new ArrayList<>(currentTransition.getEffect());
                currentTransition.getEffect().clear();
                currentTransition.getEffect().add(readAssignement);
                currentTransition.getEffect().addAll(effects);
            }
            else if (currentTransition.getSync() instanceof OutputSynchronization) {
                BufferIsFull bufferIsFull = sdveFactory.createBufferIsFull();
                VariableReference bufferReference = dveFactory.createVariableReference();
                bufferReference.setRefName(bufferVariable.getName());
                bufferReference.setRef(bufferVariable);
                bufferIsFull.setBuffer(bufferReference);

                UnaryExpression notFull = dveFactory.createUnaryExpression();
                notFull.setOperator(UnaryOperator.NOT);
                notFull.setOperand(bufferIsFull);

                BinaryExpression guard = dveFactory.createBinaryExpression();
                guard.setOperator(BinaryOperator.AND);
                guard.getOperands().add(currentTransition.getGuard());
                guard.getOperands().add(notFull);
                currentTransition.setGuard(guard);

                BufferWrite writeBuffer = sdveFactory.createBufferWrite();
                writeBuffer.setBuffer(reference);
                writeBuffer.setValue(currentTransition.getSync().getValue());
                currentTransition.setSync(null);
                currentTransition.getEffect().add(writeBuffer);
            }
            else throw new RuntimeException("unexpected channel type");
            flatSystem.getTransitions().add(currentTransition);
        }

        while (!originalSystem.getDeclarations().isEmpty()) {
            NamedDeclaration decl = originalSystem.getDeclarations().remove(0);
            if ( (decl instanceof ChannelDeclaration) && !isSynchronous((ChannelDeclaration)decl)){
                continue;
            }
            flatSystem.getDeclarations().add(decl);
        }

        flatSystem.setProperties(originalSystem.getProperties());

        while (!originalSystem.getAcceptingConditions().isEmpty()) {
            Expression cond = originalSystem.getAcceptingConditions().remove(0);
            flatSystem.getAcceptingConditions().add(cond);
        }

        return flatSystem;
    }

    boolean isSynchronous(ChannelDeclaration channelDeclaration) {
        if (channelDeclaration instanceof TypedChannelDeclaration) {
            TypedChannelDeclaration typedChannelDeclaration = (TypedChannelDeclaration) channelDeclaration;
            if (typedChannelDeclaration.getBufferSize() == null) return true; // assume 0 buffer size, so synchronization
            NumberLiteral bufferSize = (NumberLiteral) StaticEvaluator.evaluate(typedChannelDeclaration.getBufferSize());
            return bufferSize.getValue().intValue() == 0;
        }
        return true;
    }
}
