package org.sample.scores;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jcstress.annotations.State;
import org.sample.userregistration.UserRegistration;
import org.sample.domain.Score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@State
public class LeaderBoardComparisonClass implements Serializable {

    private final ConcurrentHashMap<UserRegistration.UserId, Score> safeStorage;
    private static final int MAX_RETURN_SIZE = 15;

    public LeaderBoardComparisonClass() {
        this.safeStorage = new ConcurrentHashMap<>();
    }

    private void addSafe(Score score) {
        safeStorage.merge(score.userId, score, (a, b) -> {
            if (a.greaterOrEqualThan(b)) {
                return a;
            } else {
                return b;
            }
        });
    }

    public void add(Score score) {
        this.addSafe(score);
    }

    public List<Score> rankAndDelete() {
        final List<Map.Entry<UserRegistration.UserId, Score>> entries = rankScoresAndLimitEntries();
        deleteLowerScoresThanLastScoreOf(entries);
        return getScores(entries);
    }

    private void deleteLowerScoresThanLastScoreOf(List<Map.Entry<UserRegistration.UserId, Score>> entries) {
        if (entries.size() > 0) {
            Score discardFrom = entries.get(entries.size() - 1).getValue();
            for (Map.Entry<UserRegistration.UserId, Score> userIdScoreEntry : safeStorage.entrySet()) {
                if (discardFrom.greaterThan(userIdScoreEntry.getValue())) {
                    safeStorage.remove(userIdScoreEntry.getKey(), userIdScoreEntry.getValue());
                }
            }
        }
    }

    @NotNull
    private List<Score> getScores(List<Map.Entry<UserRegistration.UserId, Score>> entries) {
        return entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @NotNull
    private List<Map.Entry<UserRegistration.UserId, Score>> rankScoresAndLimitEntries() {
        final ArrayList<Map.Entry<UserRegistration.UserId, Score>> values = new ArrayList<>(safeStorage.entrySet());
        values.sort(Comparator.comparing(Map.Entry::getValue));
        final int min = Math.min(values.size(), MAX_RETURN_SIZE);
        final List<Map.Entry<UserRegistration.UserId, Score>> entries = values.subList(0, min);
        return entries;
    }
}
