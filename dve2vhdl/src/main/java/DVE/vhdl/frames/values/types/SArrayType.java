package DVE.vhdl.frames.values.types;

import com.sun.codemodel.internal.JDocComment;

public class SArrayType extends SType {
    /**
     * The number of elements
     */
    public int length;
    /**
     * The type of the elements in the array
     * */
    public SType elementType;
    public SArrayType(int length, SType elementType) {
        this.length = length;
        this.elementType = elementType;
    }
    @Override
    public int size() {
        return length * elementType.size();
    }

    @Override
    public boolean subsumes(SType anotherType) {
        if (this == anotherType) return true;
        if (anotherType instanceof SArrayType) {
            SArrayType another = (SArrayType) anotherType;
            return
                    this.length == another.length
                            && this.elementType.subsumes(another.elementType);
        }
        return false;
    }

    @Override
    public String toString() {
        return "SArrayType{" +
                "length=" + length +
                ", elementType=" + elementType +
                '}';
    }
}
