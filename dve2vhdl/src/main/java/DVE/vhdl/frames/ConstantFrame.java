package DVE.vhdl.frames;

import DVE.vhdl.frames.values.types.SType;

public class ConstantFrame extends LeafFrame {
    public ConstantFrame(AbstractFrame parent, int offset, SType type) {
        super(parent, offset, type);
    }

    @Override
    public String toString() {
        return "ConstantFrame{" +
                "offset=" + offset +
                ", type=" + type +
                '}';
    }
}
