package com.connect_us.backend.service.account.impl;

import com.connect_us.backend.domain.account.Account;
import com.connect_us.backend.domain.account.AccountRepository;
import com.connect_us.backend.domain.enums.Gender;
import com.connect_us.backend.domain.enums.Role;
import com.connect_us.backend.domain.enums.Social;
import com.connect_us.backend.security.dto.AccountDto;
import com.connect_us.backend.service.account.AccountService;
import lombok.RequiredArgsConstructor;
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

}