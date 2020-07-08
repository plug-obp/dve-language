package DVE.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import org.eclipse.emf.ecore.EObject;

import java.math.BigInteger;

public class MemorySizeExtractor extends ModelSwitch<Integer> {
    @Override
    public Integer caseByteType(ByteType object) {
        return 8;
    }

    @Override
    public Integer caseIntegerType(IntegerType object) {
        return 2*8;
    }

    @Override
    public Integer caseArrayType(ArrayType object) {
        int numberOfElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getSize())).getValue().intValue();
        return numberOfElements * doSwitch(object.getElementType());
    }

    @Override
    public Integer caseVariableDeclaration(VariableDeclaration object) {
        return doSwitch(object.getType());
    }

    @Override
    public Integer caseChannelDeclaration(ChannelDeclaration object) {
        return 0;
    }

    @Override
    public Integer caseConstantDeclaration(ConstantDeclaration object) {
        return doSwitch(object.getType());
    }

    @Override
    public Integer caseTypedChannelDeclaration(TypedChannelDeclaration object) {
        int numberOfElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getBufferSize())).getValue().intValue();
        int size = 0;
        for (Type type : object.getTypes()) {
            size += doSwitch(type);
        }
        return numberOfElements * size;
    }

    @Override
    public Integer caseProcess(Process object) {
        int v = 0;
        for (Declaration d : object.getDeclarations()) {
            v += doSwitch(d);
        }

        return v + BigInteger.valueOf(object.getStates().size()).bitLength();
    }

    @Override
    public Integer caseSystem(System object) {
        int size = 0;

        for (Declaration d : object.getDeclarations()) {
            size += doSwitch(d);
        }

        return size;
    }

    @Override
    public Integer defaultCase(EObject object) {
        return 0;
    }
}
