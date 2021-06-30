package sdve.extractions;

import DVE.evaluation.StaticEvaluator;
import DVE.model.ModelFactory;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import SDVE.model.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class TypeInference {

    public static Type getType(Expression expression) {
        return new TypeInference().sdveModelSwitch.doSwitch(expression);
    }

    ModelFactory dveFactory = ModelFactory.eINSTANCE;
    SDVE.model.ModelFactory sdveFactory = SDVE.model.ModelFactory.eINSTANCE;
    ModelSwitch<Type> dveModelSwitch = new ModelSwitch<Type>() {
        @Override
        public Type caseByteType(ByteType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseIntegerType(IntegerType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseArrayType(ArrayType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseVariableDeclaration(VariableDeclaration object) {
            return doSwitch(object.getType());
        }

        @Override
        public Type caseVariableReference(VariableReference object) {
            return doSwitch(object.getRef());
        }

        @Override
        public Type caseConstantDeclaration(ConstantDeclaration object) {
            return doSwitch(object.getType());
        }

        @Override
        public Type caseBooleanLiteral(BooleanLiteral object) {
            return sdveFactory.createBitType();
        }

        @Override
        public Type caseNumberLiteral(NumberLiteral object) {
            if (object.getValue().bitCount() <= 8) {
                return dveFactory.createByteType();
            }
            return dveFactory.createIntegerType();
        }

        @Override
        public Type caseArrayLiteral(ArrayLiteral object) {
            TupleType tupleType = sdveFactory.createTupleType();
            for (Expression exp : object.getValues()) {
                tupleType.getTypes().add(doSwitch(exp));
            }
            return tupleType;
        }

        @Override
        public Type caseIndexedExpression(IndexedExpression object) {
            Type type = doSwitch(object.getBase());
            if (type instanceof ArrayType) {
                return doSwitch(((ArrayType)type).getElementType());
            }
            if (type instanceof TupleType) {
                NumberLiteral literal = (NumberLiteral)StaticEvaluator.evaluate(object.getIndex());
                if (literal == null) {
                    return null;
                }
                TupleType tupleType = (TupleType)type;
                return tupleType.getTypes().get(literal.getValue().intValue());
            }
            return null;
        }

        @Override
        public Type caseUnaryExpression(UnaryExpression object) {
            switch (object.getOperator()) {
                case MINUS:
                case BNOT:
                    return doSwitch(object.getOperand());
                case NOT:
                    return sdveFactory.createBitType();
            }
            return null;
        }

        @Override
        public Type caseBinaryExpression(BinaryExpression object) {
            switch (object.getOperator()) {

                case IMPLY:
                case OR:
                case AND:
                case EQ:
                case NEQ:
                case LT:
                case LEQ:
                case GT:
                case GEQ:
                    return sdveFactory.createBitType();
                case BOR:
                case BAND:
                case BXOR:
                case SHL:
                case SHR:
                    return doSwitch(object.getOperands().get(0));
                case PLUS:
                case MINUS:
                case MULT:
                case DIV:
                case MOD:
                    Type lhsType = doSwitch(object.getOperands().get(0));
                    Type rhsType = doSwitch(object.getOperands().get(1));
                    if (lhsType instanceof IntegerType || rhsType instanceof IntegerType) {
                        return dveFactory.createIntegerType();
                    } else {
                        return dveFactory.createByteType();
                    }
            }
            return null;
        }

        @Override
        public Type defaultCase(EObject object) {
            if (object instanceof SElement) return sdveModelSwitch.doSwitch(object);
            return super.defaultCase(object);
        }
    };

    SDVE.model.util.ModelSwitch<Type> sdveModelSwitch = new SDVE.model.util.ModelSwitch<Type>() {
        @Override
        public Type caseBitType(BitType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseStateType(StateType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseTupleType(TupleType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseBufferType(BufferType object) {
            return EcoreUtil.copy(object);
        }

        @Override
        public Type caseBufferIsFull(BufferIsFull object) {
            return sdveFactory.createBitType();
        }

        @Override
        public Type caseBufferIsEmpty(BufferIsEmpty object) {
            return sdveFactory.createBitType();
        }

        @Override
        public Type caseBufferRead(BufferRead object) {
            BufferType bufferType = (BufferType)doSwitch(object.getBuffer());
            return doSwitch(bufferType.getType());
        }

        @Override
        public Type caseTransientVariableDeclaration(TransientVariableDeclaration object) {
            return doSwitch(object.getType());
        }

        @Override
        public Type defaultCase(EObject object) {
            return dveModelSwitch.doSwitch(object);
        }
    };
}
