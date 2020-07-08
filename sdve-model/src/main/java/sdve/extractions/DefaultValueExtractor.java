package sdve.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import SDVE.model.BitType;

import SDVE.model.BufferType;
import SDVE.model.StateType;
import SDVE.model.TupleType;
import SDVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class DefaultValueExtractor {
    public static Literal defaultValue(EObject object) {
        return new DefaultValueExtractor().modelSwitch.doSwitch(object);
    }
    ModelSwitch<Literal> modelSwitch = new ModelSwitch<Literal>() {
        ModelFactory modelFactory = ModelFactory.eINSTANCE;
        @Override
        public Literal caseBitType(BitType object) {
            return modelFactory.createFalseLiteral();
        }

        @Override
        public Literal caseTupleType(TupleType object) {
            ArrayLiteral literal = modelFactory.createArrayLiteral();

            for (Type type : object.getTypes()) {
                literal.getValues().add(doSwitch(type));
            }
            return literal;
        }

        @Override
        public Literal caseStateType(StateType object) {
            NumberLiteral literal = modelFactory.createNumberLiteral();
            literal.setValue(object.getDefault().getValue());
            return literal;
        }

        @Override
        public Literal caseBufferType(BufferType object) {
            int numberOfElements = ((NumberLiteral)StaticEvaluator.evaluate(object.getSize())).getValue().intValue();
            ArrayLiteral literal = modelFactory.createArrayLiteral();

            Literal defaultElement = doSwitch(object.getType());
            for (int i = 0; i<numberOfElements;i++) {
                literal.getValues().add(EcoreUtil.copy(defaultElement));
            }
            return literal;
        }

        @Override
        public Literal defaultCase(EObject object) {
            DVE.extractions.DefaultValueExtractor dveDefaultValueExtractor = new DVE.extractions.DefaultValueExtractor() {
                @Override
                public Literal defaultCase(EObject object) {
                    return modelSwitch.doSwitch(object);
                }
            };
            return dveDefaultValueExtractor.doSwitch(object);
        }
    };
}
