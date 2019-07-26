package me.byungjun.demorestapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import me.byungjun.demorestapi.accounts.Account;
import me.byungjun.demorestapi.accounts.CurrentUser;
import me.byungjun.demorestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

  private final EventRepository eventRepository;

  private final ModelMapper modelMapper;

  private final EventValidator eventValidator;

  public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
    this.eventRepository = eventRepository;
    this.modelMapper = modelMapper;
    this.eventValidator = eventValidator;
  }

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    eventValidator.vaildate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    event.setManager(currentUser);
    Event newEvent = this.eventRepository.save(event);
    ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    URI createdUri = selfLinkBuilder.toUri();

    EventResource eventResource = new EventResource(event);
    eventResource.add(linkTo(EventController.class).withRel("query-events"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity.created(createdUri).body(eventResource);
  }

  @GetMapping
  public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler, @CurrentUser Account account) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    User principal = (User) authentication.getPrincipal();
    Page<Event> page = this.eventRepository.findAll(pageable);
    var pagedResources = assembler.toResource(page, e -> new EventResource(e));
    pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
    if (account != null) {
      pagedResources.add(linkTo(EventController.class).withRel("create-event"));
    }
    return ResponseEntity.ok(pagedResources);
  }

  @GetMapping("/{id}")
  public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
    Optional<Event> optionalEvent = this.eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Event event = optionalEvent.get();
    EventResource eventResource = new EventResource(event);
    eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
    if (event.getManager().equals(currentUser)) {
      eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
    }

    return ResponseEntity.ok(eventResource);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {
    Optional<Event> optionalEvent = this.eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    this.eventValidator.vaildate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event existingEvent = optionalEvent.get();
    if (!existingEvent.getManager().equals(currentUser)) {
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    this.modelMapper.map(eventDto, existingEvent);
    Event savedEvent = this.eventRepository.save(existingEvent);

    EventResource eventResource = new EventResource(savedEvent);
    eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

    return ResponseEntity.ok(eventResource);
  }

  private ResponseEntity badRequest(Errors errors) {
    return ResponseEntity.badRequest().body(new ErrorsResource(errors));
  }

}
