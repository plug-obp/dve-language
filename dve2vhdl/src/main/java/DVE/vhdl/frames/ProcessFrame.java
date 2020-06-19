package DVE.vhdl.frames;

import java.util.Map;

public class ProcessFrame extends CompositeFrame {
    int initial_offset;
    public ProcessFrame(AbstractFrame parent, int initial_offset) {
        super(parent);
        this.initial_offset = initial_offset;
    }

    @Override
    public int offset() {
        for (Map.Entry<String, AbstractFrame> e : symbols.entrySet()) {
            return e.getValue().offset();
        }
        return initial_offset;
    }
}
