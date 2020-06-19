package DVE.vhdl.frames;

import DVE.vhdl.frames.values.types.SType;

public class LeafFrame extends AbstractFrame {
    public int offset;
    public SType type;
    public LeafFrame(AbstractFrame parent, int offset, SType type) {
        super(parent);
        this.offset = offset;
        this.type = type;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int size() {
        return type.size();
    }

    @Override
    public String toString() {
        return "LeafFrame{" +
                "offset=" + offset +
                ", type=" + type +
                '}';
    }
}
