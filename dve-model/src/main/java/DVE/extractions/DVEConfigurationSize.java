package DVE.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import DVE.transformations.PrettyPrinter;
import org.eclipse.emf.ecore.EObject;

import java.math.BigInteger;

public class DVEConfigurationSize {

    public static int get(System sys) {
        DVEConfigurationSize dcs = new DVEConfigurationSize();
        int size = dcs.modelSwitch.doSwitch(sys);
//        java.lang.System.out.println("\nMAX: "+ dcs.max);
//        java.lang.System.out.println("\nSUM Transitions: "+ dcs.sumTransition);
        return size;
    }
    int max = 0;
    int sumTransition = 0;
    ModelSwitch<Integer> modelSwitch = new ModelSwitch<Integer>() {

        @Override
        public Integer caseSystem(System object) {
            int v = 0;

            for (Declaration d : object.getDeclarations()) {
                v += doSwitch(d);
            }

            return v;
        }

        @Override
        public Integer caseChannelDeclaration(ChannelDeclaration object) {
            java.lang.System.out.println("--- CHANNEL ---"+ PrettyPrinter.toString(object));
            return 0;
        }

        @Override
        public Integer caseConstantDeclaration(ConstantDeclaration object) {
            return 0;
        }

        @Override
        public Integer caseTypedChannelDeclaration(TypedChannelDeclaration object) {
            java.lang.System.out.println("--- TYPED CHANNEL ---"+ PrettyPrinter.toString(object));
            return 0;
        }

        @Override
        public Integer caseVariableDeclaration(VariableDeclaration object) {
            return doSwitch(object.getType());
        }

        @Override
        public Integer caseIntegerType(IntegerType object) {
            return 2*8;
        }

        @Override
        public Integer caseByteType(ByteType object) {
            return 8;
        }

        @Override
        public Integer caseArrayType(ArrayType object) {
            int size = ((NumberLiteral)StaticEvaluator.evaluate(object.getSize())).getValue().intValue();
            return size * doSwitch(object.getElementType());
        }

        @Override
        public Integer caseTransition(Transition object) {
            max = Integer.max(max, object.getEffect().size());
            return 0;
        }

        @Override
        public Integer caseProcess(Process object) {
            int v = 0;
            for (Declaration d : object.getDeclarations()) {
                v += doSwitch(d);
            }

            sumTransition += object.getTransitions().size();
            for (EObject o : object.getTransitions()) {
                doSwitch(o);
            }

            return v + BigInteger.valueOf(object.getStates().size()).bitLength();
        }
    };
}
