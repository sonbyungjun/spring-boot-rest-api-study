package me.byungjun.demorestapi.configs;

import java.util.Set;
import me.byungjun.demorestapi.accounts.Account;
import me.byungjun.demorestapi.accounts.AccountRole;
import me.byungjun.demorestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Appconfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {

      @Autowired
      AccountService accountService;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        Account byungjun = Account.builder()
            .email("saint2030@naver.com")
            .password("byungjun")
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
        accountService.saveAccount(byungjun);
      }
    };
  }

}
