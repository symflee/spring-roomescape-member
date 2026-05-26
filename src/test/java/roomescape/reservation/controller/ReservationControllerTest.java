package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("예약을 생성하면 201 Created와 Location 헤더를 반환한다")
    void create_reservation_returns_201() throws Exception {
        String userName = "namu";
        //given
        ReservationRequest request = new ReservationRequest(
                userName,
                LocalDate.parse("2027-05-01"),
                1L,
                1L);
        ReservationResponse response = new ReservationResponse(
                1L,
                userName,
                LocalDate.parse("2027-05-01"),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "theme1", "description,description.", "http://thumbnail.jpg")
        );

        given(reservationService.create(any(ReservationRequest.class)))
                .willReturn(response);

        //when&then
        mockMvc.perform(
                        post("/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/reservations/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userName));
    }

    @Test
    @DisplayName("모든 예약을 조회하면 200 OK를 반환한다")
    void find_all_reservations_returns_200() throws Exception {
        //given
        ReservationResponse reservation1 = new ReservationResponse(
                1L,
                "namu",
                LocalDate.parse("2027-05-01"),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "theme1", "description,description.", "http://thumbnail1.jpg")
        );
        ReservationResponse reservation2 = new ReservationResponse(
                2L,
                "john",
                LocalDate.parse("2027-05-02"),
                new ReservationTimeResponse(2L, LocalTime.of(14, 0)),
                new ThemeResponse(2L, "theme2", "description,description.", "http://thumbnail2.jpg")
        );

        given(reservationService.findReservation(null))
                .willReturn(List.of(reservation1, reservation2));

        //when&then
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("namu"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("john"));
    }

    @Test
    @DisplayName("이름으로 예약을 검색하면 200 OK를 반환한다")
    void find_reservations_by_name_returns_200() throws Exception {
        //given
        String searchName = "namu";
        ReservationResponse reservation = new ReservationResponse(
                1L,
                "namu",
                LocalDate.parse("2027-05-01"),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "theme1", "description,description.", "http://thumbnail.jpg")
        );

        given(reservationService.findReservation(searchName))
                .willReturn(List.of(reservation));

        //when&then
        mockMvc.perform(get("/reservations")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("namu"));
    }

    @Test
    @DisplayName("예약을 수정하면 204 No Content를 반환한다")
    void update_reservation_returns_204() throws Exception {
        //given
        Long reservationId = 1L;
        ReservationUpdateRequest request = new ReservationUpdateRequest(
                LocalDate.parse("2027-05-02"),
                2L
        );

        doNothing().when(reservationService).update(eq(reservationId), any(ReservationUpdateRequest.class));

        //when&then
        mockMvc.perform(
                        patch("/reservations/{id}", reservationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("예약을 삭제하면 204 No Content를 반환한다")
    void delete_reservation_returns_204() throws Exception {
        //given
        Long reservationId = 1L;

        doNothing().when(reservationService).delete(reservationId);

        //when&then
        mockMvc.perform(delete("/reservations/{id}", reservationId))
                .andExpect(status().isNoContent());
    }
}
