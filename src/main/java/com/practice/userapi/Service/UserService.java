package com.practice.userapi.Service;

import com.practice.userapi.DTO.UserResponse;
import com.practice.userapi.Entity.User;
import com.practice.userapi.Exception.ResourceNotFoundException;
import com.practice.userapi.Repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;




    @Cacheable(value = "users", unless = "#result == null")
    public UserResponse fetchUsersFromExternalApi() {
        String url = "https://dummyjson.com/users";
        return restTemplate.getForObject(url, UserResponse.class);
    }

    /**
     * Load users from an external API and save them to the database.
     */
    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackFetchUsers")
    @Retry(name = "userService", fallbackMethod = "fallbackFetchUsers")
    public void loadUsersFromExternalApi() {
        logger.info("Starting to load users from external API");

        String url = "https://dummyjson.com/users";
        UserResponse response = fetchUsersFromExternalApi();

        if (response != null && response.getUsers() != null) {
            logger.debug("Successfully fetched {} users from external API", response.getUsers().size());

            // Reset IDs to null to avoid conflicts during save
            response.getUsers().forEach(user -> user.setId(null));

            List<User> users = response.getUsers();
            userRepository.saveAll(response.getUsers());

            logger.info("Finished loading users from external API");
        } else {
            logger.error("Failed to load users from external API: No data received");
            throw new RuntimeException("Failed to load users from external API");
        }
    }

    public String fallbackFetchUsers(Exception e) {
        return "Fallback response: Service is temporarily unavailable. Please try again later.";
    }

    /**
     * Search users by firstName, lastName, or ssn using a free text query.
     *
     * @param query The search query.
     * @return A list of users matching the query.
     */
    public List<User> searchUsers(String query) {
        logger.info("Searching users with query: {}", query);

        List<User> users = userRepository.searchUsers(query);

        logger.debug("Found {} users matching query: {}", users.size(), query);
        return users;
    }

    /**
     * Find a user by ID or email.
     *
     * @param idOrEmail The ID or email of the user.
     * @return The user with the specified ID or email.
     * @throws ResourceNotFoundException If no user is found.
     */
    public User getUserByIdOrEmail(String idOrEmail) {
        logger.info("Finding user by ID or email: {}", idOrEmail);

        try {
            Long id = Long.parseLong(idOrEmail);
            logger.debug("Searching user by ID: {}", id);

            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new ResourceNotFoundException("User not found with id: " + id);
                    });

            logger.debug("Found user by ID: {}", user);
            return user;
        } catch (NumberFormatException e) {
            logger.debug("Searching user by email: {}", idOrEmail);

            User user = userRepository.findByEmail(idOrEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", idOrEmail);
                        return new ResourceNotFoundException("User not found with email: " + idOrEmail);
                    });

            logger.debug("Found user by email: {}", user);
            return user;
        }
    }
}