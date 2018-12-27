package org.sample.domain;


import org.jetbrains.annotations.NotNull;
import org.sample.userregistration.UserRegistration;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Score implements Comparable<Score>, Comparator<Score>, Serializable {
    public final UserRegistration.UserId userId;
    public final int value;

    public Score(@NotNull UserRegistration.UserId userId, int value) {
        this.userId = userId;
        this.value = value;
    }

    public static Score score(String userIdRawValue, int score) {
        return new Score(new UserRegistration.UserId(userIdRawValue), score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return value == score.value &&
                userId.equals(score.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, value);
    }

    @Override
    public String toString() {
        return userId + "=" + value;
    }

    public boolean greaterOrEqualThan(Score other) {
        return this.value >= other.value;
    }

    public boolean greaterThan(Score other) {
        return this.value > other.value;
    }

    @Override
    public int compare(Score o1, Score o2) {
        return o1.compareTo(o2);
    }

    @Override
    public int compareTo(@NotNull Score o) {
        return -Integer.compare(this.value, o.value);
    }
}
