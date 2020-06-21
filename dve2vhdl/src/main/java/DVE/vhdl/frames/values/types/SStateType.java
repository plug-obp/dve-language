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

    public int get(String name) {
        Integer stateID = symbols.get(name);
        if (stateID == null) {
            throw new RuntimeException("State named " + name + " is not defined in this scope");
        }
        return stateID;
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
