package me.byungjun.demorestapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import me.byungjun.demorestapi.accounts.Account;
import me.byungjun.demorestapi.accounts.AccountRole;
import me.byungjun.demorestapi.accounts.AccountService;
import me.byungjun.demorestapi.common.AppProperties;
import me.byungjun.demorestapi.common.BaseControllerTest;
import me.byungjun.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  AccountService accountService;

  @Autowired
  AppProperties appProperties;

  @Test
  @TestDescription("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {
    this.mockMvc.perform(post("/oauth/token")
        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSercet()))
        .param("username", appProperties.getUserUsername())
        .param("password", appProperties.getUserPassword())
        .param("grant_type", "password"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").exists());
  }

}