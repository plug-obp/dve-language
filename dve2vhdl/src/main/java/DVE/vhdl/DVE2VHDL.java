package DVE.vhdl;

import DVE.model.System;
import DVE.model.util.ModelSwitch;
import DVE.transformations.DVEFixBooleanExpressions;

import java.util.LinkedHashSet;
import java.util.Set;

public class DVE2VHDL {

    ModelSwitch<String> modelSwitch = new ModelSwitch<String>() {

    };

    Set<String> errorMessages = new LinkedHashSet<>();

    void reportError(String error) {
        errorMessages.add(error);
    }

    String transform(System model) {
        DVEFixBooleanExpressions booleanFixer = new DVEFixBooleanExpressions();
        booleanFixer.apply(model);

        //String program = (Program) modelSwitch.doSwitch(sys);
        for (String errorM : errorMessages) {
            java.lang.System.err.println(errorM);
        }
        if (!errorMessages.isEmpty()) return null;
        return null;
    }
}
