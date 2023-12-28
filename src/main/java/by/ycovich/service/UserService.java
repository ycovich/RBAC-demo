package by.ycovich.service;

import by.ycovich.dto.ChangePasswordRequest;
import by.ycovich.dto.PageOfUsersDTO;
import by.ycovich.dto.UserDTO;

public interface UserService {
    UserDTO getUser(int id);
    UserDTO getUser(String username);
    PageOfUsersDTO getAllUsers(int page, int pageSize);
    UserDTO updateUser(UserDTO updatedUserDTO);
    void changePassword(ChangePasswordRequest request);
    void deleteUser();
    void deleteUser(int id);
    void restoreUser(int id);
}
