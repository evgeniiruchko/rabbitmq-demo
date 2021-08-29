package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Sender {
    private final static String EXCHANGE_NAME = "blog";
    public static void main(String[] args) {
        Random themeNumber = new Random();
        List<String> themes = new ArrayList(Arrays.asList("php", "java", "python", "c++", "pascal"));
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        try (Connection connection = connectionFactory.newConnection()){
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            for (int i = 0; i < 10; i++) {
                String theme = themes.get(themeNumber.nextInt(themes.size()));
                String message = "Сообщение по теме " + theme;
                channel.basicPublish(EXCHANGE_NAME, theme, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Отправили сообщение: " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
