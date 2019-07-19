package me.byungjun.demorestapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EventTest {

  @Test
  public void builder() {
    Event event = Event.builder()
        .name("INflearn Spring REST API")
        .description("REST API development with Spring")
        .build();
    assertThat(event).isNotNull();
  }

  @Test
  public void javaBean() {
    // Given
    String name = "Spring";
    String description = "Event";

    // When
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);

    // Then
    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getDescription()).isEqualTo(description);
  }
}