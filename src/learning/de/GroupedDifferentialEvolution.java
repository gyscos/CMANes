package learning.de;

import java.util.Random;

public class GroupedDifferentialEvolution {

    private int          dimension;
    private double       F;
    private double       Cr;
    private int          group_pop_size;
    private int          pop_size;

    private double[][][] group_pop;

    private int[]        LG;
    private int[]        Lx;

    long                 seed = System.currentTimeMillis();
    Random               rand = new Random(seed);

    public double getCr() {
        return Cr;
    }

    public int getDimension() {
        return dimension;
    }

    public double getF() {
        return F;
    }

    public int getGroupPop_size() {
        return group_pop_size;
    }

    public int[] getLG() {
        return LG;
    }

    public int[] getLx() {
        return Lx;
    }

    public double[][] getPop(int n) {
        return group_pop[n];
    }

    public int getPop_size() {
        return pop_size;
    }

    public double[][][] init() {

        group_pop = new double[4][pop_size][dimension];

        for (int n = 0; n < 4; ++n) {
            for (int i = 0; i < group_pop_size; ++i) {
                for (int j = 0; j < dimension; ++j) {
                    group_pop[n][i][j] = 2 * rand.nextDouble() - 1;
                }
            }
        }

        LG = new int[4];
        LG[0] = 0;
        LG[1] = 0;
        LG[2] = 0;
        LG[3] = 1;

        Lx = new int[pop_size];
        for (int i = 0; i < pop_size; ++i) {
            Lx[i] = 0;
        }

        return group_pop;
    }

    public double[][][] samplePopulation() {

        int a;
        int b;
        int c;

        double[][][] y = new double[4][pop_size][dimension];

        for (int n = 0; n < 4; ++n) {

            if (LG[n] == 0) {

                for (int i = 0; i < group_pop_size; ++i) {

                    if (Lx[i + n * group_pop_size] == 0) {

                        do {
                            a = rand.nextInt(group_pop_size);
                        } while (a == i);
                        do {
                            b = rand.nextInt(group_pop_size);
                        } while (b == a || b == i);
                        do {
                            c = rand.nextInt(group_pop_size);
                        } while (c == a || c == b | c == i);

                        int jrand = rand.nextInt(dimension);
                        double[] u = new double[dimension];

                        for (int m = 0; m < dimension; ++m) {
                            u[m] = group_pop[n][a][m] + F * (group_pop[n][b][m] - group_pop[n][c][m]);
                        }

                        for (int j = 0; j < dimension; ++j) {
                            if (rand.nextDouble() < Cr || j == jrand) {
                                y[n][i] = u;

                            } else {
                                y[n][i] = group_pop[n][i];

                            }
                        }
                    } else {
                        y[n][i] = group_pop[n][i];
                    }

                }

            } else {

                for (int i = 0; i < group_pop_size; ++i) {

                    if (Lx[i + n * group_pop_size] == 0) {

                        do {
                            a = rand.nextInt(pop_size);
                        } while (a == i + n * group_pop_size);
                        do {
                            b = rand.nextInt(pop_size);
                        } while (b == a || b == i + n * group_pop_size);
                        do {
                            c = rand.nextInt(pop_size);
                        } while (c == a || c == b | c == i + n * group_pop_size);

                        int jrand = rand.nextInt(dimension);
                        double[] u = new double[dimension];

                        int group_a = Math.round(a / group_pop_size);
                        int group_b = Math.round(b / group_pop_size);
                        int group_c = Math.round(c / group_pop_size);

                        for (int m = 0; m < dimension; ++m) {
                            u[m] = group_pop[group_a][a - group_a * group_pop_size][m]
                                    + F
                                    * (group_pop[group_b][b - group_b * group_pop_size][m] - group_pop[group_c][c
                                            - group_c * group_pop_size][m]);
                        }

                        for (int j = 0; j < dimension; ++j) {
                            if (rand.nextDouble() < Cr || j == jrand) {
                                y[n][i] = u;

                            } else {
                                y[n][i] = group_pop[n][i];

                            }
                        }
                    } else {
                        y[n][i] = group_pop[n][i];
                    }

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

    public void setGroupPop_size(int group_pop_size) {
        this.group_pop_size = group_pop_size;
    }

    public void setLG(int[] LG) {
        this.LG = LG;
    }

    public void setLx(int[] Lx) {
        this.Lx = Lx;
    }

    public void setPop(double[][][] group_pop) {
        this.group_pop = group_pop;
    }

    public void setPop_size(int pop_size) {
        this.pop_size = pop_size;
    }

}
