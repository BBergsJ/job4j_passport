package ru.job4j.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.domain.Passport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PassportRepository extends CrudRepository<Passport, Integer> {
    Optional<Passport> findBySerial(int serial);
    List<Passport> findAllByExpirationDateBefore(LocalDateTime localDateTime);
    List<Passport> findAllByExpirationDateBetween(LocalDateTime starts, LocalDateTime ends);
}
