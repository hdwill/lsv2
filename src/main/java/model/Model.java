package model;

import java.io.FileReader;
import java.io.IOException;
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

}
