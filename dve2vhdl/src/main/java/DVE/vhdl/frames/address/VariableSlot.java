package DVE.vhdl.frames.address;

public class VariableSlot {
    int offset;
    int size;
    public VariableSlot(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }
}
