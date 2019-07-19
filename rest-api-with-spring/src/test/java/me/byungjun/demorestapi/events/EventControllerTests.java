package me.byungjun.demorestapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void createEvent() throws Exception {
    EventDto event = EventDto.builder()
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .beginEventDateTime(LocalDateTime.of(2019, 07, 20, 10, 00))
        .endEventDateTime(LocalDateTime.of(2019, 07, 25, 10, 00))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .build();

    mockMvc.perform(post("/api/events/")
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .accept(MediaTypes.HAL_JSON)
              .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("id").value(Matchers.not(100)))
        .andExpect(jsonPath("free").value(Matchers.not(true)))
        .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT)));
  }

  @Test
  public void createEvent_Bad_Request() throws Exception {
    Event event = Event.builder()
        .id(100)
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .beginEventDateTime(LocalDateTime.of(2019, 07, 20, 10, 00))
        .endEventDateTime(LocalDateTime.of(2019, 07, 25, 10, 00))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .free(true)
        .offline(false)
        .eventStatus(EventStatus.PUBLISHED)
        .build();

    mockMvc.perform(post("/api/events/")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void createEvent_Bad_Request_Wrong_Input() throws Exception {
    EventDto eventDto = EventDto.builder()
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 26, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .beginEventDateTime(LocalDateTime.of(2019, 07, 24, 10, 00))
        .endEventDateTime(LocalDateTime.of(2019, 07, 23, 10, 00))
        .basePrice(10000)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .build();

    this.mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andExpect(status().isBadRequest());
  }

}
