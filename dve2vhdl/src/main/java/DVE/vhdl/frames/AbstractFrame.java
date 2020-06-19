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
}
