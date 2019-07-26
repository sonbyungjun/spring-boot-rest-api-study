package me.byungjun.demorestapi.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AccountAdapter extends User {

  private Account account;

  public AccountAdapter(Account account) {
    super(account.getEmail(), account.getPassword(), authrities(account.getRoles()));
    this.account = account;
  }

  private static Collection<? extends GrantedAuthority> authrities(Set<AccountRole> roles) {
    return roles.stream().map(r ->  new SimpleGrantedAuthority("ROLE_" + r.name())).collect(
        Collectors.toSet());
  }


  public Account getAccount() {
    return account;
  }
}
