package eformer.back.eformer_backend.api.v1;

import eformer.back.eformer_backend.model.User;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.JwtService;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;


@RestController
public class BaseApi {
    final JwtService jService;

    final UserRepository userRepo;

    public BaseApi(JwtService jService, UserRepository userRepo) {
        this.jService = jService;
        this.userRepo = userRepo;
    }

    public User extractUser(HashMap<String, String> header) {
        String token = header.get("authorization").split(" ")[1];

        if (jService.isTokenExpired(token)) {
            throw new RuntimeException("Expired token");
        }

        return userRepo
                .findByUsername(jService.extractUsername(token))
                .orElseThrow();
    }

    public boolean canUserChange(HashMap<String, String> header) {
        var user = extractUser(header);

        return user.isEmployee() || user.isManager();
    }

    public boolean isManager(HashMap<String, String> header) {
        var user = extractUser(header);
        return user.isManager();
    }

    public Date processToDate(String date) {
        return Date.from(LocalDateTime.parse(date.split("\\.")[0]).atZone(ZoneId.systemDefault()).toInstant());
    }
}
