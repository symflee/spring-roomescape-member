package roomescape.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.ReservationTimeResponse;
import roomescape.dto.ThemeResponse;
import roomescape.exception.CustomException;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void notExistReservationTimeExceptionTest() {
        ReservationTime createdTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(12, 34)));
        Long testId = createdTime.getId();
        reservationTimeRepository.delete(testId);

        ThemeResponse themeResponseDto = createTheme();
        ReservationRequest requestDto = new ReservationRequest(
                "fizz",
                LocalDate.of(2026, 5, 2),
                testId,
                themeResponseDto.id()
        );

        assertThatThrownBy(() -> reservationService.create(requestDto))
                .hasMessage("[ERROR] 해당 ID의 예약 시간을 찾을 수 없습니다.")
                .isInstanceOf(CustomException.class);
    }

    private ReservationTimeResponse createReservationTime() {
        ReservationTime createdTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        return ReservationTimeResponse.from(createdTime);
    }

    private ThemeResponse createTheme() {
        Theme createdTheme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        return ThemeResponse.from(createdTheme);
    }

    @Test
    void createTest() {
        ReservationTimeResponse reservationTimeResponseDto = createReservationTime();
        ThemeResponse themeResponseDto = createTheme();

        ReservationResponse responseDto = reservationService.create(
                new ReservationRequest("fizz", LocalDate.of(2027, 5, 2), reservationTimeResponseDto.id(),
                        themeResponseDto.id()));

        assertThat(responseDto).isEqualTo(
                new ReservationResponse(responseDto.id(), "fizz", LocalDate.of(2027, 5, 2),
                        reservationTimeResponseDto, themeResponseDto));
    }

    @Test
    void findAllTest() {
        ReservationTimeResponse reservationTimeResponseDto = createReservationTime();
        ThemeResponse themeResponseDto = createTheme();
        ReservationResponse firstResponse = reservationService.create(
                new ReservationRequest("fizz", LocalDate.of(2027, 5, 2), reservationTimeResponseDto.id(),
                        themeResponseDto.id()));
        ReservationResponse secondResponse = reservationService.create(
                new ReservationRequest("fizz2", LocalDate.of(2027, 5, 3), reservationTimeResponseDto.id(),
                        themeResponseDto.id()));

        List<ReservationResponse> responseDtos = reservationService.findReservation(null);

        assertThat(responseDtos.getFirst()).isEqualTo(firstResponse);
        assertThat(responseDtos.get(1)).isEqualTo(secondResponse);
    }

    @Test
    void deleteTest() {
        ReservationTimeResponse reservationTimeResponseDto = createReservationTime();
        ThemeResponse themeResponseDto = createTheme();
        ReservationResponse responseDto = reservationService.create(
                new ReservationRequest("fizz", LocalDate.of(2027, 5, 2), reservationTimeResponseDto.id(),
                        themeResponseDto.id()));
        reservationService.delete(responseDto.id());

        List<ReservationResponse> responseDtos = reservationService.findReservation(null);

        assertThat(responseDtos.size()).isEqualTo(0);
    }
}
