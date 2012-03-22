package neurone;

public class Helper {
    public static double[] interpolate(double from, double to, int steps) {
        // Formula = A * exp(B * x)

        double result[] = new double[steps];

        double B = (Math.log(to) - Math.log(from)) / (to - from);
        double A = from / Math.exp(B * from);

        for (int i = 0; i < steps; i++) {
            double ratio = (double) i / (steps - 1);
            double x = from + ratio * (to - from);

            result[i] = A * Math.exp(B * x);
        }

        return result;
    }
}
