package com.abi.agro_back.controller;

import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.collection.User;
import com.abi.agro_back.config.MailSender;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "the User Endpoint")
public class UserController {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<User> createUser(@Validated @RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public org.springframework.data.domain.Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int sizePerPage,
                                                                  @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                                                  @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return userService.getAllUsers(PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()));
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String  userId,
                                              @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") String  id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException(user.getEmail() + " not found");
        }
        String token = java.util.UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        mailSender.sendResetEmail(token, user.getEmail());

        return ResponseEntity.ok("Link for reset password sent to email");
    }

    @GetMapping("/resetPasswordAdmin/{id}")
    public ResponseEntity<String> resetPasswordForAdmin(@PathVariable("id") String userId) {
        User user = userService.getUserById(userId);

        userService.changeUserPassword(user, "11111111");
        mailSender.sendEmail(user.getEmail(), "11111111");

        return ResponseEntity.ok("Link for reset password sent to email");
    }

    @GetMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam("token") String token,
                                               @RequestParam("password") String password) {
        String result = userService.validatePasswordResetToken(token);

        if(result != null) {
            return ResponseEntity.ok("Password saved");
        }

        User user = userService.getUserByPasswordResetToken(token);
        if(user != null) {
            userService.changeUserPassword(user, password);
            return ResponseEntity.ok("Password Updated");
        } else {
            return ResponseEntity.ok("Password have not updated");
        }
    }

    @GetMapping("/check/{oblast}")
    public ResponseEntity<Boolean> checkOblastApprove(@PathVariable("oblast") String oblast) {
        Boolean isApprove = userService.checkOblastApprove(oblast);
        return ResponseEntity.ok(isApprove);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
