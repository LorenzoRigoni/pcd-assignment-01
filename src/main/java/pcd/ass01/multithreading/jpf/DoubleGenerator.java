package pcd.ass01.multithreading.jpf;

public class DoubleGenerator {
    private final double[] values = {
      0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
            1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0
    };
    private int index;

    public DoubleGenerator() {
        index = 0;
    }

    public double next() {
        double value = values[index];
        index = (index + 1) % values.length;
        return value;
    }
}
