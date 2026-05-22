package roomescape.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.reservationtime.dto.ReservationTimeAvailabilityResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Test
    void createTest() {
        ReservationTimeResponse responseDto = reservationTimeService.create(
                new ReservationTimeRequest(LocalTime.of(10, 0)));

        assertThat(responseDto).isEqualTo(new ReservationTimeResponse(responseDto.id(), LocalTime.of(10, 0)));
    }

    @Test
    void findAllTest() {
        ReservationTimeResponse firstResponse = reservationTimeService.create(
                new ReservationTimeRequest(LocalTime.of(10, 0)));
        ReservationTimeResponse secondResponse = reservationTimeService.create(
                new ReservationTimeRequest(LocalTime.of(11, 0)));

        List<ReservationTimeResponse> responseDtos = reservationTimeService.findAll();

        assertThat(responseDtos.getFirst()).isEqualTo(firstResponse);
        assertThat(responseDtos.get(1)).isEqualTo(secondResponse);
    }

    @Test
    @Sql(scripts = "/available-time-test-data.sql")
    void findAvailabilityByDateAndThemeTest() {
        List<ReservationTimeAvailabilityResponse> responseDtos = reservationTimeService.findAvailabilityByDateAndTheme(
                LocalDate.of(2026, 5, 1), 1L);

        assertThat(responseDtos.getFirst().available()).isFalse();
        assertThat(responseDtos.get(1).available()).isTrue();
    }

    @Test
    void deleteTest() {
        ReservationTimeResponse responseDto = reservationTimeService.create(
                new ReservationTimeRequest(LocalTime.of(10, 0)));
        reservationTimeService.delete(responseDto.id());

        List<ReservationTimeResponse> responseDtos = reservationTimeService.findAll();

        assertThat(responseDtos.size()).isEqualTo(0);
    }
}
