package com.ecom.webapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.model.responseDto.UserResponse;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private MailService mailService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.getUserByUsername(username);
    }

    @Override
    public UserResponse getUserResponseByUsername(String username) {
        User user = this.userRepository.getUserByUsername2(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserResponse(user);
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid User!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);

    }


    @Override
    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return this.userRepository.getById(id);
    }

    @Override
    public void createUser(UserDto userDto, String username,String rawPassword) {
        User user = new User();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(String.valueOf(userDto.getPhoneNumber()));
        user.setGender(userDto.isGender());
        user.setRole(userDto.getRole());
        user.setDateOfBirth(userDto.getDateOfBirth());

        this.userRepository.save(user);
    }

    @Override
    public void update(UserDto userDto) {
        User user = this.userRepository.getById(userDto.getId());

        if (user == null) {
            throw new EntityNotFoundException("User not found with id " + userDto.getId());
        }
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(String.valueOf(userDto.getPhoneNumber()));
        user.setGender(userDto.isGender());
        user.setRole(userDto.getRole());
        user.setDateOfBirth(userDto.getDateOfBirth());

        if (userDto.getFile() != null && !userDto.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(userDto.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        this.userRepository.update(user);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = this.userRepository.getById(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id " + id);
        }

        Store store = user.getStore();
        if (!user.isStoreActive()) {
            if(store != null) this.storeRepository.deleteStore(store);
        } else {
            if(store != null){
                store.setOwner(null);
                this.storeRepository.updateStore(store);
            }

        }

        this.userRepository.delete(user);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepository.authenticate(username, password);
    }

    @Override
    public User registerUser(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setFullName(params.get("fullName"));
        u.setUsername(params.get("username"));
        u.setPassword(passwordEncoder.encode(params.get("password")));
        u.setEmail(params.get("email"));
        u.setPhoneNumber(params.get("phoneNumber"));
        u.setRole("ROLE_CUSTOMER");
        u.setActive(true);
        if (avatar != null &&  !avatar.isEmpty()) {
            try {
                u.setAvatar(
                        cloudinary.uploader().upload(avatar.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto")
                        ).get("secure_url").toString()
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        else {
            u.setAvatar(null);
        }
        this.userRepository.save(u);
        return u;
    }



    public void sendApprovalEmail(String username, String to, String storeName) {
        try {
            String subject = "Cửa hàng của bạn đã được phê duyệt trên Shopii!";
            String emailContent = "Xin chào người dùng " + username + ",\n\n" +
                    "Chúc mừng! Cửa hàng \"" + storeName + "\" của bạn đã được quản trị viên phê duyệt và chính thức hoạt động trên nền tảng Shopii.\n\n" +
                    "Giờ đây bạn có thể bắt đầu đăng sản phẩm, quản lý đơn hàng và tương tác với khách hàng.\n\n" +
                    "Chúc bạn kinh doanh thành công và phát triển cùng Shopii!\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ Shopii";

            mailService.sendSimpleMessage(to, subject, emailContent);
        } catch (Exception e) {
            System.err.println("Failed to send approval email: " + e.getMessage());
        }
    }


    public void acceptStoreActivation(int userId) {
        User user = this.userRepository.getById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        user.setStoreActive(true);
        this.userRepository.update(user);
        sendApprovalEmail(user.getUsername(), user.getEmail(), user.getStore().getName());
    }
}


