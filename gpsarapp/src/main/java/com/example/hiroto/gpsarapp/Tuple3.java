package com.example.hiroto.gpsarapp;

/**
 * Created by horiba on 2015/01/14.
 */
public class Tuple3<A,B,C> {
    private A elemA;
    private B elemB;
    private C elemC;
    public Tuple3(A a,B b,C c){
        this.elemA = a;
        this.elemB = b;
        this.elemC = c;
    }
    public A getElem1() {
        return elemA;
    }
    public B getElem2() {
        return elemB;
    }
    public C getElem3() {
        return elemC;
    }
    public void setElem1(A a) {
        this.elemA = a;
    }
    public void setElem2(B b) {
        this.elemB = b;
    }
    public void setElem3(C c) {
        this.elemC = c;
    }
    public void setAll(A a,B b,C c) {
        setElem1(a);
        setElem2(b);
        setElem3(c);
    }
    @Override
    public String toString(){
        return getElem1() + "," + getElem2() + "," + getElem3();
    }
}
