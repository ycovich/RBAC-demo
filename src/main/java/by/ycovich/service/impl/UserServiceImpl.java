package by.ycovich.service.impl;

import by.ycovich.dto.PageOfUsersDTO;
import by.ycovich.enums.UserStatus;
import by.ycovich.exception.DeactivatedUserRequestException;
import by.ycovich.exception.InvalidPasswordException;
import by.ycovich.exception.NotMatchingConfirmationPasswordException;
import by.ycovich.exception.UserNotFoundException;
import by.ycovich.entity.UserEntity;
import by.ycovich.service.UserService;
import by.ycovich.util.UserMapper;
import by.ycovich.dto.ChangePasswordRequest;
import by.ycovich.dto.UserDTO;
import by.ycovich.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUser(int id) {
        checkUserStatus(getUserFromPrincipal().getActive());
        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        return userMapper.mapToDTO(user);
    }

    @Override
    public UserDTO getUser(String username) {
        checkUserStatus(getUserFromPrincipal().getActive());
        UserEntity user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        return userMapper.mapToDTO(user);
    }

    @Override
    public PageOfUsersDTO getAllUsers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserEntity> pageOfUsers = userRepository.findAll(pageable);
        List<UserEntity> listOfUsers = pageOfUsers.getContent();
        List<UserDTO> content = listOfUsers
                .stream()
                .map(userMapper::mapToDTO)
                .collect(Collectors.toList());

        return PageOfUsersDTO.builder()
                .content(content)
                .page(pageOfUsers.getNumber())
                .pageSize(pageOfUsers.getSize())
                .totalElements(pageOfUsers.getTotalElements())
                .totalPages(pageOfUsers.getTotalPages())
                .last(pageOfUsers.isLast())
                .build();
    }

    @Override
    public UserDTO updateUser(UserDTO updatedUserDTO) {
        var id = getUserFromPrincipal().getId();
        UserEntity userToBeUpdated = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException
                        ("[update failure]: user not found"));
        checkUserStatus(userToBeUpdated.getActive());

        userToBeUpdated.setUsername(updatedUserDTO.getUsername());
        userToBeUpdated.setEmail(updatedUserDTO.getEmail());
        UserEntity updatedUser = userRepository.save(userToBeUpdated);
        return userMapper.mapToDTO(updatedUser);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        var user = getUserFromPrincipal();
        checkUserStatus(user.getActive());

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new InvalidPasswordException("invalid password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new NotMatchingConfirmationPasswordException
                    ("confirmation password doesn't match with the entered one");
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        var user = getUserFromPrincipal();
        checkUserStatus(user.getActive());
        UserEntity userToBeDeactivated = userRepository
                .findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException
                        ("[delete failure]: user not found"));

        userToBeDeactivated.setActive(UserStatus.DEACTIVATED);
        userRepository.save(userToBeDeactivated);
    }

    @Override
    public void deleteUser(int id) {
        UserEntity userToBeDeactivated = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException
                        ("[delete failure]: user not found"));
        checkUserStatus(userToBeDeactivated.getActive());

        userToBeDeactivated.setActive(UserStatus.DEACTIVATED);
        userRepository.save(userToBeDeactivated);
    }

    @Override
    public void restoreUser(int id) {
        UserEntity userToBeRestored = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException
                        ("[delete failure]: user not found"));
        //if (userToBeRestored.getActive().equals(UserStatus.ACTIVE)) return;
        userToBeRestored.setActive(UserStatus.ACTIVE);
        userRepository.save(userToBeRestored);
    }


    private void checkUserStatus(UserStatus userStatus){
        if (userStatus.equals(UserStatus.DEACTIVATED))
            throw new DeactivatedUserRequestException("user is deactivated");
    }

    private UserEntity getUserFromPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return userRepository
                    .findByUsername(authentication
                            .getName())
                    .orElseThrow(() -> new UserNotFoundException
                            ("[delete failure]: user not found"));
        }
        return null;
    }

}
