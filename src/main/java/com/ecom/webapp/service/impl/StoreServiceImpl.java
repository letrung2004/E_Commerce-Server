package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.model.responseDto.StoreResponse;
import com.ecom.webapp.repository.AddressRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private MailService mailService;

    public void sendEmail(String username, String to, String storeName) {
        try {
            String subject = "Đăng ký cửa hàng trên Shopii thành công!";
            String emailContent = "Xin chào người dùng " + username + ",\n\n" +
                    "Chúc mừng bạn đã đăng ký cửa hàng \"" + storeName + "\" thành công trên Shopii.\n\n" +
                    "Thông tin cửa hàng của bạn đang được chờ xét duyệt bởi quản trị viên.\n" +
                    "Chúng tôi sẽ gửi email thông báo ngay khi cửa hàng được phê duyệt và kích hoạt.\n\n" +
                    "Cảm ơn bạn đã tin tưởng và đồng hành cùng Shopii!\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ Shopii";

            mailService.sendSimpleMessage(to, subject, emailContent);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendRejectStoreEmail(String username, String to) {
        try {
            String subject = "Kênh người bán của bạn không được xét duyệt - Shopii";
            String emailContent = "Xin chào " + username + ",\n\n" +
                    "Cảm ơn bạn đã đăng ký trở thành người bán trên nền tảng Shopii.\n\n" +
                    "Chúng tôi rất tiếc phải thông báo rằng yêu cầu đăng ký cửa hàng của bạn **không được xét duyệt** tại thời điểm này.\n\n" +
                    "Bạn có thể kiểm tra lại thông tin đã đăng ký hoặc liên hệ bộ phận hỗ trợ để biết thêm chi tiết.\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ quản trị Shopii.";

            mailService.sendSimpleMessage(to, subject, emailContent);

        } catch (Exception e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }
    }


    @Override
    public void createStore(StoreDto storeDto) {
        User owner = this.userRepository.getUserByUsername(storeDto.getUsername());
        if (owner == null) {
            throw new EntityNotFoundException("User not found with name " + storeDto.getUsername());
        }
        Address address = this.addressRepository.getAddressById(storeDto.getAddressId());
        if (address == null) {
            throw new EntityNotFoundException("Address not found with id " + storeDto.getAddressId());
        }
        Store store = new Store();
        store.setName(storeDto.getName());
        store.setDescription(storeDto.getDescription());
        store.setLogo(storeDto.getLogo());
        store.setPhoneNumber(storeDto.getPhoneNumber());
        store.setAddress(address);
        store.setOwner(owner);

        this.storeRepository.createStore(store);
        sendEmail(owner.getUsername(), owner.getEmail(), store.getName());
    }

    @Override
    public Store getStoreByUsername(String username) {
        Store store = this.storeRepository.getStoreByUsername(username);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with username: " + username);
        }
        return store;
    }

    private StoreResponse convertStoreToStoreResponse(Store store) {
        if (store == null) {return null;}
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setName(store.getName());
        storeResponse.setLogo(store.getLogo());
        storeResponse.setAddressLine(store.getAddress().getAddress());
        storeResponse.setId(store.getId());
        return storeResponse;
    }

    @Override
    public List<StoreResponse> getStores(Map<String, String> params) {
        List<Store> stores = this.storeRepository.getStores(params);
        return stores.stream().map(this::convertStoreToStoreResponse).collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getStoresUnprocessed() {
        return this.storeRepository.getStoresUnprocessed();
    }

    @Override
    public void updateStore(int store) {

    }

    @Override
    public void deleteStore(int storeId) {
        Store store = this.storeRepository.getStoreById(storeId);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with id " + storeId);
        }
        sendRejectStoreEmail(store.getOwner().getUsername(), store.getOwner().getEmail());
        this.storeRepository.deleteStore(store);

    }

    @Override
    public StoreResponse getStoreById(int storeId) {
        Store store = this.storeRepository.getStoreById(storeId);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with id " + storeId);
        }
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setId(storeId);
        storeResponse.setName(store.getName());
        storeResponse.setDescription(store.getDescription());
        storeResponse.setLogo(store.getLogo());
        storeResponse.setPhoneNumber(store.getPhoneNumber());
        storeResponse.setAddressLine(store.getAddress().getAddress());
        storeResponse.setOwnerName(store.getOwner().getUsername());
        return storeResponse;
    }

    @Override
    public int getStoreIdByUsername(String username) {
        Store store = this.storeRepository.getStoreByUsername(username);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with username: " + username);
        }
        return store.getId();
    }
}
