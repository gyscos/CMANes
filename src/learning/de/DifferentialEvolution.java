package learning.de;

import java.util.Random;

public class DifferentialEvolution {

    private int        dimension;
    private double     F;
    private double     Cr;
    private int        pop_size;

    private double[][] pop;

    long               seed = System.currentTimeMillis();
    Random             rand = new Random(seed);

    public double getCr() {
        return Cr;
    }

    public int getDimension() {
        return dimension;
    }

    public double getF() {
        return F;
    }

    public double[][] getPop() {
        return pop;
    }

    public int getPop_size() {
        return pop_size;
    }

    public double[][] init() {

        pop = new double[pop_size][dimension];
        for (int i = 0; i < pop_size; ++i) {
            for (int j = 0; j < dimension; ++j) {
                pop[i][j] = 2 * rand.nextDouble() - 1;
            }
        }
        return pop;
    }

    public double[][] samplePopulation() {

        int a;
        int b;
        int c;

        double[][] y = new double[pop_size][dimension];

        for (int i = 0; i < pop_size; ++i) {

            do {
                a = rand.nextInt(pop_size);
            } while (a == i);
            do {
                b = rand.nextInt(pop_size);
            } while (b == a || b == i);
            do {
                c = rand.nextInt(pop_size);
            } while (c == a || c == b | c == i);

            int jrand = rand.nextInt(dimension);
            double[] u = new double[dimension];

            for (int m = 0; m < dimension; ++m) {
                u[m] = pop[a][m] + F * (pop[b][m] - pop[c][m]);
            }

            for (int j = 0; j < dimension; ++j) {
                if (rand.nextDouble() < Cr || j == jrand) {
                    y[i] = u;

                } else {
                    y[i] = pop[i];

                }
            }

        }

        return y;

    }

    public void setCr(double cr) {
        Cr = cr;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setF(double f) {
        F = f;
    }

    public void setPop(double[][] pop) {
        this.pop = pop;
    }

    public void setPop_size(int pop_size) {
        this.pop_size = pop_size;
    }

}
