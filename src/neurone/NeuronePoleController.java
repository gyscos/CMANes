package neurone;

import pole.PoleController;

public class NeuronePoleController implements PoleController {

    ReseauNeurone reseau;

    public NeuronePoleController(ReseauNeurone reseau) {
        this.reseau = reseau;
    }

    @Override
    public double getAction(double... data) {
        return reseau.getOutput(data);
    }
}
