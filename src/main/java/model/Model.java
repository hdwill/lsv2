package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

/**
 * A model is consist of states and transitions
 */
public class Model {
    model.State[] states;
    model.Transition[] transitions;

    public static Model parseModel(String filePath) throws IOException {
        Gson gson = new Gson();
        Model model = gson.fromJson(new FileReader(filePath), Model.class);
        for (model.Transition t : model.transitions) {
            System.out.println(t);
            ;
        }
        return model;
    }

    /**
     * Returns the list of the states
     * 
     * @return list of state for the given model
     */
    public State[] getStates() {
        return states;
    }

    /**
     * Returns the list of transitions
     * 
     * @return list of transition for the given model
     */
    public Transition[] getTransitions() {
        return transitions;
    }

    /**
     * Returns the list of transitions that start at the given state.
     * @param startState the state to get transitions from
     * @return the list of transitions for the given state.
     */
    public Transition[] getTransistions(State startState){
        ArrayList<Transition> tList = new ArrayList<>();
        for (Transition transistion : transitions) {
            if (transistion.getSource().equals(startState.getName())){
                tList.add(transistion);
            }
        }
        return (Transition[])tList.toArray();
    }

    /**
     * Returns a state from the model with the given name.
     * @param name the id of the state to search for.
     * @return the State corresponding with the id.
     * null if not found
     */
    public State getState(String name){
        for (State state: states) {
            if (state.getName().equals(name)) return state;
        }
        return null;
    }
}
