package DVE.vhdl;

import DVE.evaluation.StaticEvaluator;
import DVE.model.*;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.util.ModelSwitch;
import DVE.vhdl.frames.values.types.*;
import org.eclipse.emf.ecore.EObject;

import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

public class InferTypes {
    Map<EObject, SType> typeMap = new IdentityHashMap<>();
    ModelSwitch<Boolean> modelSwitch = new ModelSwitch<Boolean>() {
        Stack<SType> typingContext = new Stack<>();

        boolean hasType(EObject o) {
            return typeMap.containsKey(o);
        }
        SType type(EObject o) {
            SType type = typeMap.get(o);
            if (type == null) {
                throw new RuntimeException("The object " + o + " does not have a type associated");
            }
            return type;
        }

        SType type(EObject o, SType type) {
            if (typeMap.get(o) != null) {
                throw new RuntimeException("The object " + o + " is already typed");
            }
            return typeMap.put(o, type);
        }

        @Override
        public Boolean caseSystem(System object) {
            assert typingContext.empty();
            for (EObject e : object.getDeclarations()) {
                doSwitch(e);
            }
            return true;
        }

        @Override
        public Boolean caseVariableDeclaration(VariableDeclaration object) {
            assert typingContext.empty();
            if (hasType(object)) return true;
            doSwitch(object.getType());
            SType type = type(object, type(object.getType()));
            typingContext.push(type);
            doSwitch(object.getInitial());
            typingContext.pop();
            return true;
        }

        @Override
        public Boolean caseByteType(ByteType object) {
            if (hasType(object)) return true;
            type(object, SByteType.INSTANCE);
            return true;
        }

        @Override
        public Boolean caseIntegerType(IntegerType object) {
            if (hasType(object)) return true;
            type(object, SIntegerType.INSTANCE);
            return true;
        }

        @Override
        public Boolean caseArrayType(ArrayType object) {
            if (hasType(object)) return true;

            typingContext.push(SIntegerType.INSTANCE);
            doSwitch(object.getSize());
            typingContext.pop();

            int nbElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getSize())).getValue().intValue();

            doSwitch(object.getElementType());
            SType elementType = type(object.getElementType());

