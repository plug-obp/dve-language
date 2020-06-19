package DVE.vhdl.frames.values.types;

public class SByteType extends SType {
    public static final SByteType INSTANCE = new SByteType();
    private SByteType() {super();}
    @Override
    public int size() {
        return 8;
    }

    @Override
    public String toString() {
        return "BYTE(8)";
    }
}
