package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestParam String username,
                                           @RequestParam String phone,
                                           @RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam(required = false) MultipartFile imageFile) throws IOException {
        User user = User.builder()
                .username(username)
                .phone(phone)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        User savedUser = userService.saveUser(user, imageFile);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}