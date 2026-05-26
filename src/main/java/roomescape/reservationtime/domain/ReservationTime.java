package roomescape.reservationtime.domain;

import java.time.LocalTime;
import lombok.Getter;

@Getter
public class ReservationTime {

    private final Long id;
    private final LocalTime startAt;

    public ReservationTime(Long id, LocalTime startAt) {
        validate(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    public ReservationTime(LocalTime startAt) {
        this(null, startAt);
    }

    public static ReservationTime of(Long id, ReservationTime reservationTime) {
        return new ReservationTime(id, reservationTime.startAt);
    }

    private void validate(LocalTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("예약 시간은 비어 있을 수 없습니다.");
        }
    }

    public boolean isBefore(LocalTime other) {
        return startAt.isBefore(other);
    }
}
