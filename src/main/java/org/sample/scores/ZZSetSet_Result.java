package org.sample.scores;

import org.openjdk.jcstress.annotations.Result;

import java.io.Serializable;
import java.util.Objects;

@Result
public class ZZSetSet_Result implements Serializable {
    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public boolean r1;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public boolean r2;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public boolean isOk;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public LeaderBoardComparisonClass sut;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZZSetSet_Result that = (ZZSetSet_Result) o;
        return r1 == that.r1 &&
                r2 == that.r2 &&
                isOk == that.isOk &&
                sut.equals(that.sut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r1, r2, isOk, sut);
    }

    @Override
    public String toString() {
        return r1 + ", " +
                r2 + ", " +
                isOk;
    }
}
