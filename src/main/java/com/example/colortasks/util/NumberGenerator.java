package com.example.colortasks.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
public class NumberGenerator {

    public String generateRandomNumber(){
        ArrayList<Integer> numbersGenerated = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            Random randomNumber = new Random();
            int iNumber = randomNumber.nextInt(9) + 1;
            if (!numbersGenerated.contains(iNumber)) {
                numbersGenerated.add(iNumber);
                builder.append(iNumber);
            } else {
                i--;
            }
        }
        return builder.toString();
    }
}
