package sdve.transformations;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import SDVE.model.ModelFactory;
import SDVE.model.System;
import SDVE.model.TransientVariableDeclaration;
import SDVE.model.Transition;
import sdve.extractions.ChannelSchedule;

import java.util.IdentityHashMap;
import java.util.Map;

public class SynchronizationFlattening {
    ModelFactory sdveFactory = ModelFactory.eINSTANCE;
    DVE.model.ModelFactory dveFactory = DVE.model.ModelFactory.eINSTANCE;
    System originalSystem;
    System flatSystem;
    TransientVariableDeclaration currentChannelVariableDecl;

    public SynchronizationFlattening(System system) {
        this.originalSystem = system;
        this.flatSystem = sdveFactory.createSystem();
    }

    public System flattenSynchronizations() {
        Map<ChannelDeclaration, ChannelSchedule> channelScheduleMap = new IdentityHashMap<>();

        while (!originalSystem.getTransitions().isEmpty()) {
            Transition currentTransition = originalSystem.getTransitions().remove(0);

            if (currentTransition.getSync() == null) {
                //if no synchronization just add the transition to the system
                flatSystem.getTransitions().add(currentTransition);
                continue;
            }
            ChannelDeclaration channelDeclaration = currentTransition.getSync().getChannel().getRef();
            ChannelSchedule schedule = channelScheduleMap.get(channelDeclaration);
            if (schedule == null) {
                channelScheduleMap.put(channelDeclaration, schedule = new ChannelSchedule(channelDeclaration));
            }
            if (!isSynchronous(channelDeclaration)) {
                throw new RuntimeException("Buffered channels have to be removed before flattening synchronizations");
            }
            //valueless channel
            if (currentTransition.getSync() instanceof InputSynchronization) {
                schedule.addInput(currentTransition);
            }
            else if (currentTransition.getSync() instanceof OutputSynchronization) {
                schedule.addOutput(currentTransition);
            }
            else throw new RuntimeException("unexpected channel type");
        }

        for (Map.Entry<ChannelDeclaration, ChannelSchedule> entry : channelScheduleMap.entrySet()) {
            currentChannelVariableDecl = null;
            if (false /*typed channel*/) {
                ChannelDeclaration channelDeclaration = entry.getKey();
                currentChannelVariableDecl = sdveFactory.createTransientVariableDeclaration();
                currentChannelVariableDecl.setName(channelDeclaration.getName());

                flatSystem.getDeclarations().add(currentChannelVariableDecl);
            }

            entry.getValue().handleSynchronousActions(this::synchronousActionHandler);
        }

        while (!originalSystem.getDeclarations().isEmpty()) {
            NamedDeclaration decl = originalSystem.getDeclarations().remove(0);
            flatSystem.getDeclarations().add(decl);
        }

        flatSystem.setProperties(originalSystem.getProperties());

        while (!originalSystem.getAcceptingConditions().isEmpty()) {
            Expression cond = originalSystem.getAcceptingConditions().remove(0);
            flatSystem.getAcceptingConditions().add(cond);
        }

        return null;
    }

    public void synchronousActionHandler(Transition[] transitions) {
        //if the input and output are in the same process do not synchronize
        if (transitions[0].getProcess().equals(transitions[1].getProcess())) {
            return;
        }

        //it is a synchronous channel with data exchange
        if (currentChannelVariableDecl != null) {
            //write the data to the channel variable
            VariableReference variableReference = dveFactory.createVariableReference();
            variableReference.setRef(currentChannelVariableDecl);

            Assignment writeAssignement = dveFactory.createAssignment();
            writeAssignement.setLhs(variableReference);
            writeAssignement.setRhs(transitions[0].getSync().getValue());

            transitions[0].getEffect().add(writeAssignement);

            //write the data to the slot given by the input transition
            variableReference = dveFactory.createVariableReference();
            variableReference.setRef(currentChannelVariableDecl);

            Assignment readAssignement = dveFactory.createAssignment();
            readAssignement.setLhs(transitions[1].getSync().getValue());
            readAssignement.setRhs(variableReference);

            transitions[1].getEffect().add(readAssignement);
        }

        Transition compositeTransition = sdveFactory.createTransition();

        compositeTransition.setSync(null);

        BinaryExpression compositeGuard = dveFactory.createBinaryExpression();
        compositeGuard.setOperator(BinaryOperator.AND);
        compositeGuard.getOperands().add(transitions[0].getGuard());
        compositeGuard.getOperands().add(transitions[1].getGuard());
        compositeTransition.setGuard(compositeGuard);

        compositeTransition.getEffect().addAll(transitions[0].getEffect());
        compositeTransition.getEffect().addAll(transitions[1].getEffect());

        flatSystem.getTransitions().add(compositeTransition);
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
