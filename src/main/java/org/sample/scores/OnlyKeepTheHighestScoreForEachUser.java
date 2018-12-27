/*
 * Copyright (c) 2017, Red Hat Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sample.scores;

// See jcstress-samples or existing tests for API introduction and testing guidelines

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.Z_Result;
import org.sample.userregistration.UserRegistration;
import org.sample.domain.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static org.sample.domain.Score.score;

@JCStressTest
@Outcome(id = "false", expect = Expect.FORBIDDEN)
@Outcome(id = "true", expect = Expect.ACCEPTABLE)
public class OnlyKeepTheHighestScoreForEachUser {

    @Actor
    public static void actor1(LeaderBoardComparisonClass sut) {
        sut.add(score("1", 2));
        sut.add(score("1", 2));
        sut.add(score("1", 2));
        sut.add(score("1", 2));
    }

    @Actor
    public static void actor2(LeaderBoardComparisonClass sut) {
        sut.add(score("1", 3));
        sut.add(score("1", 3));
        sut.add(score("1", 3));
        sut.add(score("1", 3));
        sut.add(score("1", 3));
    }

    @Actor
    public static void actor3(LeaderBoardComparisonClass sut) {
        sut.add(score("1", 0));
        sut.add(score("1", 0));
        sut.add(score("1", 0));
        sut.add(score("1", 0));
        sut.add(score("1", 0));
    }

    @Arbiter
    public void arbiter(LeaderBoardComparisonClass sut, Z_Result r) {
        final List<Score> result = new ArrayList<>();
        result.add(new Score(new UserRegistration.UserId("1"), 3));
        r.r1 = result.equals(sut.rankAndDelete());
    }

}


@JCStressTest
@Outcome(id = "true, true, true", expect = Expect.ACCEPTABLE)
class OnlyKeep15Elements {

    @Actor
    public static void actor1(LeaderBoardComparisonClass sut, ZZSetSet_Result r) {
        final boolean[] isValid = {true};
        range(0, 100).forEach(it -> {
            isValid[0] = isValid[0] && sut.rankAndDelete().size() <= 15;
            sut.add(fixedUserIdAndScoreUpTo(it, it));
            isValid[0] = isValid[0] && sut.rankAndDelete().size() <= 15;
        });
        r.r1 = isValid[0];
        r.sut = sut;
    }

    @Actor
    public static void actor2(LeaderBoardComparisonClass sut, ZZSetSet_Result r) {
        final boolean[] isValid = {true};
        range(0, 100).forEach(it -> {
            isValid[0] = isValid[0] && sut.rankAndDelete().size() <= 15;
            sut.add(sameUserIdAndScore(it));
            isValid[0] = isValid[0] && sut.rankAndDelete().size() <= 15;
        });
        r.r2 = isValid[0];
    }

    private static Score fixedUserIdAndScoreUpTo(int userId, int score) {
        int decrease;
        if (score > 0) {
            decrease = Math.abs(new Random().nextInt(score));
        } else {
            decrease = 0;
        }
        return new Score(new UserRegistration.UserId("" + userId), score - decrease);
    }

    private static Score sameUserIdAndScore(int score) {
        return new Score(new UserRegistration.UserId("" + score), score);
    }

    @Arbiter
    public void arbiter(ZZSetSet_Result r) {
        final LeaderBoardComparisonClass leaderBoardComparisonClass = new LeaderBoardComparisonClass();
        final List<Integer> collect = range(0, 100).boxed().collect(Collectors.toList());
        collect.stream().map(OnlyKeep15Elements::sameUserIdAndScore).forEach(leaderBoardComparisonClass::add);
        final List<Score> result = leaderBoardComparisonClass.rankAndDelete();
        final List<Score> rs1 = r.sut.rankAndDelete();
        final boolean equals = result.equals(rs1);
        if (!equals) {
            throw new RuntimeException(result + ", " + rs1 + ", " + r.sut);
        }
        r.isOk = equals;
    }

}

