package io.github.thebesteric.framework.versioner.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArrayIndexPointer {

    private boolean initialized = false;
    private List<ArrayIndex> indexes = new ArrayList<>();

    public void init(int length) {
        if (!initialized) {
            for (int i = 0; i < length; i++) {
                indexes.add(new ArrayIndex(i, false));
            }
            initialized = true;
        }
    }

    public int calcActualIndex(int pastIndex) {
        int offset = 0;
        for (int i = 0; i < pastIndex; i++) {
            if (indexes.get(i).isDeleted()) {
                offset++;
            }
        }
        return pastIndex - offset;
    }

    public void delete(int index) {
        indexes.get(index).setDeleted(true);
    }


    @Data
    @AllArgsConstructor
    private static class ArrayIndex {
        private int index;
        private boolean deleted;
    }
}
