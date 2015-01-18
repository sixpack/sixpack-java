package com.seatgeek.sixpack;

public class Alternative {
    private final String name;

    public Alternative(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Alternative name cannot be empty or null!");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alternative)) return false;

        Alternative that = (Alternative) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
