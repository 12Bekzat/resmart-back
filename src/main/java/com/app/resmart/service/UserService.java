package com.app.resmart.service;

import com.app.resmart.dto.RegUserDto;
import com.app.resmart.entity.Role;
import com.app.resmart.entity.User;
import com.app.resmart.repository.RoleRepo;
import com.app.resmart.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь %s не найден", username)
        ));

        org.springframework.security.core.userdetails.User user1 = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), !user.isBanned(), true, true, true,
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));

        return user1;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createNewUser(RegUserDto regUserDto) {
        User user = new User();
        user.setUsername(regUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(regUserDto.getPassword()));
        user.setName(regUserDto.getName());
        user.setAddress(regUserDto.getAddress());
        user.setContactPerson(regUserDto.getContactPerson());
        user.setWorkTime(regUserDto.getWorkTime());
        user.setPhone(regUserDto.getContactPhone());
        user.setDescText(regUserDto.getDescText());
        user.setLogotype(regUserDto.getLogotype());
        user.setBanned(true);
        user.setCreatedAt((LocalDateTime.now()).toString());
        user.setEmail(regUserDto.getEmail());
        user.setRoles(List.of(roleRepository.findByName(regUserDto.getRole()).get()));
        return userRepository.save(user);
    }

    public void saveUser(User user) {
        if (user == null) return;
        userRepository.save(user);
    }

    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    public void saveUserWithEncrypt(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void setRoles() {
        Role admin = new Role();
        admin.setName("ROLE_ADMIN");
        Role rest = new Role();
        rest.setName("ROLE_REST");
        Role post = new Role();
        post.setName("ROLE_POST");
        roleRepository.saveAll(List.of(admin, post, rest));
    }

    public void createAdmin() {
        User admin = new User();
        admin.setName("Admin");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin1234"));
        admin.setRoles(List.of(roleRepository.findByName("ROLE_ADMIN").get()));
        userRepository.save(admin);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
