package sdve.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.NamedDeclaration;
import DVE.model.NumberLiteral;
import DVE.model.Type;
import SDVE.model.*;
import SDVE.model.System;
import SDVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;

import java.math.BigInteger;

public class MemorySizeExtractor {

    ModelSwitch<Integer> modelSwitch = new ModelSwitch<Integer>() {

        @Override
        public Integer caseBitType(BitType object) {
            return 1;
        }

        @Override
        public Integer caseStateType(StateType object) {
            return BigInteger.valueOf(object.getStates().size()).bitLength();
        }

        @Override
        public Integer caseTupleType(TupleType object) {
            int size = 0;
            for (Type type : object.getTypes()) {
                size += doSwitch(type);
            }
            return size;
        }

        @Override
        public Integer caseBufferType(BufferType object) {
            int numberOfElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getSize())).getValue().intValue();
            return numberOfElements * doSwitch(object.getType());
        }

        @Override
        public Integer caseTransientVariableDeclaration(TransientVariableDeclaration object) {
            return doSwitch(object.getType());
        }

        @Override
        public Integer caseSystem(System object) {
            int size = 0;
            for (NamedDeclaration decl : object.getDeclarations()) {
                size += doSwitch(decl);
            }
            return size;
        }

        @Override
        public Integer defaultCase(EObject object) {
            DVE.extractions.MemorySizeExtractor dveMemorySizeExtractor = new DVE.extractions.MemorySizeExtractor() {
                @Override
                public Integer defaultCase(EObject object) {
                    return modelSwitch.doSwitch(object);
                }
            };
            return dveMemorySizeExtractor.doSwitch(object);
        }
    };
}
