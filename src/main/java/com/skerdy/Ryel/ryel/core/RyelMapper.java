package com.skerdy.Ryel.ryel.core;

import java.util.Objects;

public class RyelMapper {

    private Integer level;
    private Integer index;

    public RyelMapper(int level, int index) {
        this.level = level;
        this.index = index;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RyelMapper that = (RyelMapper) o;
        return Objects.equals(level, that.level) &&
                Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {

        return Objects.hash(level, index);
    }
}
