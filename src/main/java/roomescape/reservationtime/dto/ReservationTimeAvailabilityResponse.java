package roomescape.reservationtime.dto;

import roomescape.reservationtime.domain.ReservationTime;

public record ReservationTimeAvailabilityResponse(
        ReservationTimeResponse time,
        boolean available
) {
    public static ReservationTimeAvailabilityResponse from(ReservationTime time, boolean available) {
        return new ReservationTimeAvailabilityResponse(
                ReservationTimeResponse.from(time),
                available);
    }
}
