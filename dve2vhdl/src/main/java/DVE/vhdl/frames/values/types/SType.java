package DVE.vhdl.frames.values.types;

public class SType {
    public static final SType INSTANCE = new SType();
    public int size() {
        return 0;
    }

    public boolean subsumes(SType anotherType) {
        return this == anotherType;
    }
}
