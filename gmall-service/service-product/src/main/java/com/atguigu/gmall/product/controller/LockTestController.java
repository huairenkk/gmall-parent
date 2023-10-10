package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.LockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/admin/product/test")
public class LockTestController {
    @Autowired
    private LockTestService lockTestService;

    @GetMapping("read")
    public Result<String> read(){
        String msg = lockTestService.readLock();

        return Result.ok(msg);
    }


    @GetMapping("write")
    public Result<String> write(){
        String msg = lockTestService.writeLock();

        return Result.ok(msg);
    }


    @GetMapping("/testLock")
    public Result testLock() {
        lockTestService.testLock();
        return Result.ok();
    }

}
