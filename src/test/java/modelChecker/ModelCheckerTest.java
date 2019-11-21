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

    @Test
    public void buildAndCheckModel1() {
        try {
            Model model = Model.parseModel("src/test/resources/testModel1.json");

            StateFormula fairnessConstraint = new FormulaParser("src/test/resources/testConstraint1.json").parse();
            StateFormula query = new FormulaParser("src/test/resources/ctl1.json").parse();

            ModelChecker mc = new SimpleModelChecker();

            assertTrue(mc.check(model, fairnessConstraint, query));

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}
