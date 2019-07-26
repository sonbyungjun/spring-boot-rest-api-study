package me.byungjun.demorestapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import me.byungjun.demorestapi.accounts.Account;
import me.byungjun.demorestapi.accounts.AccountRepository;
import me.byungjun.demorestapi.accounts.AccountRole;
import me.byungjun.demorestapi.accounts.AccountService;
import me.byungjun.demorestapi.common.AppProperties;
import me.byungjun.demorestapi.common.BaseControllerTest;
import me.byungjun.demorestapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

public class EventControllerTests extends BaseControllerTest {

  @Autowired
  EventRepository eventRepository;

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  AppProperties appProperties;

  @Before
  public void setUp() {
    this.eventRepository.deleteAll();
    this.accountRepository.deleteAll();
  }

  @Test
  @TestDescription("정상적으로 이벤트를 생성하는 테스트")
  public void createEvent() throws Exception {
    EventDto event = EventDto.builder()
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .beginEventDateTime(LocalDateTime.of(2019, 07, 21, 10, 00))
        .endEventDateTime(LocalDateTime.of(2019, 07, 25, 10, 00))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .build();

    mockMvc.perform(post("/api/events/")
              .header(HttpHeaders.AUTHORIZATION, getBearerToken())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .accept(MediaTypes.HAL_JSON)
              .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("free").value(false))
        .andExpect(jsonPath("offline").value(true))
        .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        .andDo(document("create-event",
            links(
                linkWithRel("self").description("link to self"),
                linkWithRel("query-events").description("link to query events"),
                linkWithRel("update-event").description("link to update an existing event"),
                linkWithRel("profile").description("link to profile")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.ACCEPT).description("Accept header"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type header")
            ),
            requestFields(
                fieldWithPath("name").description("Name of new event"),
                fieldWithPath("description").description("description of new event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of close of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
            ),
            responseHeaders(
                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
            ),
            relaxedResponseFields(
                fieldWithPath("id").description("identifier of new event"),
                fieldWithPath("name").description("Name of new event"),
                fieldWithPath("description").description("description of new event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of close of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                fieldWithPath("free").description("it tells is this event is free or not"),
                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                fieldWithPath("eventStatus").description("event status"),
                fieldWithPath("_links.self.href").description("link to self"),
                fieldWithPath("_links.query-events.href").description("link to query event list"),
                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                fieldWithPath("_links.profile.href").description("link to profile")
            )
        ));
  }

  private String getBearerToken(boolean needToCreateAccount) throws Exception {
    return "Bearer " + getAccessToken(needToCreateAccount);
  }

  private String getAccessToken(boolean needToCreateAccount) throws Exception {
    // Given
    if (needToCreateAccount) {
      createAccount();
    }

    ResultActions perform = this.mockMvc.perform(post("/oauth/token")
        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSercet()))
        .param("username", appProperties.getUserUsername())
        .param("password", appProperties.getUserPassword())
        .param("grant_type", "password"));
    var responseBody = perform.andReturn().getResponse().getContentAsString();
    Jackson2JsonParser parser = new Jackson2JsonParser();
    return parser.parseMap(responseBody).get("access_token").toString();
  }

  private Account createAccount() {
    Account byungjun = Account.builder()
        .email(appProperties.getUserUsername())
        .password(appProperties.getUserPassword())
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
        .build();
    return accountService.saveAccount(byungjun);
  }

  private String getBearerToken() throws Exception {
    return getBearerToken(true);
  }

  @Test
  @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request() throws Exception {
    Event event = Event.builder()
        .id(100)
        .name("Spring")
        .description("REST Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
        .beginEventDateTime(LocalDateTime.of(2019, 07, 21, 10, 00))
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
          .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("content[0].objectName").exists())
        .andExpect(jsonPath("content[0].defaultMessage").exists())
        .andExpect(jsonPath("content[0].code").exists())
        .andExpect(jsonPath("_links.index").exists());
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When
    this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("page").exists())
        .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("query-events"));
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEventsWithAuthentication() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When
    this.mockMvc.perform(get("/api/events")
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .param("page", "1")
        .param("size", "10")
        .param("sort", "name,DESC")
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("page").exists())
        .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andExpect(jsonPath("_links.create-event").exists())
        .andDo(document("query-events"));
  }

  @Test
  @TestDescription("기존의 이벤트를 하나 조회하기")
  public void getEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(100, account);

    // When
    this.mockMvc.perform(get("/api/events/{id}", event.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").exists())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("get-an-event"));
  }

  @Test
  @TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
  public void getEvent404() throws Exception {
    // When & Then
    this.mockMvc.perform(get("/api/events/11883"))
        .andExpect(status().isNotFound());
  }

  @Test
  @TestDescription("이벤트를 정상적으로 수정하기")
  public void updateEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(200, account);

    EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    String eventName = "Updated Event";
    eventDto.setName(eventName);

    // When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value(eventName))
        .andExpect(jsonPath("_links.self").exists());
  }

  @Test
  @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
  public void updateEvent400_Empty() throws Exception {
    // Given
    Event event = this.generateEvent(200);

    EventDto eventDto = new EventDto();
    // When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
  public void updateEvent400_Wrong() throws Exception {
    // Given
    Event event = this.generateEvent(200);

    EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    eventDto.setBasePrice(20000);
    eventDto.setMaxPrice(1000);

    // When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("존재하지 않는 이벤트 수정 실패")
  public void updateEvent404() throws Exception {
    // Given
    Event event = this.generateEvent(200);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);

    // When & Then
    this.mockMvc.perform(put("/api/events/123123", event.getId())
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  private Event generateEvent(int index, Account account) {
    Event event = buildEvent(index);
    event.setManager(account);
    return this.eventRepository.save(event);
  }

  private Event generateEvent(int index) {
    Event event = buildEvent(index);
    return this.eventRepository.save(event);
  }

  private Event buildEvent(int index) {
    return Event.builder()
          .name("event" + index)
          .description("test event")
          .beginEnrollmentDateTime(LocalDateTime.of(2019, 07, 19, 10, 10))
          .closeEnrollmentDateTime(LocalDateTime.of(2019, 07, 20, 10, 10))
          .beginEventDateTime(LocalDateTime.of(2019, 07, 21, 10, 00))
          .endEventDateTime(LocalDateTime.of(2019, 07, 25, 10, 00))
          .basePrice(100)
          .maxPrice(200)
          .limitOfEnrollment(100)
          .location("강남역 D2 스타텁 팩토리")
          .free(false)
          .offline(true)
          .eventStatus(EventStatus.DRAFT)
          .build();
  }

}
