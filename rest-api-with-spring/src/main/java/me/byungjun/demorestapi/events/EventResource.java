package me.byungjun.demorestapi.events;

import org.springframework.hateoas.ResourceSupport;

public class EventResource extends ResourceSupport {

  private Event event;

  public EventResource(Event event) {
    this.event = event;
  }

  public Event getEvent() {
    return event;
  }
}
