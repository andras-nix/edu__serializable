package com.nixsolutions.ppp.serialization;

import java.io.Serializable;

class IntBox implements Serializable {
    private final int content;

    public IntBox(int content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("IntBox{content=%d}", content);
    }
}