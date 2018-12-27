package org.sample.scores;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.sample.userregistration.UserRegistration;
import org.sample.domain.Score;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class NonConcurrentLeaderboardTest {

    private static Score sameUserIdAndScore(int value) {
        return new Score(new UserRegistration.UserId("" + value), value);
    }

    @Test
    public void sameSize() {
        LeaderBoardComparisonClass sut = new LeaderBoardComparisonClass();
        rangeClosed(0, 99).forEach(it -> sut.add(sameUserIdAndScore(it)));
        sut.rankAndDelete();
        sut.rankAndDelete();
//        sut.rankAndDelete();
//        sut.rankAndDelete();
//        sut.rankAndDelete();
//        sut.rankAndDelete();
        List<Score> realValues = sut.rankAndDelete();

        final List<Score> expected = reverseRangeOfScoresUpTo15(100);
        assertEquals(15, realValues.size());
        assertEquals(expected, realValues);
    }

    @Test
    public void sameSize_single_element() {
        LeaderBoardComparisonClass sut = new LeaderBoardComparisonClass();
        range(0, 1).forEach(it -> sut.add(sameUserIdAndScore(it)));
        List<Score> realValues = sut.rankAndDelete();

        final List<Score> expected = reverseRangeOfScoresUpTo15(1);
        assertEquals(expected, realValues);
    }

    @Test
    public void sameSize_does_not_cut_elements() {
        LeaderBoardComparisonClass sut = new LeaderBoardComparisonClass();
        range(0, 1).forEach(it -> sut.add(sameUserIdAndScore(it)));
        sut.rankAndDelete();
        sut.rankAndDelete();
        sut.rankAndDelete();
        sut.rankAndDelete();
        List<Score> realValues = sut.rankAndDelete();

        final List<Score> expected = reverseRangeOfScoresUpTo15(1);
        assertEquals(expected, realValues);
    }

    @NotNull
    private List<Score> reverseRangeOfScoresUpTo15(int endExclusive) {
        final List<Integer> collect = range(0, endExclusive).boxed().collect(Collectors.toList());
        Collections.reverse(collect);
        return collect.subList(0, Math.min(collect.size(), 15)).stream().map(NonConcurrentLeaderboardTest::sameUserIdAndScore).collect(Collectors.toList());
    }
}
