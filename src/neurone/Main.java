package neurone;

import learning.FitnessFinder;
import learning.Pair;
import learning.Teacher;
import learning.cmaes.CMATeacher;
import learning.de.DETeacher;
import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;

public class Main {

    /**
     * Tries many simulations to find minimum iterations for various sigma
     * values.
     * 
     * @param useTwoPoles
     * @param useTotalInformation
     * @param N
     *            number of simulations to average on for each sigma value.
     */
    static void findBestSigma(final boolean useTwoPoles, final boolean useTotalInformation, int N) {
        final ReseauNeurone reseau = makeNetwork(useTwoPoles, useTotalInformation);
        FitnessFinder finder = new FitnessFinder() {
            @Override
            public Pair<Double, Boolean> getFitness(double[] values) {
                reseau.setWeights(values);
                return Main.getFitness(reseau, useTwoPoles, useTotalInformation);
            }
        };

        double start = 3;
        double end = 5;

        double steps = 7;

        for (int i = 0; i < steps; i++) {

            // Find sigma
            double x = i / (steps - 1);
            double sigma = start + (end - start) * x;

            // See how it fares
            Teacher teacher = new CMATeacher(finder, sigma);
            int iter = getAverageMinIterations(teacher, reseau, N);
            System.out.println("[sigma = " + sigma + "] : " + iter);
        }
    }

    /**
     * Computes average fitness obtained by the teacher after N iterations
     * 
     * @param teacher
     * @param weightNb
     * @param maxIter
     * @param N
     * @return
     */
    public static double getAverageFitness(Teacher teacher, int weightNb, int maxIter, int N) {

        double sum = 0;

        for (int j = 0; j < N; ++j) {

            Teacher.Result result = teacher.teach(weightNb, N);

            sum += result.bestFitness;

            System.out.println("[" + j + "] Best Fitness : " + result.bestFitness);

        }
        return sum / N;
    }

    /**
     * Average several simulations to find the minimal required number of
     * iterations to successfully train the neural network.
     * 
     * @param teacher
     * @param reseau
     * @param N
     * @return
     */
    static int getAverageMinIterations(Teacher teacher, ReseauNeurone reseau, int N) {
        int sum = 0;
        for (int i = 0; i < N; i++) {
            int v = getMinIterations(teacher, reseau);
            sum += v;
            // System.out.println("Solution found in " + v);
        }
        return sum / N;
    }

    /**
     * Computes the fitness for a given neural network.
     * 
     * @param reseau
     * @param useTwoPoles
     * @param useTotalInformation
     * @return A pair with <Fitness, Success>
     */
    public static Pair<Double, Boolean> getFitness(final ReseauNeurone reseau, boolean useTwoPoles,
            boolean useTotalInformation) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        // Score is inverted because we are trying to minimize
        return new Pair<Double, Boolean>(-pole.getFitnessF1(), !pole.lost);
    }

    /**
     * Computes the minimal required iterations for the teacher to successfully
     * train the neural network.
     * 
     * @param teacher
     * @param reseau
     * @return
     */
    static int getMinIterations(Teacher teacher, ReseauNeurone reseau) {
        int weightNb = reseau.getSize();
        return teacher.teach(weightNb).iterations;
    }

    /**
     * Returns true if the given Neural network is able to run through a whole
     * game without losing.
     * 
     * @param reseau
     * @param useTwoPoles
     * @param useTotalInformation
     * @return
     */
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

        findBestSigma(useTwoPoles, useTotalInformation, 100);

    }

    /**
     * Creates adequate neural network for the pole balancing problem in the
     * given configuration
     * 
     * @param useTwoPoles
     * @param useTotalInformation
     * @return
     */
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

    /**
     * Shows a graphical window with a cart controlled by the given controller.
     * 
     * @param controller
     * @param useTwoPoles
     * @param useTotalInformation
     */
    static void showController(PoleController controller, boolean useTwoPoles, boolean useTotalInformation) {
        PoleFrame frame = new PoleFrame(useTwoPoles, useTotalInformation);
        frame.setController(controller);
        frame.start(0, 0, 0.07, 0, 0, 0);
        frame.end();
    }

    public void test(final boolean useTwoPoles, final boolean useTotalInformation) {

        final ReseauNeurone reseau = makeNetwork(useTwoPoles, useTotalInformation);

        FitnessFinder finder = new FitnessFinder() {
            @Override
            public Pair<Double, Boolean> getFitness(double[] values) {
                reseau.setWeights(values);
                return Main.getFitness(reseau, useTwoPoles, useTotalInformation);
            }
        };

        int weightNb = reseau.getSize();

        int nb_test = 1;
        int sumiter;

        System.out.println(weightNb);

        for (int i = 0; i < 1; ++i) {

            sumiter = 0;

            double params1 = 0.5;
            double params2 = 0.5;
            double params3 = 10;
            int maxIter = 100000;
            Teacher teacher = new DETeacher(finder, params1, params2, params3);
            double fitness = getAverageFitness(teacher, weightNb, maxIter, nb_test);

            System.out.println("Average iterations for success on " + nb_test + " tests : " + sumiter);

            System.out.println("Average Fitness on " + nb_test + " tests : " + fitness);
        }
    }
}
