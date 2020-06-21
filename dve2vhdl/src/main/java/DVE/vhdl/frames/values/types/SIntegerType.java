package DVE.vhdl.frames.values.types;

public class SIntegerType extends SType {
    public static final SIntegerType INSTANCE = new SIntegerType();
    private SIntegerType() {super();}
    @Override
    public int size() {
        return 2*8;
    }

    @Override
    public String toString() {
        return "INTEGER(8)";
    }

    @Override
    public boolean subsumes(SType anotherType) {
        return this == anotherType || anotherType == SByteType.INSTANCE;
    }
}
