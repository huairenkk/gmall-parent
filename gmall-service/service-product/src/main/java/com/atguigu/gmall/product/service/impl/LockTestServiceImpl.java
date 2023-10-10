package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.LockTestService;
import jodd.time.TimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class LockTestServiceImpl implements LockTestService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    //    public synchronized void testLock() {
//        // 查询redis中的num值
//        String value = (String)this.redisTemplate.opsForValue().get("num");
//        // 没有该值return
//        if (StringUtils.isBlank(value)){
//            return ;
//        }
//        // 有值就转成成int
//        int num = Integer.parseInt(value);
//        // 把redis中的num值+1
//        this.redisTemplate.opsForValue().set("num", String.valueOf(++num));
//
//    }

    /**
     * redis实现分布式锁
     * 尝试获得锁
     * 判断是否获得锁
     * 如果没有获得锁
     * 等待重试
     * 自旋
     * 释放锁
     */
//    @Override
//    @SneakyThrows
//    public void testLock() {
//        //尝试获得锁
////        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", "111");
////        无法保证原子性
////        redisTemplate.expire("lock",7,TimeUnit.SECONDS);
//        String id = UUID.randomUUID().toString().replaceAll("-","");
//
//        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", id, 7, TimeUnit.SECONDS);
//        //判断是否活得锁
//        if (flag) {
//            // 查询redis中的num值
//            String value = redisTemplate.opsForValue().get("num");
//            // 没有该值return
//            if (StringUtils.isBlank(value)) {
//                return;
//            }
//            // 有值就转成成int
//            int num = Integer.parseInt(value);
//            // 把redis中的num值+1
//            redisTemplate.opsForValue().set("num", String.valueOf(++num));
//
//            //判断
//            if (id.equals(redisTemplate.opsForValue().get("lock"))){
//                //释放锁
//                redisTemplate.delete("lock");
//            }
//
//
//
//        } else {
//            Thread.sleep(100);
//            //重试
//            testLock();
//        }
//
//
//
//    }


    /**redisson实现分布式锁
     * 创建锁
     * 加锁
     * 处理业务
     * 解锁
     */
    @Autowired
    private RedissonClient redissonClient;
    @Override
    @SneakyThrows
    public void testLock() {
        RLock lock = redissonClient.getLock("lock");
        boolean flag = lock.tryLock(100,10,TimeUnit.SECONDS);
        //判断是获得锁
        if (flag) {
            try {
                // 查询redis中的num值
                String value = redisTemplate.opsForValue().get("num");
                // 没有该值return
                if (StringUtils.isBlank(value)) {
                    return;
                }
                // 有值就转成成int
                int num = Integer.parseInt(value);
                // 把redis中的num值+1
                redisTemplate.opsForValue().set("num", String.valueOf(++num));
            } finally {
                lock.unlock();
            }


        } else {
            Thread.sleep(100);
            //重试
            testLock();
        }
    }


    @Override
    public String readLock() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("readWriteLock");
        RLock readLock = readWriteLock.readLock();
        readLock.lock(10,TimeUnit.SECONDS);
        String msg = redisTemplate.opsForValue().get("msg");
        return msg;
    }

    @Override
    public String writeLock() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("readWriteLock");
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock(10, TimeUnit.SECONDS);
        String msg = UUID.randomUUID().toString().replaceAll("-","");
        redisTemplate.opsForValue().set("msg",msg);
        return "写入了数据"+msg;
    }

}
