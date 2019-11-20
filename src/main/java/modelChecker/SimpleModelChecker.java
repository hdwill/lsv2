package modelChecker;

import formula.pathFormula.PathFormula;
import formula.stateFormula.StateFormula;
import model.Model;
import model.State;

public class SimpleModelChecker implements ModelChecker {

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {

        return false;
    }

    @Override
    public String[] getTrace() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * Breaks up the input formula into asCTL style.
     */
    private void breakdown(StateFormula formula){

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

