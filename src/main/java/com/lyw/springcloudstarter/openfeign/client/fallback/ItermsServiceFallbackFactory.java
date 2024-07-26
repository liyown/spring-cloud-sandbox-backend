package com.lyw.springcloudstarter.openfeign.client.fallback;

import com.lyw.springcloudstarter.openfeign.client.ItemDTO;
import com.lyw.springcloudstarter.openfeign.client.RemoteItermService;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: liuyaowen
 * @poject: hmall
 * @create: 2024-07-11 13:39
 * @Description: fallback factory example
 */
public class ItermsServiceFallbackFactory implements FallbackFactory<RemoteItermService> {

    @Override
    public RemoteItermService create(Throwable throwable) {
        System.out.println("fallbackFactory");
        return new RemoteItermService() {
            @Override
            public List<ItemDTO> queryItermByIDs(Collection<Long> ids) {
                System.out.println("fallback");
                throw new RuntimeException("远程调用失败");
            }

            @Override
            public void deductStock(List<Map<String, Object>> items) {
                throw new RuntimeException("远程调用失败");
            }

            @Override
            public void updateItem(ItemDTO item) {
                throw new RuntimeException("远程调用失败");
            }
        };
    }
}
