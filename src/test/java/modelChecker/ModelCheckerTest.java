package modelChecker;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import formula.FormulaParser;
import formula.stateFormula.StateFormula;
import modelChecker.ModelChecker;
import modelChecker.SimpleModelChecker;
import model.Model;

public class ModelCheckerTest {

    /*
     * An example of how to set up and call the model building methods and make
     * a call to the model checker itself. The contents of model.json,
     * constraint1.json and ctl.json are just examples, you need to add new
     * models and formulas for the mutual exclusion task.
     */
    @Test
    public void buildAndCheckModel() {
        try {
            Model model = Model.parseModel("src/test/resources/testModel1.json");

            StateFormula fairnessConstraint = new FormulaParser("src/test/resources/testConstraint1.json").parse();
            StateFormula query = new FormulaParser("src/test/resources/ctl1.json").parse();

            ModelChecker mc = new SimpleModelChecker();

            mc.check(model, fairnessConstraint, query);

            assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

//    @Test
//    public void checkPathTest(){
//        try {
//            Model model = Model.parseModel("src/test/resources/model1.json");
//
//            StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
//            StateFormula query = new FormulaParser("src/test/resources/ctl1.json").parse();
//
//            SimpleModelChecker mc = new SimpleModelChecker();
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail(e.toString());
//        }
//      }






}
