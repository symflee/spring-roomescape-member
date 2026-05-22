package roomescape.reservationtime.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.CustomException;
import roomescape.exception.ErrorCode;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeAvailabilityResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Transactional(readOnly = true)
@Service
public class ReservationTimeService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationTimeService(ReservationRepository reservationRepository,
                                  ReservationTimeRepository reservationTimeRepository,
                                  ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    @Transactional
    public ReservationTimeResponse create(ReservationTimeRequest requestDto) {
        ReservationTime reservationTime = reservationTimeRepository.create(requestDto.toEntity());
        return ReservationTimeResponse.from(reservationTime);
    }

    public List<ReservationTimeResponse> findAll() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public List<ReservationTimeAvailabilityResponse> findAvailabilityByDateAndTheme(
            LocalDate date, Long themeId) {
        Theme theme = findTheme(themeId);

        List<ReservationTime> allReservationTimes = reservationTimeRepository.findAll();
        List<Long> bookedTimeIds = reservationTimeRepository.findIdsByDateAndTheme(date, theme.getId());

        return allReservationTimes.stream()
                .map(reservationTime -> {
                    if (bookedTimeIds.contains(reservationTime.getId())) {
                        return ReservationTimeAvailabilityResponse.from(reservationTime, false);
                    }
                    return ReservationTimeAvailabilityResponse.from(reservationTime, true);
                }).toList();
    }

    private Theme findTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.THEME_NOT_FOUND));
    }

    @Transactional
    public void delete(Long id) {
        validateUnreferencedTime(id);

        reservationTimeRepository.delete(id);
    }

    private void validateUnreferencedTime(Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new CustomException(ErrorCode.RESERVATION_TIME_DELETE_REFERENTIAL_INTEGRITY);
        }
    }
}
