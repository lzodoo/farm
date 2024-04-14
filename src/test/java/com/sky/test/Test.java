package com.sky.test;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Test {
    @org.junit.jupiter.api.Test
    public void Test2(){

    }
    @org.junit.jupiter.api.Test
    public void Test3(){
        int[] a=new int[]{1,2,3,4,5,6,7,8,9,10};
        Arrays.stream(a).sorted().forEach(System.out::println);
    }
}
