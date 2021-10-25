package com.zrc.demo.execute;

public class PrintSquare {

    public static int fun(int n) {
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        return n * fun(n - 1);
    }

    public static void main(String[] args) {
        System.out.println(fun(6));
    }
}