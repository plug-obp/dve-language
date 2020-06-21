package DVE.vhdl.frames;

public abstract class AbstractFrame {
    public AbstractFrame parent;

    public AbstractFrame(AbstractFrame parent) {
        this.parent = parent;
    }

    public AbstractFrame parent() {
        return parent;
    }
    public abstract int offset();
    public abstract int size();

    public AbstractFrame lookup(String name) {
        throw new RuntimeException("Cannot lookup in this context");
    }
}
