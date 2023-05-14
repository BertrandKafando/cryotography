package com.example.auditjpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserApi {

        private final UserMRepository userMRepository;
        private final HistoryRepository historyRepository;

       @PostMapping("/add")
    public  UserM add(UserM userM){
           historyRepository.save(new History("UserM", userM.getId().toString(), "ADD", "User Added"));
        return userMRepository.save(userM);
    }

    @PutMapping("/update")
    public  UserM update(UserM userM){
        return userMRepository.save(userM);
    }
}
