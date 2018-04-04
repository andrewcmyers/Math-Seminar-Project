package mathSeminarProject;

public class Complex {
    double re;
    double im;

    public Complex(double r) {
        re = r;
    }

    public Complex(Complex c) {
        re = c.re;
        im = c.im;
    }

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    public Complex() {
        re = im = 0.0;
    }

    /**
     * Set this to a + bi
     */
    void set(double a, double b) {
        re = a;
        im = b;
    }

    /**
     * Effect: this += b
     */
    void add(double b) {
        re += b;
    }

    /**
     * Effect: this -= b
     */
    void sub(Complex b) {
        re -= b.re;
        im -= b.im;
    }

    /**
     * Effect: this *= b
     */
    void mul(double b) {
        re *= b;
        im *= b;
    }

    /**
     * Effect: this *= b
     */
    void mul(Complex b) {
        double ret_re = re * b.re - im * b.im;
        double ret_im = re * b.im + im * b.re;
        re = ret_re;
        im = ret_im;
    }

    /**
     * Effect: this := 1/this
     */
    void recip() {
        double inorm = 1.0 / (re * re + im * im);
        re *= inorm;
        im *= inorm;
        im = -im;
    }

    static double SMALL = 1.0e-15;

    boolean near(Complex c) {
        return Math.abs(re - c.re) < SMALL &&
               Math.abs(im - c.im) < SMALL;
    }

    boolean nearZero() {
        return Math.abs(re) < SMALL &&
               Math.abs(im) < SMALL;
    }

    boolean isZero() {
        return re == 0.0 && im == 0.0;
    }

    public String toString() {
        return re + " + " + im + "i";
    }
}
