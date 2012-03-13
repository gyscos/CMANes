package neurone;

import learning.FitnessFinder;
import learning.Teacher;
import learning.de.GDETeacher;
import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;

public class Main {

    public static double getFitness(final ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation,
            boolean... fit) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        if (fit != null)
            fit[0] = !pole.lost;

        // System.out.println(1 - pole.getFitness());

        return 1 - pole.getFitness();
    }

    public static boolean isFit(ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        return !pole.lost;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        final boolean useTwoPoles = false;
        final boolean useTotalInformation = true;

        final ReseauNeurone reseau = makeNetwork(useTwoPoles, useTotalInformation);
        int weightNb = reseau.getSize();

        FitnessFinder finder = new FitnessFinder() {
            @Override
            public double getFitness(double[] values, boolean... fit) {
                reseau.setWeights(values);
                return Main.getFitness(reseau, useTwoPoles, useTotalInformation, fit);
            }
        };

        // Teacher teacher = new CMATeacher(finder);
        // Teacher teacher = new DETeacher(finder);
        Teacher teacher = new GDETeacher(finder);

        int[] iterations = new int[1];
        teacher.teach(weightNb, iterations);

        System.out.println("Min iterations for success : " + iterations[0]);

        showController(new NeuronePoleController(reseau), useTwoPoles, useTotalInformation);
    }

    static ReseauNeurone makeNetwork(boolean useTwoPoles, boolean useTotalInformation) {
        ReseauNeurone reseau = new ReseauNeurone();

        if (useTwoPoles) {
            if (useTotalInformation) {
                reseau.setNeurones(6, 2, 1);
                for (int i = 0; i < 6; i++)
                    reseau.addSynapse(0, i, 2, 0, 1, true);
            } else {
                reseau.setNeurones(3, 1, 1);
                for (int i = 0; i < 3; i++)
                    reseau.addSynapse(0, i, 2, 0, 1, true);
                reseau.addSynapse(1, 0, 1, 0, 1, false);
                reseau.addSynapse(1, 0, 2, 0, 1, false);
                reseau.addSynapse(2, 0, 1, 0, 1, false);
                reseau.addSynapse(2, 0, 2, 0, 1, false);
            }
        } else {
            if (useTotalInformation) {
                reseau.setNeurones(4, 2, 1);
                for (int i = 0; i < 4; i++)
                    reseau.addSynapse(0, i, 2, 0, 1, true);
            } else {
                reseau.setNeurones(2, 1, 1);
                for (int i = 0; i < 2; i++)
                    reseau.addSynapse(0, i, 2, 0, 1, true);
                reseau.addSynapse(1, 0, 1, 0, 1, false);
                reseau.addSynapse(1, 0, 2, 0, 1, false);
                reseau.addSynapse(2, 0, 1, 0, 1, false);
                reseau.addSynapse(2, 0, 2, 0, 1, false);
            }
        }

        return reseau;
    }

    public static void showController(PoleController controller, boolean useTwoPoles, boolean useTotalInformation) {
        PoleFrame frame = new PoleFrame(useTwoPoles, useTotalInformation);
        frame.setController(controller);
        frame.start(0, 0, 0.07, 0, 0, 0);
        frame.end();
    }
}
