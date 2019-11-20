package formula.stateFormula;

public class AtomicProp extends formula.stateFormula.StateFormula {
    public final String label;

    public AtomicProp(String label) {
        this.label = label;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        buffer.append(" " + label + " ");
    }

    @Override
    public String getType(){
        return "ATOMIC";
    }

}
