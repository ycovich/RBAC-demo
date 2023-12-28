package by.ycovich.controller;

import by.ycovich.dto.UserDTO;
import by.ycovich.dto.PageOfUsersDTO;
import by.ycovich.service.UserService;
import by.ycovich.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    @Autowired
    public AdminController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<PageOfUsersDTO> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        PageOfUsersDTO response = userService.getAllUsers(page, pageSize);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable("id") int id){
        UserDTO response = userService.getUser(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int id){
        userService.deleteUser(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("user has been successfully deleted");
    }

    @PatchMapping("/users/{id}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable("id") int id){
        userService.restoreUser(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("the user has been successfully restored");
    }

}
