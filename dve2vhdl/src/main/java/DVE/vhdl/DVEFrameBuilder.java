package DVE.vhdl;

import DVE.evaluation.StaticEvaluator;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.*;
import DVE.model.util.ModelSwitch;
import DVE.vhdl.frames.*;
import DVE.vhdl.frames.values.types.*;

import java.math.BigInteger;
import java.util.Stack;

public class DVEFrameBuilder {
    public static GlobalFrame process(System sys) {
        DVEFrameBuilder dveFrameBuilder = new DVEFrameBuilder();
        dveFrameBuilder.modelSwitch.doSwitch(sys);
        return dveFrameBuilder.result;
    }
    public GlobalFrame result;
    ModelSwitch<SType> modelSwitch = new ModelSwitch<SType>() {
        Stack<CompositeFrame> stack = new Stack<>();
        int const_offset = 0;
        int allocate_constant(int size) {
            const_offset += size;
            return const_offset;
        }

        @Override
        public SType caseSystem(System object) {
            GlobalFrame frame = new GlobalFrame();
            stack.push(frame);
            for (NamedDeclaration decl : object.getDeclarations()) {
                doSwitch(decl);
            }
            stack.pop();
            result = frame;
            return SType.INSTANCE;
        }

        @Override
        public SType caseChannelDeclaration(ChannelDeclaration object) {
            return SType.INSTANCE;
        }

        @Override
        public SType caseConstantDeclaration(ConstantDeclaration object) {
            SType type = doSwitch(object.getType());
            CompositeFrame frame = stack.peek();
            frame.putConstant(object.getName(), new ConstantFrame(frame, const_offset, type));
            allocate_constant(type.size());
            return SType.INSTANCE;
        }

        @Override
        public SType caseTypedChannelDeclaration(TypedChannelDeclaration object) {
            return SType.INSTANCE;
        }

        @Override
        public SType caseVariableDeclaration(VariableDeclaration object) {
            SType type = doSwitch(object.getType());
            CompositeFrame frame = stack.peek();
            frame.put(object.getName(), new LeafFrame(frame, frame.offset()+frame.size(), type));
            return SType.INSTANCE;
        }

        @Override
        public SType caseIntegerType(IntegerType object) {
            return SIntegerType.INSTANCE;
        }

        @Override
        public SType caseByteType(ByteType object) {
            return SByteType.INSTANCE;
        }

        @Override
        public SType caseArrayType(ArrayType object) {
            int nbElements = ((NumberLiteral) StaticEvaluator.evaluate(object.getSize())).getValue().intValue();

            return new SArrayType(nbElements, doSwitch(object.getElementType()));
        }

        @Override
        public SType caseProcess(Process object) {
            CompositeFrame parent = stack.peek();
            ProcessFrame frame = new ProcessFrame(parent, parent.offset() + parent.size());
            stack.push(frame);
            for (NamedDeclaration decl : object.getDeclarations()) {
                doSwitch(decl);
            }

            int stateSize = BigInteger.valueOf(object.getStates().size()).bitLength();
            SStateType type = new SStateType(stateSize);
            int idx = 0;
            for (State state : object.getStates()) {
                type.put(state.getName(), idx++);
            }

            stack.peek().put("%state%", new LeafFrame(frame, frame.offset()+frame.size(), type));
            
            stack.pop();
            parent.put(object.getName(), frame);
            return SType.INSTANCE;
        }
    };
}
