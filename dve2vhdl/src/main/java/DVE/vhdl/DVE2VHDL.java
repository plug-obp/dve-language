package DVE.vhdl;

import DVE.model.NamedDeclaration;
import DVE.model.Process;
import DVE.model.System;
import DVE.model.util.ModelSwitch;
import DVE.transformations.DVEFixBooleanExpressions;

import java.util.LinkedHashSet;
import java.util.Set;

public class DVE2VHDL {

    ModelSwitch<String> modelSwitch = new ModelSwitch<String>() {
        
        @Override
        public String caseSystem(System object) {
            StringBuilder sb = new StringBuilder();
            
            for (NamedDeclaration decl : object.getDeclarations()) {
                sb.append(doSwitch(decl));
            }
            return sb.toString();
        }

        @Override
        public String caseProcess(Process object) {
            return "";
        }
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
