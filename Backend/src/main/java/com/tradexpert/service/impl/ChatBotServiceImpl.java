package com.tradexpert.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.tradexpert.model.CoinDTO;
import com.tradexpert.response.ApiResponse;
import com.tradexpert.response.FunctionResponse;
import com.tradexpert.service.ChatBotService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatBotService {

    @Value("${gemini.api.key}")
    private String API_KEY;

    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getName());
        }
    }

    public CoinDTO makeApiRequest(String currencyName) {
        System.out.println("coin name "+currencyName);
        String url = "https://api.coingecko.com/api/v3/coins/"+currencyName.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                Map<String, Object> image = (Map<String, Object>) responseBody.get("image");
                Map<String, Object> marketData = (Map<String, Object>) responseBody.get("market_data");

                CoinDTO coinInfo = new CoinDTO();
                coinInfo.setId((String) responseBody.get("id"));
                coinInfo.setSymbol((String) responseBody.get("symbol"));
                coinInfo.setName((String) responseBody.get("name"));
                coinInfo.setImage((String) image.get("large"));

                coinInfo.setCurrentPrice(convertToDouble(((Map<String, Object>) marketData.get("current_price")).get("usd")));
                coinInfo.setMarketCap(convertToDouble(((Map<String, Object>) marketData.get("market_cap")).get("usd")));
                coinInfo.setMarketCapRank((int) responseBody.get("market_cap_rank"));
                coinInfo.setTotalVolume(convertToDouble(((Map<String, Object>) marketData.get("total_volume")).get("usd")));
                coinInfo.setHigh24h(convertToDouble(((Map<String, Object>) marketData.get("high_24h")).get("usd")));
                coinInfo.setLow24h(convertToDouble(((Map<String, Object>) marketData.get("low_24h")).get("usd")));
                coinInfo.setPriceChange24h(convertToDouble(marketData.get("price_change_24h")));
                coinInfo.setPriceChangePercentage24h(convertToDouble(marketData.get("price_change_percentage_24h")));
                coinInfo.setMarketCapChange24h(convertToDouble(marketData.get("market_cap_change_24h")));
                coinInfo.setMarketCapChangePercentage24h(convertToDouble(marketData.get("market_cap_change_percentage_24h")));
                coinInfo.setCirculatingSupply(convertToDouble(marketData.get("circulating_supply")));
                coinInfo.setTotalSupply(convertToDouble(marketData.get("total_supply")));

                return coinInfo;
            }
        } catch (Exception e) {
            System.out.println("Error in makeApiRequest: " + e.getMessage());
        }
        return null;
    }

    public FunctionResponse getFunctionResponse(String prompt) {
        String apiUrl = GEMINI_BASE_URL + "?key=" + API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", API_KEY);

        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        partsArray.put(part);
        content.put("parts", partsArray);
        contentsArray.put(content);
        requestBody.put("contents", contentsArray);

        JSONArray toolsArray = new JSONArray();
        JSONObject tool = new JSONObject();
        JSONArray functionDeclarations = new JSONArray();
        JSONObject function = new JSONObject();

        function.put("name", "getCoinDetails");
        function.put("description", "Get the coin details from given currency object");

        JSONObject parameters = new JSONObject();
        parameters.put("type", "OBJECT");

        JSONObject properties = new JSONObject();
        JSONObject currencyNameProp = new JSONObject();
        currencyNameProp.put("type", "STRING");
        currencyNameProp.put("description", "The currency name, id, symbol.");
        properties.put("currencyName", currencyNameProp);

        JSONObject currencyDataProp = new JSONObject();
        currencyDataProp.put("type", "STRING");
        currencyDataProp.put("description", "Currency Data id, symbol, name, image, current_price, market_cap, market_cap_rank, fully_diluted_valuation, total_volume, high_24h, low_24h, price_change_24h, price_change_percentage_24h, market_cap_change_24h, market_cap_change_percentage_24h, circulating_supply, total_supply, max_supply, ath, ath_change_percentage, ath_date, atl, atl_change_percentage, atl_date, last_updated.");
        properties.put("currencyData", currencyDataProp);

        parameters.put("properties", properties);

        JSONArray required = new JSONArray();
        required.put("currencyName");
        required.put("currencyData");
        parameters.put("required", required);

        function.put("parameters", parameters);
        functionDeclarations.put(function);
        tool.put("functionDeclarations", functionDeclarations);
        toolsArray.put(tool);
        requestBody.put("tools", toolsArray);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String responseBody = response.getBody();
            System.out.println("Gemini Response: " + responseBody);

            ReadContext ctx = JsonPath.parse(responseBody);

            // First check if we have a valid response structure
            if (ctx.read("$.candidates") == null || ctx.read("$.candidates[0]") == null ||
                    ctx.read("$.candidates[0].content") == null || ctx.read("$.candidates[0].content.parts") == null) {
                System.out.println("Invalid response structure from Gemini");
                return null;
            }

            // Try to get function call first
            try {
                Object functionCall = ctx.read("$.candidates[0].content.parts[0].functionCall");
                if (functionCall != null) {
                    FunctionResponse res = new FunctionResponse();
                    String currencyName = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyName");
                    String currencyData = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyData");
                    String name = ctx.read("$.candidates[0].content.parts[0].functionCall.name");

                    res.setCurrencyName(currencyName);
                    res.setCurrencyData(currencyData);
                    res.setFunctionName(name);

                    System.out.println("Function call response: " + name + " - " + currencyName + " - " + currencyData);
                    return res;
                }
            } catch (Exception e) {
                System.out.println("No function call in response, trying text response");
            }

            // If no function call, try to get direct text response
            String textResponse = ctx.read("$.candidates[0].content.parts[0].text");
            if (textResponse != null) {
                System.out.println("Text response from Gemini: " + textResponse);

                // If Gemini is asking for clarification, return null to trigger a more specific prompt
                if (textResponse.toLowerCase().contains("specify") ||
                        textResponse.toLowerCase().contains("which details") ||
                        textResponse.toLowerCase().contains("please clarify")) {
                    return null;
                }

                String currencyName = extractCurrencyNameFromText(textResponse);
                if (currencyName != null) {
                    FunctionResponse res = new FunctionResponse();
                    res.setCurrencyName(currencyName);
                    res.setCurrencyData(textResponse);
                    res.setFunctionName("getCoinDetails");
                    return res;
                }
            }

            System.out.println("No valid function call or identifiable text response found");
            return null;

        } catch (Exception e) {
            System.out.println("Error in getFunctionResponse: " + e.getMessage());
            return null;
        }
    }

    private String extractCurrencyNameFromText(String text) {
        if (text == null) return null;

        text = text.toLowerCase();
        if (text.contains("bitcoin") || text.contains("btc")) return "bitcoin";
        if (text.contains("ethereum") || text.contains("eth")) return "ethereum";
        if (text.contains("ripple") || text.contains("xrp")) return "ripple";
        if (text.contains("tether") || text.contains("usdt")) return "tether";
        if (text.contains("binancecoin") || text.contains("bnb")) return "binancecoin";
        if (text.contains("solana") || text.contains("sol")) return "solana";
        if (text.contains("usd-coin") || text.contains("usdc")) return "usd-coin";
        if (text.contains("dogecoin") || text.contains("doge")) return "dogecoin";
        if (text.contains("tron") || text.contains("trx")) return "tron";

        return null;
    }

    @Override
    public ApiResponse getCoinDetails(String prompt) {
        try {
            FunctionResponse res = getFunctionResponse(prompt);
            if (res == null || res.getCurrencyName() == null) {
                // If Gemini asked for clarification or we couldn't identify a coin
                return new ApiResponse("Please specify which cryptocurrency you're interested in and what information you need (e.g., 'What is the current price of Bitcoin?' or 'Show me market cap for Ethereum')", false);
            }

            CoinDTO coinData = makeApiRequest(res.getCurrencyName());
            if (coinData == null) {
                return new ApiResponse("Could not fetch data for " + res.getCurrencyName() + ". Please check if the cryptocurrency name is correct.", false);
            }

            String apiUrl = GEMINI_BASE_URL + "?key=" + API_KEY;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-goog-api-key", API_KEY);

            JSONObject requestBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();

            // User message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            JSONArray userParts = new JSONArray();
            JSONObject userText = new JSONObject();
            userText.put("text", prompt);
            userParts.put(userText);
            userMessage.put("parts", userParts);
            contentsArray.put(userMessage);

            // Model's function call
            JSONObject modelMessage = new JSONObject();
            modelMessage.put("role", "model");
            JSONArray modelParts = new JSONArray();
            JSONObject functionCall = new JSONObject();
            JSONObject functionCallDetails = new JSONObject();
            functionCallDetails.put("name", "getCoinDetails");

            JSONObject args = new JSONObject();
            args.put("currencyName", res.getCurrencyName());
            args.put("currencyData", res.getCurrencyData());
            functionCallDetails.put("args", args);

            functionCall.put("functionCall", functionCallDetails);
            modelParts.put(functionCall);
            modelMessage.put("parts", modelParts);
            contentsArray.put(modelMessage);

            // Function response
            JSONObject functionMessage = new JSONObject();
            functionMessage.put("role", "function");
            JSONArray functionParts = new JSONArray();
            JSONObject functionResponse = new JSONObject();
            JSONObject functionResponseDetails = new JSONObject();
            functionResponseDetails.put("name", "getCoinDetails");

            JSONObject responseContent = new JSONObject();
            responseContent.put("name", "getCoinDetails");
            responseContent.put("content", new JSONObject(coinData.toString()));

            functionResponseDetails.put("response", responseContent);
            functionResponse.put("functionResponse", functionResponseDetails);
            functionParts.put(functionResponse);
            functionMessage.put("parts", functionParts);
            contentsArray.put(functionMessage);

            requestBody.put("contents", contentsArray);

            // Tools definition
            JSONArray toolsArray = new JSONArray();
            JSONObject tool = new JSONObject();
            JSONArray functionDeclarations = new JSONArray();
            JSONObject function = new JSONObject();

            function.put("name", "getCoinDetails");
            function.put("description", "Get crypto currency data from given currency object.");

            JSONObject parameters = new JSONObject();
            parameters.put("type", "OBJECT");

            JSONObject properties = new JSONObject();
            JSONObject currencyNameProp = new JSONObject();
            currencyNameProp.put("type", "STRING");
            currencyNameProp.put("description", "The currency Name, id, symbol.");
            properties.put("currencyName", currencyNameProp);

            JSONObject currencyDataProp = new JSONObject();
            currencyDataProp.put("type", "STRING");
            currencyDataProp.put("description", "The currency data id, symbol, current price, image, market cap etc.");
            properties.put("currencyData", currencyDataProp);

            parameters.put("properties", properties);

            JSONArray required = new JSONArray();
            required.put("currencyName");
            required.put("currencyData");
            parameters.put("required", required);

            function.put("parameters", parameters);
            functionDeclarations.put(function);
            tool.put("functionDeclarations", functionDeclarations);
            toolsArray.put(tool);
            requestBody.put("tools", toolsArray);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            String responseBody = response.getBody();
            System.out.println("Final Gemini Response: " + responseBody);

            ReadContext ctx = JsonPath.parse(responseBody);
            String text = ctx.read("$.candidates[0].content.parts[0].text");

            return new ApiResponse(text, true);

        } catch (Exception e) {
            System.out.println("Error in getCoinDetails: " + e.getMessage());
            return new ApiResponse("Sorry, I encountered an error while processing your request. Please try again later.", false);
        }
    }

    @Override
    public CoinDTO getCoinByName(String coinName) {
        return this.makeApiRequest(coinName);
    }

    @Override
    public String simpleChat(String prompt) {
        String apiUrl = GEMINI_BASE_URL + "?key=" + API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", API_KEY);

        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        partsArray.put(part);
        content.put("parts", partsArray);
        contentsArray.put(content);
        requestBody.put("contents", contentsArray);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String responseBody = response.getBody();

            ReadContext ctx = JsonPath.parse(responseBody);
            String text = ctx.read("$.candidates[0].content.parts[0].text");

            return text;
        } catch (Exception e) {
            System.out.println("Error in simpleChat: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}