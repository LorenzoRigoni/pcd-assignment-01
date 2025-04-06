package pcd.ass01.multithreading.jpf;

/**
 * This class generates a fixed number of double values. It is used for JPF testing instead of Java library Math.random
 * with the aim to reduce the indeterminism.
 */
public class DoubleGenerator {
    private final double[] values = {
      0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
            1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0
    };
    private int index;

    public DoubleGenerator() {
        index = 0;
    }

    /**
     * Generates a new double value.
     *
     * @return the value generated.
     */
    public double next() {
        double value = values[index];
        index = (index + 1) % values.length;
        return value;
    }
}
