package sdve.transformations;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import SDVE.model.*;
import SDVE.model.ModelFactory;
import SDVE.model.System;
import SDVE.model.Transition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import sdve.extractions.ChannelSchedule;

import java.util.IdentityHashMap;
import java.util.Map;

public class SynchronizationFlattening {
    ModelFactory sdveFactory = ModelFactory.eINSTANCE;
    DVE.model.ModelFactory dveFactory = DVE.model.ModelFactory.eINSTANCE;
    System originalSystem;
    System flatSystem;
    TransientVariableDeclaration currentChannelVariableDecl;

    public static System flatten(System system) {
        return new SynchronizationFlattening(system).flattenSynchronizations();
    }

    public SynchronizationFlattening(System system) {
        this.originalSystem = system;
        this.flatSystem = sdveFactory.createSystem();
    }

    public System flattenSynchronizations() {
        Map<ChannelDeclaration, ChannelSchedule> channelScheduleMap = new IdentityHashMap<>();

        while (!originalSystem.getTransitions().isEmpty()) {
            Transition currentTransition = (Transition) originalSystem.getTransitions().remove(0);

            if (currentTransition.getSync() == null) {
                //if no synchronization just add the transition to the system
                flatSystem.getTransitions().add(currentTransition);
                continue;
            }
            ChannelDeclaration channelDeclaration = currentTransition.getSync().getChannel().getRef();
            if (!isSynchronous(channelDeclaration)) {
                throw new RuntimeException("Buffered channels have to be removed before flattening synchronizations");
            }

            ChannelSchedule schedule = channelScheduleMap.get(channelDeclaration);
            if (schedule == null) {
                channelScheduleMap.put(channelDeclaration, schedule = new ChannelSchedule(channelDeclaration));
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
            if (entry.getKey() instanceof TypedChannelDeclaration /*typed channel*/) {
                ChannelDeclaration channelDeclaration = entry.getKey();
                currentChannelVariableDecl = sdveFactory.createTransientVariableDeclaration();
                currentChannelVariableDecl.setName(channelDeclaration.getName());

                TypedChannelDeclaration typedChannelDeclaration = (TypedChannelDeclaration) channelDeclaration;
                if (typedChannelDeclaration.getTypes().size() == 1) {
                    currentChannelVariableDecl.setType(EcoreUtil.copy(typedChannelDeclaration.getTypes().get(0)));
                } else {
                    TupleType tupleType = sdveFactory.createTupleType();
                    tupleType.getTypes().addAll(EcoreUtil.copyAll(typedChannelDeclaration.getTypes()));
                    currentChannelVariableDecl.setType(tupleType);
                }

                flatSystem.getDeclarations().add(currentChannelVariableDecl);
            }

            entry.getValue().handleSynchronousActions(this::synchronousActionHandler);
        }

        while (!originalSystem.getDeclarations().isEmpty()) {
            NamedDeclaration decl = originalSystem.getDeclarations().remove(0);
            if ( (decl instanceof ChannelDeclaration) && isSynchronous((ChannelDeclaration)decl)){
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

    public void synchronousActionHandler(Transition[] transitions) {
        //if the input and output are in the same process do not synchronize
        if (transitions[0].getProcess().equals(transitions[1].getProcess())) {
            java.lang.System.out.println("info: input/output synchronization in the same process are ignored "+transitions[1].getProcess());
            return;
        }

        Transition outputTransition     = EcoreUtil.copy(transitions[0]);
        Transition inputTransition      = EcoreUtil.copy(transitions[1]);
        //it is a synchronous channel with data exchange
        if (currentChannelVariableDecl != null) {
            //write the data to the channel variable
            VariableReference variableReference = dveFactory.createVariableReference();
            variableReference.setRef(currentChannelVariableDecl);
            variableReference.setRefName(currentChannelVariableDecl.getName());

            //the transmited value should be typecasted before the transmission
            Assignment writeAssignement = dveFactory.createAssignment();
            writeAssignement.setLhs(variableReference);
            writeAssignement.setRhs(EcoreUtil.copy(outputTransition.getSync().getValue()));

            outputTransition.getEffect().add(writeAssignement);

            //write the data to the slot given by the input transition
            variableReference = dveFactory.createVariableReference();
            variableReference.setRef(currentChannelVariableDecl);
            variableReference.setRefName(currentChannelVariableDecl.getName());

            Assignment readAssignement = dveFactory.createAssignment();
            readAssignement.setLhs(EcoreUtil.copy(inputTransition.getSync().getValue()));
            readAssignement.setRhs(variableReference);

            inputTransition.getEffect().add(readAssignement);
        } else {//untyped channel with data exchange
            if (outputTransition.getSync().getValue() != null && inputTransition.getSync().getValue() != null) {
                Assignment syncAssignment = dveFactory.createAssignment();
                syncAssignment.setLhs(EcoreUtil.copy(inputTransition.getSync().getValue()));
                syncAssignment.setRhs(EcoreUtil.copy(outputTransition.getSync().getValue()));
                outputTransition.getEffect().add(syncAssignment);
            }
        }

        Transition compositeTransition = sdveFactory.createTransition();

        compositeTransition.setProcess(outputTransition.getProcess() + "_" + inputTransition.getProcess());
        compositeTransition.setSync(null);

        if (outputTransition.getGuard() != null && inputTransition.getGuard() != null) {
            BinaryExpression compositeGuard = dveFactory.createBinaryExpression();
            compositeGuard.setOperator(BinaryOperator.AND);
            compositeGuard.getOperands().add(EcoreUtil.copy(outputTransition.getGuard()));
            compositeGuard.getOperands().add(EcoreUtil.copy(inputTransition.getGuard()));
            compositeTransition.setGuard(compositeGuard);
        } else {
            throw  new RuntimeException("The system should be normalized, all transition have a guard");
        }

        compositeTransition.getEffect().addAll(outputTransition.getEffect());
        compositeTransition.getEffect().addAll(inputTransition.getEffect());

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
