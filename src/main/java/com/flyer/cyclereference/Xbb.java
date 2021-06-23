package com.flyer.cyclereference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Xbb {
    @Autowired
    private Dbb dbb;

    @Override
    public String toString() {
        return "Xbb{" +
                "dbb=" + dbb +
                '}';
    }
}
