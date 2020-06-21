package DVE.vhdl.frames.values.types;

import java.util.ArrayList;
import java.util.List;

public class STupleType extends SType {
    List<SType> types;

    public STupleType() {
        super();
        this.types = new ArrayList<>();
    }

    public void add(SType type) {
        types.add(type);
    }

    @Override
    public int size() {
        int size = 0;
        for (SType type : types) {
            size += type.size();
        }
        return size;
    }
}
