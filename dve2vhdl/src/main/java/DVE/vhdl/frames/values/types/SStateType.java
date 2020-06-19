package DVE.vhdl.frames.values.types;

import java.util.LinkedHashMap;
import java.util.Map;

public class SStateType extends SType {
    int size;
    Map<String, Integer> symbols;
    public SStateType(int size) {
        this.size = size;
        this.symbols = new LinkedHashMap<>();
    }

    public void put(String name, Integer id) {
        if (symbols.get(name) != null) {
            throw new RuntimeException("State named " + name + " already present in this scope");
        }
        symbols.put(name, id);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "SStateType{" +
                "size=" + size +
                ", symbols=" + symbols +
                '}';
    }
}
