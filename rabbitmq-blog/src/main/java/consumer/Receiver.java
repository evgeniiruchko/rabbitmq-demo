package consumer;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Receiver {
    private final static String EXCHANGE_NAME = "blog";

    public static void main(String[] args) throws IOException {
        Random themeNumber = new Random();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Что бы подписаться введите [subscribe] [название темы]");
        System.out.println("Что бы отписаться введите [unsubscribe] [название темы]");
        System.out.println("Что бы получить сообщения введите [end]");

        List<String> subscribeThemes = new ArrayList<>();
        while (true) {
            String theme = reader.readLine();
            if (theme.startsWith("subscribe ")) {
                theme = theme.substring(10);
                subscribeThemes.add(theme);
            }
            if (theme.startsWith("unsubscribe ")) {
                theme = theme.substring(12);
                subscribeThemes.remove(theme);
            }
            if (theme.equals("end")) {
                System.out.println("Вы подписани на " + subscribeThemes);
                break;
            }
        }

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String queueName = channel.queueDeclare().getQueue();
            System.out.println("Имя очереди: " + queueName);
            for (String theme: subscribeThemes) {
                channel.queueBind(queueName, EXCHANGE_NAME, theme);
            }
            System.out.println("Ждём сообщение");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Получили сообщение: " + message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
