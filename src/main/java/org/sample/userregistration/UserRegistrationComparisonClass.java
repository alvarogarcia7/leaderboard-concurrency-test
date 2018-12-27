package org.sample.userregistration;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jcstress.annotations.State;
import org.sample.domain.SessionKey;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@State
public abstract class UserRegistrationComparisonClass {

    private UserRegistration sut = new UserRegistration();
    private static final SessionKey sessionKey = new SessionKey("session_key");
    protected static final LocalDateTime[] expiringDates;

    static {
        expiringDates = new LocalDateTime[6];
        expiringDates[0] = LocalDateTime.MIN;
        expiringDates[1] = LocalDateTime.MAX;
        expiringDates[2] = LocalDateTime.now();
        expiringDates[3] = LocalDateTime.now().plus(1, ChronoUnit.MICROS);
        expiringDates[4] = LocalDateTime.now().plus(1, ChronoUnit.MILLIS);
        expiringDates[5] = LocalDateTime.now().plus(1, ChronoUnit.NANOS);
    }

    public UserRegistrationComparisonClass() {
        sut.insertExpiringSessionId(sessionKey, new UserRegistration.Expiring<>(new UserRegistration.UserId("user_id"), chooseExpiryDate()));
    }

    protected abstract LocalDateTime chooseExpiryDate();

    public Optional<UserRegistration.Expiring<UserRegistration.UserId>> getAndDelete() {
        return sut.getUserId(sessionKey);
    }

    @NotNull
    private UserRegistration.UserId userId() {
        return new UserRegistration.UserId(randomString());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    @State
    public static class UserRegistrationComparisonClassAlwaysPast extends UserRegistrationComparisonClass {

        @Override
        protected LocalDateTime chooseExpiryDate() {
            return expiringDates[0];
        }
    }

    @State
    public static class UserRegistrationComparisonClassAlwaysFuture extends UserRegistrationComparisonClass {

        @Override
        protected LocalDateTime chooseExpiryDate() {
            return expiringDates[1];
        }
    }

    @State
    public static class UserRegistrationComparisonClassNow extends UserRegistrationComparisonClass {

        @Override
        protected LocalDateTime chooseExpiryDate() {
            return expiringDates[2];
        }
    }
}
