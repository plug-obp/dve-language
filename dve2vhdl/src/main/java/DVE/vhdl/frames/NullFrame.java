package DVE.vhdl.frames;

public class NullFrame extends AbstractFrame {
    public static final NullFrame instance = new NullFrame();
    private NullFrame() {
        super(null);
    }

    @Override
    public int offset() {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }
}
