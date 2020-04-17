package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;  //这块容易自动倒错，倒成另外一个
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value="/payment/create")
    public CommonResult create(@RequestBody Payment payment){    // 这里为什么加@RequestBody？不加得话，80端口插入插不进去？
                                                                //  因为restTemplate发送的post请求是json格式，JSON格式要用@RequestBody接收
        int result = paymentService.create(payment);
        log.info("*****插入成功："+ result);
        if(result > 0) {
            return new CommonResult(200, "插入数据成功,serverPort: "+serverPort,result);
        } else {
            return new CommonResult(444, "插入数据失败",null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("*****查询成功："+ payment);
        if(payment != null) {
            return new CommonResult(200, "查询成功,serverPort: "+serverPort,payment);
        } else {
            return new CommonResult(444, "没有对应记录，查询ID："+id,null);
        }
    }

    @GetMapping(value= "/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();  //得到所有微服务

        for (String element : services) {
            log.info("*****element:" + element);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE"); //得到一个具体微服务的所有实例
        for (ServiceInstance instance : instances){
            log.info(instance.getInstanceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;
    }
}
