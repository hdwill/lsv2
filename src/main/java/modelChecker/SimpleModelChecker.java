package modelChecker;

import formula.pathFormula.*;
import formula.stateFormula.*;
import model.Model;
import model.State;

import java.awt.*;
import java.util.HashSet;

public class SimpleModelChecker implements ModelChecker {
    public static final HashSet<String> emptySet = new HashSet<String>();

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {

        return false;
    }

    @Override
    public String[] getTrace() {
        // TODO Auto-generated method stub
        return null;
    }

    //This is called from recursiveStateBreak
    private PathFormula recursivePathBreak(PathFormula formula){
        switch (formula.getType()){
            case "ALWAYS" :
                //This should never be reached, ALWAYS should be caught in a ForAll or ForEach
                return null;
            case "EVENTUALLY" :
                //⋄p = TrueξUap
                return new Until(new BoolProp(true), ((Eventually) formula).stateFormula, null, ((Eventually) formula).getRightActions());
            case "NEXT" :
                //X(p) = TrueøUap
                BoolProp tempBool = new BoolProp(true);
                Until retUntil = new Until(tempBool, recursiveStateBreak(((Next) formula).stateFormula), emptySet, ((Next) formula).getActions());
                return retUntil;
            case "UNTIL" :
                return new Until( recursiveStateBreak(((Until) formula).left), recursiveStateBreak(((Until) formula).right), ((Until) formula).getLeftActions(), ((Until) formula).getRightActions());
            default :
                System.out.println("Unknown Path Formula");
                break;
        }
        return null;
    }

    //recursiveStateBreak should be called first on the result of the parser, creating a more consistent system.
    private StateFormula recursiveStateBreak(StateFormula formula){
        switch (formula.getType()){
            case "AND" :
                return new And(recursiveStateBreak( ((And) formula).left), recursiveStateBreak( ((And) formula).right));
            case "ATOMIC" :
                return new AtomicProp(((AtomicProp) formula).label);
            case "BOOL" :
                return new BoolProp(((BoolProp) formula).value);
            case "NOT" :
                return new Not(recursiveStateBreak( ((Not) formula).stateFormula));
            case "OR" :
                return new Or(recursiveStateBreak( ((Or) formula).left), recursiveStateBreak( ((Or) formula).right));
            case "THEREEXISTS" :
                if(((ThereExists) formula).pathFormula.getType().equals("ALWAYS")){
                    //TODO: Implement special case always.
                    //Always p is seen as a terminating case, so this special case is sufficient.
                    //∃◽p = ㄱ∀(TrueaUaㄱp)
                    Always tempAlways = ((Always) ((ThereExists) formula).pathFormula);
                    Until tempUntil = new Until(new BoolProp(true), recursiveStateBreak(tempAlways.stateFormula), tempAlways.getActions(), tempAlways.getActions());
                    ForAll tempForAll = new ForAll(tempUntil);
                    return new Not(tempForAll);
                } else {
                    return new ThereExists(recursivePathBreak(((ThereExists) formula).pathFormula));
                }
            case "FORALL" :
                if(((ForAll) formula).pathFormula.getType().equals("ALWAYS")){
                    //TODO: Implement special case always.
                    //Always p is seen as a terminating case, so this special case is sufficient.
                    //∀◽p = ㄱ∃(TrueaUaㄱp)
                    Always tempAlways = ((Always) ((ForAll) formula).pathFormula);
                    Until tempUntil = new Until(new BoolProp(true), recursiveStateBreak(tempAlways.stateFormula), tempAlways.getActions(), tempAlways.getActions());
                    ThereExists tempTE = new ThereExists(tempUntil);
                    return new Not(tempTE);
                } else {
                    return new ForAll(recursivePathBreak(((ForAll) formula).pathFormula));
                }
            default :
                System.out.println("Unknown Path Formula");
                break;
        }
        return null;
    }

    /**
     * Returns whether the given formula holds in the given state.
     * @param formula
     * @param state
     */
    private boolean evaluate(StateFormula formula, State state){
        return false;
    }

    /**
     * Checks to see if the path formula holds will hold in future states.
     * @param pathFormula
     */
    private boolean checkPath(PathFormula pathFormula, State currentState){
        return false;
    }


    /**
     * Finds the paths that satisfy the formula.
     */
    private void findAcceptedPaths(){

    }
}

