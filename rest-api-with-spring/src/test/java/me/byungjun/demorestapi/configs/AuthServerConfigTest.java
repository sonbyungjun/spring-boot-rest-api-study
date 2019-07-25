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
import me.byungjun.demorestapi.common.BaseControllerTest;
import me.byungjun.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  AccountService accountService;

  @Test
  @TestDescription("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {
    // Given
    String username = "saint@naver.com";
    String password = "byungjun";
    Account byungjun = Account.builder()
        .email(username)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
        .build();
    accountService.saveAccount(byungjun);

    String clientID = "myApp";
    String clientSecret = "pass";

      this.mockMvc.perform(post("/oauth/token")
          .with(httpBasic(clientID, clientSecret))
          .param("username", username)
          .param("password", password)
          .param("grant_type", "password"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("access_token").exists());
  }

}