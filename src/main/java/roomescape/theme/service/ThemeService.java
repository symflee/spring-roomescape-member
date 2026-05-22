package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.CustomException;
import roomescape.exception.ErrorCode;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@Transactional(readOnly = true)
@Service
public class ThemeService {
    private static final int RANKING_LIMIT = 10;

    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;

    public ThemeService(ReservationRepository reservationRepository, ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
    }

    @Transactional
    public ThemeResponse create(ThemeRequest requestDto) {
        Theme theme = requestDto.toEntity();
        return ThemeResponse.from(themeRepository.create(theme));
    }

    public List<ThemeResponse> findAll() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findRanking(LocalDate startDate, LocalDate endDate) {
        return themeRepository.findAllByOrderByReservationCountDesc(startDate, endDate, RANKING_LIMIT).stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        validateUnreferencedTheme(id);

        themeRepository.delete(id);
    }

    private void validateUnreferencedTheme(Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new CustomException(ErrorCode.THEME_DELETE_REFERENTIAL_INTEGRITY);
        }
    }

}
