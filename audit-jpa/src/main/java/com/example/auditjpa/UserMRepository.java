package com.example.auditjpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMRepository extends JpaRepository<UserM, Long> {
}
