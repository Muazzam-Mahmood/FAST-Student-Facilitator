package com.fast.fsf.identity.web;

import com.fast.fsf.identity.domain.User;
import com.fast.fsf.identity.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user-related operations.
 * Handles HTTP requests for listing, creating, and managing users.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * GET /api/users/count
     */
    @GetMapping("/count")
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * POST /api/users
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * PUT /api/users/{id}/ban
     * Toggles the banned status of a user.
     */
    @PutMapping("/{id}/ban")
    public User toggleBan(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                user.setBanned(!user.isBanned());
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
