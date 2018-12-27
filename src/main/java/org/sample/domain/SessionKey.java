package org.sample.domain;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SessionKey {
    private final String value;

    public SessionKey(@NotNull String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SessionKey{" +
                "value='" + value + '\'' +
                '}';
    }
}
