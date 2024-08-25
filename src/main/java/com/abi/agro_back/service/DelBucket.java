package com.abi.agro_back.service;

import com.abi.agro_back.config.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DelBucket {
    @Autowired
    StorageService storageService;
    public void delBuck(){
        storageService.deleteBucket();
    }

}
