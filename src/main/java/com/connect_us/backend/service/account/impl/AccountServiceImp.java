package com.connect_us.backend.service.account.impl;

import com.connect_us.backend.domain.account.Account;
import com.connect_us.backend.domain.account.AccountRepository;
import com.connect_us.backend.domain.enums.Gender;
import com.connect_us.backend.domain.enums.Role;
import com.connect_us.backend.domain.enums.Social;
import com.connect_us.backend.security.dto.AccountDto;
import com.connect_us.backend.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {
    private final AccountRepository accountRepository;
    @Override
    public void createUser(Account account){
        accountRepository.save(account);
    }

    /**
     * 회원정보 저장
     *
     * @param //infoDto 회원정보가 들어있는 DTO
     * @return 저장되는 회원의 PK
     */
    @Transactional
    public Long save(AccountDto accountDto){
        validateDuplicate(accountDto);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        accountDto.setPassword(encoder.encode(accountDto.getPassword()));

        Gender gender;
        if(accountDto.getGender()==Gender.MALE){
            gender= Gender.MALE;
        }
        else{
            gender=Gender.FEMALE;
        }
        return accountRepository.save(Account.builder()
                .email(accountDto.getEmail())
                .name(accountDto.getName())
                .social(Social.FALSE)
                .role(Role.USER)
                .gender(gender)
                .password(accountDto.getPassword()).build()).getId();
    }

    //중복 회원 검사
    private void validateDuplicate(AccountDto accountDto){
        Account findAccount = accountRepository.findByEmail(accountDto.getEmail());
        if(findAccount!=null){
            throw new IllegalStateException("이미 존재하는 회원 이메일 입니다.");
        }
    }

    public Account findByEmail(String email){
        return accountRepository.findByEmail(email);
    }

    public Account findOne(Long id){return accountRepository.findById(id).orElse(null);}
    //회원정보 업데이트
    @Transactional
    public void update(String name, String addr, String phone, Gender gender){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email);
        account.setName(name);
        account.setAddr(addr);
        account.setPhone(phone);
        account.setGender(gender);
        //Member을 반환하면 update 하면서 영속성 상태가 끊김(query 날리니까)
    }
}
