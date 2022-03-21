package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Passport;
import ru.job4j.service.PassportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passport")
public class PassportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassportController.class.getSimpleName());
    private final PassportService passportService;
    private final ObjectMapper objectMapper;

    public PassportController(PassportService passportService, ObjectMapper objectMapper) {
        this.passportService = passportService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/save")
    public ResponseEntity<Passport> create(@RequestBody Passport passport) {
        Optional<Passport> rsl = passportService
                .findBySerialAndNumber(passport.getSerial(), passport.getNumber());
        if (rsl.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passport already exists");
        }
        return new ResponseEntity<>(
                this.passportService.save(passport),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/update")
    public ResponseEntity<Passport> update(@RequestParam int id, @RequestBody Passport passport) {
        var rsl = passportService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passport not found."));
        rsl.setSerial(passport.getSerial());
        rsl.setNumber(passport.getNumber());
        rsl.setFio(passport.getFio());
        rsl.setBirthDate(passport.getBirthDate());
        rsl.setExpirationDate(passport.getExpirationDate());
        return new ResponseEntity<>(
                passportService.save(rsl),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam int id) {
        return new ResponseEntity<>(
            passportService.delete(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/find")
    public ResponseEntity<List<Passport>> find(@RequestParam(required = false) String serial) {
        if (serial == null) {
            return new ResponseEntity<>(
                    passportService.findAll(),
                    HttpStatus.OK
            );
        } else {
            List<Passport> rsl = passportService.findBySerial(Integer.parseInt(serial));
            return new ResponseEntity<>(
                    rsl,
                    rsl.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/unavailable")
    public ResponseEntity<List<Passport>> findUnavailable() {
        return new ResponseEntity<>(
                passportService.findUnavailable(),
                HttpStatus.OK
        );
    }

    @GetMapping("/find-replaceable")
    public ResponseEntity<List<Passport>> findForReplace() {
        return new ResponseEntity<>(
                passportService.findForReplace(),
                HttpStatus.OK
        );
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}
