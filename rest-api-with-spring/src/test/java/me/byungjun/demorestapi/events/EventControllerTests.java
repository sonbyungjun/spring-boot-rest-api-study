package me.byungjun.demorestapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  EventRepository eventRepository;

  @Test
  public void createEvent() throws Exception {
    Event event = Event.builder()
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .endEventDateTime(LocalDateTime.of(2019, 07, 25, 10, 00))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .build();
    
    event.setId(10);
    Mockito.when(eventRepository.save(event)).thenReturn(event);

    mockMvc.perform(post("/api/events/")
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .accept(MediaTypes.HAL_JSON)
              .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").exists());
  }

}
