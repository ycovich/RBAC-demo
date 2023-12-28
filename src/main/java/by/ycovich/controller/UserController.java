package by.ycovich.controller;

import by.ycovich.dto.ChangePasswordRequest;
import by.ycovich.dto.UserDTO;
import by.ycovich.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {
    private final UserServiceImpl userService;
    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getProfile(
            @PathVariable("username") String username){
        UserDTO response = userService.getUser(username);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/whoami")
    public ResponseEntity<String> whoami(@AuthenticationPrincipal UserDetails userDetails){
        String response = userDetails.getUsername();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PatchMapping("/edit")
    public ResponseEntity<UserDTO> editProfile(
            @RequestBody UserDTO updatedUserDTO){
        UserDTO response = userService.updateUser(updatedUserDTO);
        return ResponseEntity
                .accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request){
        userService.changePassword(request);
        return ResponseEntity
                .accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body("password changed successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfile(){
        userService.deleteUser();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("profile deleted successfully");
    }

}
