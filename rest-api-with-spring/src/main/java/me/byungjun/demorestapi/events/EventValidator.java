package me.byungjun.demorestapi.events;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

  public void vaildate(EventDto eventDto, Errors errors) {
    if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
      errors.reject("wrongPrices", "Values fo prices are wrong");
    }

    LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
    if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
      errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
    }

    // TODO beginEventDateTime
    LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
    if (beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
      beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
      errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong.");
    }

    // TODO CloseEnrollmentDateTime
    LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
    if (closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
      errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong.");
    }

  }

}
