package mathSeminarProject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Main {
    int degree; // degree of polynomial
    int size = 1200;
    double maxx = 2.0;
    int cores = Runtime.getRuntime().availableProcessors();
    double[] coeffs;
    double[] dcoeffs; // derivative coefficients
    ArrayList<Complex> roots = new ArrayList<>();
    ArrayList<Color> colors = new ArrayList<>();
    static int MANY = 1000;
    static int DIMITER = 20;
    static float DIM = 0.15f;

    public static void main(String[] args) {
        new Main(args);
    }

    private Main(String[] args) {
        Date start_time = new Date();
        degree = args.length - 1;
        coeffs = new double[degree + 1];
        dcoeffs = new double[degree];
        for (int i = 0; i <= degree; i++)
            coeffs[i] = Double.parseDouble(args[i]);
        for (int i = 1; i <= degree; i++)
            dcoeffs[i-1] = coeffs[i] * i;
        long[][] data = grid(size, maxx/size );
        Date compute_time = new Date();
        System.out.println("Total compute time: " + (compute_time.getTime() - start_time.getTime()) + "ms");
        outputImage(data);
        Date img_time = new Date();
        System.out.println("Image generation time: " + (img_time.getTime() - compute_time.getTime()) + "ms");
    }

    long[][] grid(int n, double zoom) {
        long[][] result = new long[n][n];
        double n2 = n/2.0;
        int[] res = new int[2];

        for (c = 0; c < cores; c++) {
            new Thread(() -> {
                for (int xi0 = 0; xi0 < n; xi0 += c) {
                    for (int yi = 0; yi < n; yi++) {
                        double x = zoom * (xi - n2),
                                y = zoom * (yi - n2);
                        Complex p = new Complex(x, y);
                        solve(p, res);
                        result[xi][yi] = (((long) res[0]) << 32) | res[1];
                    }
                }
            ).run();
        }
        return result;
    }

    Complex eval_poly(Complex x, double coeffs[]) {
        Complex y = new Complex();
        for (int i = coeffs.length - 1; i > 0; i--) {
            y.add(coeffs[i]);
            y.mul(x);
        }
        y.add(coeffs[0]);
        return y;
    }

    /**
     * Finds which root is reached via the Newton method.
     * Requires: result has length 2
     * Effects: sets result[0] to the index of the root found, and
     * result[1] to the number of iterations required.
     *
     * @param x      initial value
     * @param result
     */
    void solve(Complex x, int[] result) {
        int count = 0;
        while (true) {
            Complex y = eval_poly(x, coeffs);
            if (y.nearZero()) break;
            Complex d = eval_poly(x, dcoeffs);
            if (count++ > MANY || d.isZero()) {
                result[0] = 0; // "root 0" represents failure to find a root
                result[1] = MANY;
                return;
            }
            d.recip();
            d.mul(y);
            x.sub(d);
        }
        result[0] = whichRoot(x);
        result[1] = count;
    }

    int whichRoot(Complex x) {
        int i = 1;
        for (Complex r : roots) {
            if (r.near(x)) return i;
            i++;
        }
        roots.add(x);
        colors.add(randomColor(i-1));
        return i;
    }

    Random rand = new Random(1131);

    Color randomColor(int i) {
        return new Color(Color.HSBtoRGB(rand.nextFloat(), 1f, 1f));
    }

    private void outputImage(long[][] data) {

        System.out.println("Generating image...");
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Color c = getColor(data[i][j]);
                g.setColor(c);
                g.drawRect(i, j, 1, 1);
            }
        }

        try {
            ImageIO.write(image, "PNG", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Color getColor(long data) {
        long root = data >> 32;
        int iter = (int)(data & 0xFFFFFFFF);
        float f = (float)(DIMITER + ((- iter) % DIMITER)) / DIMITER;
        if (f < DIM) f = DIM;
        f /= 255f;
        if (root == 0) return Color.BLACK;
        Color c = colors.get((int)root-1);
        return new Color(c.getRed() * f, c.getGreen()*f, c.getBlue()*f);
    }
}
