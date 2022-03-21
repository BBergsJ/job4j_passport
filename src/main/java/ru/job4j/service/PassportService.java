package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.domain.Passport;
import ru.job4j.repository.PassportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PassportService {

    private final PassportRepository passportRepository;

    public PassportService(PassportRepository passportRepository) {
        this.passportRepository = passportRepository;
    }

    public Passport save(Passport passport) {
        return passportRepository.save(passport);
    }

    public boolean delete(int id) {
        boolean rsl = false;
        if (passportRepository.existsById(id)) {
            passportRepository.deleteById(id);
            rsl = true;
        }
        return rsl;
    }

    public List<Passport> findAll() {
        return (List<Passport>) passportRepository.findAll();
    }

    public List<Passport> findBySerial(int serial) {
        return passportRepository.findBySerial(serial);
    }

    public Optional<Passport> findById(int id) {
        return passportRepository.findById(id);
    }

    public List<Passport> findUnavailable() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return passportRepository.findAllByExpirationDateBefore(localDateTime);
    }

    public List<Passport> findForReplace() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMonths(3);
        return passportRepository.findAllByExpirationDateBetween(LocalDateTime.now(), localDateTime);
    }

    public Optional<Passport> findBySerialAndNumber(int serial, int number) {
        return passportRepository.findBySerialAndNumber(serial, number);
    }
}
