package com.miguelsanchezp.ancmediaplayer;

public class Complex {
    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal () {
        return this.real;
    }

    public double getImaginary () {
        return this.imaginary;
    }

    public static Complex multiply (Complex a, Complex b) {
        return new Complex(a.getReal()*b.getReal()-a.getImaginary()*b.getImaginary(), a.getReal()*b.getImaginary()+a.getImaginary()*b.getReal());
    }

    public static Complex add (Complex a, Complex b) {
        return new Complex (a.getReal()+b.getReal(), a.getImaginary()+b.getImaginary());
    }
    public static Complex subtract(Complex a, Complex b) {
        return new Complex (a.getReal()-b.getReal(), a.getImaginary()-b.getImaginary());
    }

    @Override
    public String toString () {
        if (imaginary < 0) {
            return (String.valueOf(this.real) + String.valueOf(this.imaginary) + 'i');
        }
        return (String.valueOf(this.real) + '+' + String.valueOf(this.imaginary) + 'i');
    }
}
