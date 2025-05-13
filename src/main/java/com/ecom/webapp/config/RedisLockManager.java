//package com.ecom.webapp.config;
//
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Component
//public class RedisLockManager {
//    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
//
//    public boolean tryAcquire(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
//        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
//        boolean acquired = lock.tryLock(waitTime, unit);
//        if (acquired) {
//            // Tạo luồng tự động giải phóng sau thời gian leaseTime
//            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//                releaseLock(key);
//            }, leaseTime, unit);
//        }
//        return acquired;
//    }
//
//    public void releaseLock(String key) {
//        ReentrantLock lock = locks.get(key);
//        if (lock != null && lock.isHeldByCurrentThread()) {
//            lock.unlock();
//        }
//    }
//}
