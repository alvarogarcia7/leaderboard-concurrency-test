package org.sample.userregistration;

import org.jetbrains.annotations.NotNull;
import org.sample.domain.SessionKey;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistration {

    private final Map<SessionKey, Expiring<UserId>> values = new ConcurrentHashMap<>();
    private Timer timer = new Timer();

    public void insertExpiringSessionId(SessionKey sessionKey, Expiring<UserId> userId) {
        values.put(sessionKey, userId);
        scheduleForDeletion(sessionKey, userId);
    }

    public Optional<Expiring<UserId>> getUserId(SessionKey sessionKey) {
        return Optional.ofNullable(values.get(sessionKey))
                .flatMap(Expiring::validated);
    }

    private void scheduleForDeletion(SessionKey sessionKey, Expiring<UserId> userId) {
        final LocalDate localDate = userId.expiryDate.toLocalDate();
        final Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeIfSame(sessionKey, userId);
            }
        }, date);
    }

    private boolean removeIfSame(SessionKey sessionKey, Expiring<UserId> it) {
        return values.remove(sessionKey, it);
    }

    public static class UserId implements Serializable {
        private final String value;

        public UserId(@NotNull String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserId userId = (UserId) o;
            return value.equals(userId.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "UserId{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    public static class Expiring<T> {
        private final T value;
        private final LocalDateTime expiryDate;

        public Expiring(@NotNull T value, @NotNull LocalDateTime expiryDate) {
            this.value = value;
            this.expiryDate = expiryDate;
        }

        public Optional<Expiring<T>> validated() {
            if (LocalDateTime.now().isAfter(expiryDate)) {
                return Optional.empty();
            } else {
                return Optional.of(this);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Expiring<?> expiring = (Expiring<?>) o;
            return value.equals(expiring.value) &&
                    expiryDate.equals(expiring.expiryDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, expiryDate);
        }

        @Override
        public String toString() {
            return "Expiring{" +
                    "value=" + value +
                    ", expiryDate=" + expiryDate +
                    '}';
        }
    }
}
