package com.atguigu.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

//    @Autowired
//    OrderService orderService;
//
//    @Test
//    void contextLoads() {
//        OrderEntity entity = new OrderEntity();
//        entity.setId(1L);
//        entity.setBillContent("这是一个账单");
//        boolean save = orderService.save(entity);
//        System.out.println("保存成功：" + save);
//        List<OrderEntity> list = orderService.list(new QueryWrapper<OrderEntity>().eq("id", 1L));
//        list.forEach((item)->{
//            System.out.println(item);
//        });
//    }
//
//    @Autowired
//    AmqpAdmin amqpAdmin;
//
//    @Test
//    void createExchange() {
//        // 创建交换机
//        amqpAdmin.declareExchange(new DirectExchange("hello-java-exchange", true, false));
//        log.info("Exchange创建[{}]成功", "hello-java-exchange");
//    }
//
//    @Test
//    void createQueue() {
//        amqpAdmin.declareQueue(new Queue("hello-java-queue", true, false, false));
//        log.info("Queue创建[{}]成功", "hello-java-queue");
//    }
//
//    @Test
//    void createBinding() {
//        // 创建绑定
//        // String destination【目的地，队列name或 交换机name(如果作为路由的话)】
//        // Binding.DestinationType destinationType【目的地类型 queue还是exchange(路由)】
//        // String exchange【交换机】
//        // String routingKey【路由键】
//        // @Nullable Map<String, Object> arguments【自定义参数】
//        amqpAdmin.declareBinding(new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange",
//                "hello.java", null));
//        log.info("Binding创建[{}]成功", "hello-java-binding");
//    }
//
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//    @Test
//    void sendMsg() {
//        // 如果发送的消息是个对象，会使用序列化机制，将对象写出去。对象类必须实现 serializable
//        // 使用json的方式序列化
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", "hello world", new CorrelationData(UUID.randomUUID().toString()));
//    }
}
