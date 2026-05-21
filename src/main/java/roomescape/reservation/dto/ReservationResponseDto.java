package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.dto.ReservationTimeResponseDto;
import roomescape.theme.dto.ThemeResponseDto;

public record ReservationResponseDto(Long id, String name, LocalDate date, ReservationTimeResponseDto time,
                                     ThemeResponseDto theme) {
    public static ReservationResponseDto from(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                ReservationTimeResponseDto.from(reservation.getTime()),
                ThemeResponseDto.from(reservation.getTheme()));
    }
}
