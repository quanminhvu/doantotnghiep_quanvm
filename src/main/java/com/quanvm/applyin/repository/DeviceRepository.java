package com.quanvm.applyin.repository;

import com.quanvm.applyin.entity.Device;
import com.quanvm.applyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByUserAndDeviceId(User user, String deviceId);
    
    Optional<Device> findByUserAndDeviceToken(User user, String deviceToken);
    
    List<Device> findByUserAndIsActiveTrue(User user);
    
    List<Device> findByUser(User user);
    
    void deleteByUserAndDeviceId(User user, String deviceId);
    
    void deleteByUserAndDeviceToken(User user, String deviceToken);
}
