package modelChecker;

import com.sun.org.apache.xpath.internal.operations.Equals;
import formula.pathFormula.Until;
import formula.stateFormula.StateFormula;
import model.Model;
import model.State;
import model.Transition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleModelChecker implements ModelChecker {

    public enum Predicate {
        THERE_EXISTS,
        FOR_ALL
    }

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
    private boolean evaluate(StateFormula formula, State state){
        return false;
    }


    private boolean checkPathsInit(Model model, Predicate predicate, Until u){
        // All paths must start from initial states, so we can filter out the rest for this for-loop.
        List<State> initStates = Arrays.stream(model.getStates())
                .filter(State::isInit)
                .collect(Collectors.toList());
        for (State initState : initStates) {
            for (State otherState : model.getStates()) {
                otherState.setVisited(false);
            }
        }
        return false;
    }

    private ArrayList<ArrayList<String>> checkPaths(Model model, Until u, State state, ArrayList<ArrayList<String>> path){
        state.setVisited(true);

        path = null;

        // A depth-first search through the model.
        for (Transition transition: model.getTransistionsFrom(state)) {
            if (!model.getState(transition.getTarget()).isVisited()) {
                // Add all the actions required to get to the target to the path.
                path = checkPaths(model, u, model.getState(transition.getTarget()), path);
            }
        }

        // If phiRight holds in this state and it can be reached using actions in action set B,
        // it a valid component of the second part of the path.
        if (evaluate(u.right, state)) {
            List<Transition> transitions = Arrays.stream(model.getTransistionsTo(state))
                    .filter(transition ->
                            Arrays.stream(transition.getActions()).anyMatch(action -> u.getRightActions().contains(action)))
                    .collect(Collectors.toList());

            // Start a new recursion rabbit hole for each transition we can use to get to this state.
            if (transitions.size() > 0){

            }
        // If the path hasn't started, it won't be starting here because this state doesn't conform to phiRight.
        } else if (path == null) {
            return null;
        }
        return null;
    }


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
     * @param u The {@see Class#PathFormula} that path will conform to. Will always be in the the form PhiLeft A u B PhiRight
     *          where PhiLeft and PhiRight are {@see Class#StateFormula}, A and B are accepted inputs in their respective sides,
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

