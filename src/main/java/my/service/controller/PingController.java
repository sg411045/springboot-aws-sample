package my.service.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;


@RestController
@EnableWebMvc
public class PingController {

    private static String LAMBDA_ID = UUID.randomUUID().toString();
    //private static Integer TIMEOUT_SECONDS = Integer.parseInt(System.getenv("TIMEOUT_SECONDS"));
    private static Integer TIMEOUT_SECONDS = 30;

    //private static String PRODUCT_TABLE = System.getenv("PRODUCT_TABLE");
    private static String PRODUCT_TABLE = "PRODUCTS";
    private static String PRODUCT_ID = "what_is_serverless";

    private AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
    private Integer invocationCount = 0;


    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");
        return pong;
    }

    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public Map<String, String> products() {
        Map<String, String> products = new HashMap<>();
        products.put(":1", "Product 1");

        HashMap<String, AttributeValue> updateKey = new HashMap<>();
        updateKey.put("productId", new AttributeValue(PRODUCT_ID));

        // HashMap<String, AttributeValue> expressionValues = new HashMap<>();
        // expressionValues.put(":q", new AttributeValue().withN("1"));
        // expressionValues.put(":z", new AttributeValue().withN("0"));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(PRODUCT_TABLE)
                .withKey(updateKey);
                //.withUpdateExpression("SET quantity = quantity - :q")
                //.withExpressionAttributeValues(expressionValues)
                //.withConditionExpression("quantity > :z");

        boolean success = false;

        try {
            dynamoDBClient.updateItem(updateItemRequest);
            success = true;
        } catch (ConditionalCheckFailedException exception) {
            // NOP
        }

        products.put("success ", Boolean.toString(success));

        return products;
    }
}
