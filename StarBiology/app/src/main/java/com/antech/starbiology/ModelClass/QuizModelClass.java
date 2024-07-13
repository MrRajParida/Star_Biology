package com.antech.starbiology.ModelClass;

public class QuizModelClass {
    private String Q, A, B, C, D, CA;

    public QuizModelClass() {
    }

    public QuizModelClass(String q, String a, String b, String c, String d, String CA) {
        Q = q;
        A = a;
        B = b;
        C = c;
        D = d;
        this.CA = CA;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String q) {
        Q = q;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getCA() {
        return CA;
    }

    public void setCA(String CA) {
        this.CA = CA;
    }
}
