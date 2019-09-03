package com.niyanchun.language.lambda;

import java.util.Arrays;
import java.util.List;

/**
 * @description: Some lambda usage demos.
 * @author: NiYanchun
 * @version: 1.0
 * @create: 2019-03-10
 **/
public class LambdaDemos {

    public static void main(String[] args) {
        // Array iterator
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);

        // old way
        for (Integer n : list) {
            System.out.println(n);
        }

        // new way 1
        list.forEach(n -> System.out.println(n));

        // new way 2
        list.forEach(System.out::println);
    }
}
