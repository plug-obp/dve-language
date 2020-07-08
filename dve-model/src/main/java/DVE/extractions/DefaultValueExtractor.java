package DVE.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.math.BigInteger;

public class DefaultValueExtractor extends ModelSwitch<Literal> {
    ModelFactory factory = ModelFactory.eINSTANCE;
    @Override
    public Literal caseByteType(ByteType object) {
        NumberLiteral literal = factory.createNumberLiteral();
        literal.setValue(BigInteger.ZERO);
        return literal;
    }

    @Override
    public Literal caseIntegerType(IntegerType object) {
        NumberLiteral literal = factory.createNumberLiteral();
        literal.setValue(BigInteger.ZERO);
        return literal;
    }

    @Override
    public Literal caseArrayType(ArrayType object) {
        ArrayLiteral literal = factory.createArrayLiteral();

        Literal defaultElement = doSwitch(object.getElementType());

        int numberOfElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getSize())).getValue().intValue();
        for (int i = 0; i<numberOfElements; i++) {
            literal.getValues().add(EcoreUtil.copy(defaultElement));
        }
        return literal;
    }
}
