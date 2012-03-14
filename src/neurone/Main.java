package neurone;

import learning.FitnessFinder;
import learning.Teacher;
import learning.cmaes.CMATeacher;
import learning.de.DETeacher;
import pole.Pole;
import pole.PoleController;
import pole.PoleFrame;

public class Main {

    static void findBestSigma(final boolean useTwoPoles, final boolean useTotalInformation, int N) {
        final ReseauNeurone reseau = makeNetwork(useTwoPoles, useTotalInformation);

        double start = 0.01;
        double end = 5.01;

        double steps = 501;

        for (int i = 0; i < steps; i++) {
            double x = i / (steps - 1);
            double sigma = start + (end - start) * x;

            FitnessFinder finder = new FitnessFinder() {
                @Override
                public double getFitness(double[] values, boolean... fit) {
                    reseau.setWeights(values);
                    return Main.getFitness(reseau, useTwoPoles, useTotalInformation, fit);
                }
            };
            Teacher teacher = new CMATeacher(finder, sigma);
            int iter = getMinIterations(teacher, reseau, N);
            System.out.println("[sigma = " + sigma + "] : " + iter);
        }
    }

    public static double getAverageFitness(Teacher teacher, int weightNb, int maxIter, int N) {

        double sum = 0;

        for (int j = 0; j < N; ++j) {

            Teacher.Result result = teacher.teach(weightNb, N);

            sum += result.bestFitness;

            System.out.println("[" + j + "] Best Fitness : " + result.bestFitness);

        }
        return sum / N;
    }

    public static double getFitness(final ReseauNeurone reseau, boolean useTwoPoles, boolean useTotalInformation,
            boolean[] fit) {

        Pole pole = new Pole(useTwoPoles, useTotalInformation);
        pole.setController(new NeuronePoleController(reseau));
        pole.start(0, 0, 0.07, 0, 0, 0);
        pole.end();

        if (fit != null && fit.length > 0)
            fit[0] = !pole.lost;

        return -pole.getFitnessF1();
    }

    static int getMinIterations(Teacher teacher, ReseauNeurone reseau) {
        int weightNb = reseau.getSize();
        return teacher.teach(weightNb).iterations;
    }

    static int getMinIterations(Teacher teacher, ReseauNeurone reseau, int N) {
        int sum = 0;
        for (int i = 0; i < N; i++)
            sum += getMinIterations(teacher, reseau);
        return sum / N;
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

        findBestSigma(useTwoPoles, useTotalInformation, 100);
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

    static void showController(PoleController controller, boolean useTwoPoles, boolean useTotalInformation) {
        PoleFrame frame = new PoleFrame(useTwoPoles, useTotalInformation);
        frame.setController(controller);
        frame.start(0, 0, 0.07, 0, 0, 0);
        frame.end();
    }

    static void test(int... a) {
        a[0] = 1;
    }

    public void test() {

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
