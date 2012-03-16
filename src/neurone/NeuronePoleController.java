package neurone;

import pole.PoleController;

/**
 * Implementation of PoleController using a Neural Network to take decisions.
 * 
 * @author gyscos
 * 
 */
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
