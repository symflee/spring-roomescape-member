package roomescape.reservationtime.dto;

import roomescape.reservationtime.domain.ReservationTime;

public record ReservationTimeAvailabilityResponseDto(
        ReservationTimeResponseDto time,
        boolean available
) {
    public static ReservationTimeAvailabilityResponseDto from(ReservationTime time, boolean available) {
        return new ReservationTimeAvailabilityResponseDto(
                ReservationTimeResponseDto.from(time),
                available);
    }
}
