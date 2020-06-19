package DVE.vhdl.frames.values.types;

public class SArrayType extends SType {
    int length;
    SType elementType;
    public SArrayType(int length, SType elementType) {
        this.length = length;
        this.elementType = elementType;
    }
    @Override
    public int size() {
        return length * elementType.size();
    }

    @Override
    public String toString() {
        return "SArrayType{" +
                "length=" + length +
                ", elementType=" + elementType +
                '}';
    }
}
