package modelChecker;

import com.sun.org.apache.xpath.internal.operations.Equals;
import formula.pathFormula.Until;
import formula.stateFormula.StateFormula;
import formula.pathFormula.*;
import formula.stateFormula.*;
import model.Model;
import model.State;
import model.Transition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.*;
import java.util.HashSet;

public class SimpleModelChecker implements ModelChecker {
    public static final HashSet<String> emptySet = new HashSet<String>();

    public enum Predicate {
        THERE_EXISTS,
        FOR_ALL
    }

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
        return evaluate(constraint, model.getStates()[0]);
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
        // Translate the formula into one that conforms to asCTL:
        // Φ ::= true | p | ¬Φ | Φ ∧ Φ | ∃φ | ∀φ
        formula = recursiveStateBreak(formula);
        return recursiveEval(formula, state);
    }

    private boolean recursiveEval(StateFormula formula, State state){

        if (formula instanceof AtomicProp){
            for (String label : state.getLabel()) {
                if (label.equals(((AtomicProp) formula).label)) return true;
            }
            return false;
        }

        else if (formula instanceof Not) {
            return !recursiveEval(((Not) formula).stateFormula, state);
        }

        else if (formula instanceof And){
            return (recursiveEval(((And) formula).left, state) && recursiveEval(((And) formula).right, state));
        }

        else if (formula instanceof ThereExists){
            return true; // FIXME: 21/11/2019 pls
        }

        else if (formula instanceof ForAll){
            return true;
        }

        return false;
    }


    /**
     * Initial method for the recursive {@see #checkPath(Model, Transition, Until, ArrayList<ArrayList<String>>)} method,
     * calls checkPath using each of the initial states in the model.
     * @param model the model to check.
     * @param u the Until clause to check.
     * @return
     */
    public ArrayList<ArrayList<String>> checkPathFormula(Model model, Until u){
        List<State> initStates = Arrays.stream(model.getStates())
                .filter(State::isInit)
                .collect(Collectors.toList());

        for (State initState : initStates) {
            for (Transition t : model.getTransitions())
                t.setExplored(false);

            for (Transition t : model.getTransistionsFrom(initState)){
                checkPath(model, t, u, null);
            }
        }
        return null;

    }

    /**
     * Recursively builds a path that satisfies the PathFormula u.
     * @param model the model this path is in.
     * @param inboundT The current transition we're exploring.
     * @param u The {@see PathFormula} that path will conform to. Will always be in the the form PhiLeft A u B PhiRight
     *          where PhiLeft and PhiRight are {@see StateFormula}, A and B are accepted inputs in their respective sides,
     *          and u is the {@see Class#Until} path formula.
     * @param path The path is a 2D ArrayList, with each element
     * @return A completed path that satisfies the path formula, or null if one doesn't exist.
     */
    private ArrayList<ArrayList<String>> checkPath(Model model, Transition inboundT, Until u, ArrayList<ArrayList<String>> path){
        // Base case to only check each transition once.
        inboundT.setExplored(true);

        // For each outbound, unexplored Transition at the current state.
        State state = model.getState(inboundT.getTarget());
        for (Transition outboundT : model.getTransistionsFrom(state)) {
            if (outboundT.isExplored()) continue;

            // Recursively build the path.
            path = checkPath(model, outboundT, u, path);
        }

        // Path building process begins from here.

        // If phiRight holds in the current state and it can be reached using actions in action set B,
        // it a valid component of the second part of the path.
        if (path == null && evaluate(u.right, state) &&
                Arrays.stream(inboundT.getActions()).anyMatch(action -> u.getRightActions().contains(action))){
            path = new ArrayList<>();
            path.add(new ArrayList<String>(Arrays.asList(inboundT.getActions())));
            return path;
        }

        // If the path hasn't started, it won't be starting here because this state doesn't conform to phiRight.
        else if (path == null || !evaluate(u.right, state)) {
            return null;
        }

        // If the path has started, and this node conforms to phiLeft, and it can be reached using actions in action
        // set A, it can be added to the path.
        else if (evaluate(u.left, state) &&
                Arrays.stream(inboundT.getActions()).anyMatch(action -> u.getLeftActions().contains(action))){
            path.add(new ArrayList<>(Arrays.asList(inboundT.getActions())));
            return path;
        }

        // If the path has started, but this state doesn't conform to phiLeft or can't be reached using actions from
        // action set A, we don't want to use it, so we return null and start building a new path.
        else {
            return null;
        }
    }

    /**
     * Finds the paths that satisfy the formula.
     */
    private void findAcceptedPaths(){

    }
}