            type(object, new SArrayType(nbElements, elementType));
            return true;
        }

        @Override
        public Boolean caseTypedChannelDeclaration(TypedChannelDeclaration object) {
            assert typingContext.empty();
            if (hasType(object)) return true;
            STupleType objectType = new STupleType();
            for (EObject type : object.getTypes()) {
                doSwitch(type);
                objectType.add(type(type));
            }
            type(object, objectType);
            //type the bufferSize expression
            typingContext.push(SIntegerType.INSTANCE);
            doSwitch(object.getBufferSize());
            typingContext.pop();
            return true;
        }

        @Override
        public Boolean caseProcess(Process object) {
            assert typingContext.empty();
            for (EObject decl : object.getDeclarations()) {
                doSwitch(decl);
            }

            int stateSize = BigInteger.valueOf(object.getStates().size()).bitLength();
            SStateType stateType = new SStateType(stateSize);
            int idx = 0;
            for (State state : object.getStates()) {
                stateType.put(state.getName(), idx++);
                type(state, stateType);
            }

            for (EObject state : object.getAccepting()) {
                typingContext.push(stateType);
                doSwitch(state);
                typingContext.pop();
            }

            for (EObject state : object.getCommited()) {
                typingContext.push(stateType);
                doSwitch(state);
                typingContext.pop();
            }

            typingContext.push(stateType);
            doSwitch(object.getInitial());
            typingContext.pop();

            for (EObject transition : object.getTransitions()) {
                doSwitch(transition);
            }

            return true;
        }

        @Override
        public Boolean caseStateReference(StateReference object) {
            if (hasType(object)) return true;
            SType contextType = typingContext.peek();

            doSwitch(object.getRef());
            SType type = type(object.getRef());
            if (contextType != null && contextType != type) {
                throw new RuntimeException("Typing context"+ type +" does not match the object type" + type);
            }

            type(object, type);
            return true;
        }

        @Override
        public Boolean caseProcessStateReference(ProcessStateReference object) {
            if (hasType(object)) return true;
            doSwitch(object.getRef());
            type(object, type(object.getRef()));
            return true;
        }

        @Override
        public Boolean caseState(State object) {
            assert !typingContext.empty();
            if (hasType(object)) return true;
            type(object, typingContext.peek());
            return true;
        }

        @Override
        public Boolean caseTransition(Transition object) {
            assert typingContext.empty();
            doSwitch(object.getFrom());
            doSwitch(object.getTo());
            doSwitch(object.getSync());

            //the guard is a byte type
            typingContext.push(SByteType.INSTANCE);
            doSwitch(object.getGuard());
            typingContext.pop();

            for (EObject effect : object.getEffect()) {
                doSwitch(effect);
            }
            return true;
        }

        @Override
        public Boolean caseAssignment(Assignment object) {
            assert typingContext.empty();

            doSwitch(object.getLhs());

            SType contextType = type(object.getLhs());
            typingContext.push(contextType);
            doSwitch(object.getRhs());
            typingContext.pop();
            return true;
        }

        @Override
        public Boolean caseBooleanLiteral(BooleanLiteral object) {
            if (hasType(object)) return true;
            assert !typingContext.empty();
            SType contextType = typingContext.peek();

            if (!contextType.subsumes(SByteType.INSTANCE)) {
                throw new RuntimeException("The context type " + contextType + " does not accept boolean values");
            }
            type(object, SByteType.INSTANCE);
            return true;
        }

        @Override
        public Boolean caseNumberLiteral(NumberLiteral object) {
            if (hasType(object)) return true;
            assert !typingContext.empty();
            SType contextType = typingContext.peek();

            SType type;
            if (object.getValue().bitLength() <= 8) {
                type = SByteType.INSTANCE;
            } else {
                type = SIntegerType.INSTANCE;
            }
            if (!contextType.subsumes(type)) {
                throw new RuntimeException("The context type " + contextType + " does not allow " + type + " values");
            }
            type(object, type);
            return true;
        }

        @Override
        public Boolean caseArrayLiteral(ArrayLiteral object) {
            if (hasType(object)) return true;
            assert !typingContext.empty();
            if (! (typingContext.peek() instanceof SArrayType)) {
                throw new RuntimeException("The typing context " + typingContext.peek() + " is not compatible with array literals");
            }
            SArrayType contextType = (SArrayType) typingContext.peek();

            if (object.getValues().size() != contextType.length) {
                throw new RuntimeException("The array literal size " + object.getValues().size() + " does not match the required size" + contextType.length);
            }

            typingContext.push(contextType.elementType);
            for (EObject element : object.getValues()) {
                doSwitch(element);
            }
            typingContext.pop();

            type(object, contextType);
            return true;
        }

        @Override
        public Boolean caseIndexedExpression(IndexedExpression object) {
            if (hasType(object)) return true;

            doSwitch(object.getBase());

            typingContext.push(SIntegerType.INSTANCE);
            doSwitch(object.getIndex());
            typingContext.pop();

            if (!(type(object.getBase()) instanceof SArrayType)) {
                throw new RuntimeException("The base of an indexed expression should be an array type");
            }
            SArrayType arrayType = (SArrayType) type(object.getBase());

            if (!typingContext.empty()) {
                SType contextType = typingContext.peek();
                if (!contextType.subsumes(arrayType.elementType)) {
                    throw new RuntimeException("The indexed expression type does not match the required type");
                }
                type(object, contextType);
            } else {
                type(object, arrayType.elementType);
            }
            return true;
        }

        @Override
        public Boolean caseVariableReference(VariableReference object) {
            if (hasType(object)) return true;
            doSwitch(object.getRef());

            SType type = type(object.getRef());
            if (!typingContext.empty()) {
                SType contextType = typingContext.peek();
                if (!contextType.subsumes(type)) {
                    throw new RuntimeException("The variable reference type does not match the required type");
                }
                type(object, contextType);
            } else {
                type(object, type);
            }
            return true;
        }

        @Override
        public Boolean caseUnaryExpression(UnaryExpression object) {
            if (hasType(object)) return true;
            SType type;
            switch (object.getOperator()) {
                case MINUS:
                    doSwitch(object.getOperand());
                    type = type(object.getOperand());
                    if (!(type instanceof SByteType || type instanceof SIntegerType)) {
                        throw new RuntimeException("The minus operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(type)) {
                            throw new RuntimeException("The unary minus type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        type(object, type);
                    }
                    break;
                case BNOT:
                    doSwitch(object.getOperand());
                    type = type(object.getOperand());
                    if (!(type instanceof SByteType || type instanceof SIntegerType)) {
                        throw new RuntimeException("The binary not operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(type)) {
                            throw new RuntimeException("The binary not type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        type(object, type);
                    }
                    break;
                case NOT:
                    doSwitch(object.getOperand());
                    type = type(object.getOperand());
                    if (!(type instanceof SByteType || type instanceof SIntegerType)) {
                        throw new RuntimeException("The logical not operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(type)) {
                            throw new RuntimeException("The logical not type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        type(object, SByteType.INSTANCE);
                    }
                    break;
            }
            return true;
        }

        @Override
        public Boolean caseBinaryExpression(BinaryExpression object) {
            if (hasType(object)) return true;
            SType typeLHS, typeRHS;
            switch (object.getOperator()) {
                case IMPLY:
                case OR:
                case AND:
                case LT:
                case LEQ:
                case GT:
                case GEQ:
                    for (EObject o : object.getOperands()) {
                        doSwitch(o);
                    }
                    typeLHS = type(object.getOperands().get(0));
                    typeRHS = type(object.getOperands().get(1));
                    if (
                            !((typeLHS instanceof SByteType || typeLHS instanceof SIntegerType)
                    &&      (typeRHS instanceof SByteType || typeRHS instanceof SIntegerType))
                    ) {
                        throw new RuntimeException("The operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(SByteType.INSTANCE)) {
                            throw new RuntimeException("The type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        type(object, SByteType.INSTANCE);
                    }
                    break;
                case BOR:
                case BAND:
                case BXOR:
                    for (EObject o : object.getOperands()) {
                        doSwitch(o);
                    }
                    typeLHS = type(object.getOperands().get(0));
                    typeRHS = type(object.getOperands().get(1));
                    if (
                            !((typeLHS instanceof SByteType || typeLHS instanceof SIntegerType)
                                    &&      (typeRHS instanceof SByteType || typeRHS instanceof SIntegerType))
                    ) {
                        throw new RuntimeException("The operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!(contextType.subsumes(typeLHS) && contextType.subsumes(typeRHS))) {
                            throw new RuntimeException("The type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        if (typeLHS instanceof SIntegerType || typeRHS instanceof SIntegerType) {
                            type(object, SIntegerType.INSTANCE);
                        } else {
                            type(object, SByteType.INSTANCE);
                        }
                    }
                    break;

                case EQ:
                case NEQ:
                    for (EObject o : object.getOperands()) {
                        doSwitch(o);
                    }
                    type(object, SByteType.INSTANCE);
                    break;

                case SHL:
                case SHR:
                    for (EObject o : object.getOperands()) {
                        doSwitch(o);
                    }
                    typeLHS = type(object.getOperands().get(0));
                    typeRHS = type(object.getOperands().get(1));
                    if (
                            !((typeLHS instanceof SByteType || typeLHS instanceof SIntegerType)
                                    &&      (typeRHS instanceof SByteType || typeRHS instanceof SIntegerType))
                    ) {
                        throw new RuntimeException("The operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(typeLHS)) {
                            throw new RuntimeException("The type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        type(object, typeLHS);
                    }
                    break;
                case PLUS:
                case MINUS:
                case MULT:
                case DIV:
                case MOD:
                    for (EObject o : object.getOperands()) {
                        doSwitch(o);
                    }
                    typeLHS = type(object.getOperands().get(0));
                    typeRHS = type(object.getOperands().get(1));
                    if (
                            !((typeLHS instanceof SByteType || typeLHS instanceof SIntegerType)
                                    &&      (typeRHS instanceof SByteType || typeRHS instanceof SIntegerType))
                    ) {
                        throw new RuntimeException("The operator expects a Byte of Integer type");
                    }
                    if (!typingContext.empty()) {
                        SType contextType = typingContext.peek();
                        if (!contextType.subsumes(typeLHS)) {
                            throw new RuntimeException("The type does not match the required context type");
                        }
                        type(object, contextType);
                    } else {
                        if (typeLHS instanceof SIntegerType || typeRHS instanceof SIntegerType) {
                            type(object, SIntegerType.INSTANCE);
                        } else {
                            type(object, SByteType.INSTANCE);
                        }
                    }
                    break;
            }
            return true;
        }
    };
}
