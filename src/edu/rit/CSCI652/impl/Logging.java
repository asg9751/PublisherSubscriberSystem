package edu.rit.CSCI652.impl;

public class Logging {

    static boolean on=false;

    public static void print(String s){

        if(on)
            System.out.println(s);
    }
}
