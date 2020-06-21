package DVE.vhdl.frames;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeFrame extends AbstractFrame {
    Map<String, AbstractFrame> symbols;
    Map<String, LeafFrame> constants;
    
    public CompositeFrame(AbstractFrame parent) {
        super(parent);
        symbols = new LinkedHashMap<>();
        constants = new LinkedHashMap<>();
    }

    @Override
    public int offset() {
        for (Map.Entry<String, AbstractFrame> e : symbols.entrySet()) {
            return e.getValue().offset();
        }
        return 0;
    }

    @Override
    public int size() {
        int size = 0;
        for (Map.Entry<String, AbstractFrame> e : symbols.entrySet()) {
            size += e.getValue().size();
        }
        return size;
    }

    public void put(String name, AbstractFrame frame) {
        if (constants.get(name) != null || symbols.get(name) != null) {
            throw new RuntimeException("Symbol " + name + " already present in this scope");
        }
        symbols.put(name, frame);
    }

    public void putConstant(String name, LeafFrame frame) {
        if (constants.get(name) != null || symbols.get(name) != null) {
            throw new RuntimeException("Symbol " + name + " already present in this scope");
        }
        constants.put(name, frame);
    }

    @Override
    public String toString() {
        return "CompositeFrame{" +
                "symbols=" + symbols +
                ",constants=" + constants +
                '}';
    }

    @Override
    public AbstractFrame lookup(String name) {
        AbstractFrame frame = constants.get(name);
        if (frame != null) {
            return frame;
        }
        frame = symbols.get(name);
        if (frame != null) {
            return frame;
        }
        if (parent() == null) {
            throw new RuntimeException("Symbol " + name + " is not present in scope");
        }
        return parent().lookup(name);
    }
}
