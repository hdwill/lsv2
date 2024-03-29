package formula.stateFormula;

public abstract class StateFormula {
    public abstract void writeToBuffer(StringBuilder buffer);
    public abstract String getType();

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        writeToBuffer(buffer);
        return buffer.toString();
    }
}
