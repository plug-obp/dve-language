package sdve.extractions;

import DVE.model.ChannelDeclaration;
import SDVE.model.Transition;
import plug.utils.CartesianProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChannelSchedule {
    ChannelDeclaration channel;
    List<Transition> inputSchedule;
    List<Transition> outputSchedule;

    public ChannelSchedule(ChannelDeclaration channel) {
        inputSchedule = new ArrayList<>();
        outputSchedule = new ArrayList<>();
    }

    public void addInput(Transition input) {
        inputSchedule.add(input);
    }

    public void addOutput(Transition output) {
        outputSchedule.add(output);
    }

    public void handleSynchronousActions(Consumer<Transition[]> handler) {
        if (inputSchedule.isEmpty() || outputSchedule.isEmpty()) {
            //one of the two is empty so no contribution to the soup
            return;
        }
        //inputs and output present
        List<List<Transition>> in = new ArrayList<>();
        in.add(outputSchedule);
        in.add(inputSchedule);
        CartesianProduct<Transition> cp = new CartesianProduct<>();
        cp.cartesianProduct(
                in,
                handler,
                Transition[]::new);
    }
}
