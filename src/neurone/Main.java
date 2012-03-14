package neurone;

import learning.FitnessFinder;
import learning.Teacher;
import learning.de.DETeacher;
import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;

public class Main {

    public static double getAverageFitness(Teacher teacher, int weightNb, int maxIter, int N) {

        double sum = 0;

        for (int j = 0; j < N; ++j) {
            double[] bestFitness = new double[1];

            teacher.teachEndFitness(weightNb, N, bestFitness);

            sum += bestFitness[0];

            System.out.println("[" + j + "] Best Fitness : " + bestFitness[0]);

        }
        return sum / N;
    }

    public static double getFitness(final ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation,
            boolean... fit) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        if (fit != null && fit.length > 0)
            fit[0] = !pole.lost;

        // System.out.println(1 - pole.getFitness());

        return -pole.getFitness();
    }

    static int getMinIterations(Teacher teacher, ReseauNeurone reseau) {
        int weightNb = reseau.getSize();
        int[] iterations = new int[1];
        teacher.teachMinIter(weightNb, iterations);
        return iterations[0];
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

        final boolean useTwoPoles = true;
        final boolean useTotalInformation = true;

        final ReseauNeurone reseau = makeNetwork(useTwoPoles, useTotalInformation);

        FitnessFinder finder = new FitnessFinder() {
            @Override
            public double getFitness(double[] values, boolean... fit) {
                reseau.setWeights(values);
                return Main.getFitness(reseau, useTwoPoles, useTotalInformation, fit);
            }
        };

        int weightNb = reseau.getSize();

        int nb_test = 1;
        int sumiter;

        sumiter = 0;

        System.out.println(weightNb);

        for (int i = 0; i < 1; ++i) {

            sumiter = 0;

            double params1 = 0.5;
            double params2 = 0.5;
            double params3 = 10;

            Teacher teacher = new DETeacher(finder, params1, params2, params3);
            int maxIter = 100000;

            double fitness = getAverageFitness(teacher, weightNb, maxIter, nb_test);

            System.out.println("Average iterations for success on " + nb_test + " tests : " + sumiter);

            System.out.println("Average Fitness on " + nb_test + " tests : " + fitness);
        }
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

    static void test(int... a) {
        a[0] = 1;
    }
}
