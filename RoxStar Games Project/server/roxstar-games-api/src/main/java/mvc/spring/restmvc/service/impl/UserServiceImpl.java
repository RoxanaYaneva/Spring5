package mvc.spring.restmvc.service.impl;

import lombok.extern.slf4j.Slf4j;
import mvc.spring.restmvc.dao.UserRepository;
import mvc.spring.restmvc.exception.EntityNotFoundException;
import mvc.spring.restmvc.model.Role;
import mvc.spring.restmvc.model.User;
import mvc.spring.restmvc.model.enums.UserProfile;
import mvc.spring.restmvc.service.RoleService;
import mvc.spring.restmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private RoleService roleService;

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(String id) {
        if (id == null) return null;
        return repo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%s does not exist", id)));
    }

    @Override
    public User createUser(User user) {
        Optional<User> result = repo.findByEmail(user.getEmail());
        if (result.isPresent()) {
            return result.get();
//            throw new UserAlreadyExistsException(String.format("User with email=%s already exists.", user.getEmail()));
        } else {
            user.setRegistered(LocalDateTime.now());
            user.setUpdated((LocalDateTime.now()));
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(Arrays.asList(roleService.getRoleByName(UserProfile.CUSTOMER.getDescription()).get()));
            } else {
                List<Role> expandedRoles = user.getRoles().stream()
                        .map(role -> roleService.getRoleByName(role.getUserProfile().getDescription()))
                        .filter(roleOpt -> roleOpt.isPresent())
                        .map(roleOpt -> roleOpt.get())
                        .collect(Collectors.toList());
                log.info(">>> Expanded roles: {}", expandedRoles);
                user.setRoles(expandedRoles);
            }
            user.setActive(true);
            log.info("Creating default user: {}", user);
            return repo.insert(user);
        }
    }

    @Override
    public User updateUser(User user) {
        user.setUpdated(LocalDateTime.now());
        return repo.save(user);
    }

    @Override
    public User deleteUser(String id) {
        User old = getUserById(id);
        repo.deleteById(id);
        return old;
    }
}