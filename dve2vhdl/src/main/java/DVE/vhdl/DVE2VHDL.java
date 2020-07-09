package DVE.vhdl;

import DVE.model.System;
import SDVE.model.util.ModelSwitch;

public class DVE2VHDL {

    ModelSwitch<String> sdveModelSwitch = new ModelSwitch<String>() {

    };

    DVE.model.util.ModelSwitch<String> dveModelSwitch = new DVE.model.util.ModelSwitch<String>() {

    };

    public static String transform(System model) {
        return "Generated code";
    }
}
