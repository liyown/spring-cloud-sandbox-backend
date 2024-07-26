package com.lyw.springcloudstarter.openfeign.client;

import com.lyw.springcloudstarter.openfeign.client.fallback.ItermsServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: liuyaowen
 * @poject: hmall
 * @create: 2024-07-01 15:58
 * @Description:
 */
@FeignClient(name = "null", url = "localhost:8081", fallbackFactory = ItermsServiceFallbackFactory.class, path = "items")
public interface RemoteItermService {

    @GetMapping()
    List<ItemDTO> queryItermByIDs(@RequestParam("ids") Collection<Long> ids);

    @PutMapping("/stock/deduct")
    void deductStock(@RequestBody List<Map<String, Object>> items);

    @PutMapping
    void updateItem(@RequestBody ItemDTO item);
}

