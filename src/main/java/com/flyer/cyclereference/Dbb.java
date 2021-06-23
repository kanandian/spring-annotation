package com.flyer.cyclereference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dbb {
    @Autowired
    private Xbb xbb;

    @Override
    public String toString() {
        return "Dbb{" +
                "xbb=" + xbb +
                '}';
    }
}
