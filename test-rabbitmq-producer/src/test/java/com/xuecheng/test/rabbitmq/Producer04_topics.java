package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer04_topics {
    private static final String QUEUE_INFORM_EMAIL="queue_inform_email";
    private static final String QUEUE_INFORM_SMS="queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    private static final String INFORM_EMAIL="inform.#.email.#";
    private static final String INFORM_SMS="inform.#.sms.#";

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;

        try {  //创建连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.43.9");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");//rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务
            //创建与RabbitMQ服务的TCP连接
            connection = factory.newConnection(); //创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM,BuiltinExchangeType.TOPIC);
            //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            //绑定队列
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,INFORM_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_TOPICS_INFORM,INFORM_SMS);
            //发送消息
            for (int i = 0; i < 5; i++) {
                String msg = "sed email  message :   "+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email",null,msg.getBytes());
                System.out.println("sed message :   '"+msg+"'");
            }

            for (int i = 0; i < 5; i++) {
                String msg = "sed sms  message :   "+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms",null,msg.getBytes());
                System.out.println("sed message :   '"+msg+"'");
            }
            for (int i = 0; i < 5; i++) {
                String msg = "sed sms AND email  message :   "+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms.email",null,msg.getBytes());
                System.out.println("sed message :   '"+msg+"'");
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
