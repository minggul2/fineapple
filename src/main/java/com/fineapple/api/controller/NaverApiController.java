package com.fineapple.api.controller;

import org.json.simple.parser.JSONParser;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NaverApiController {
    @GetMapping("/api/v1/search")
    public @ResponseBody
    Map<String, Object> search(ModelMap model
            , HttpServletResponse response
            , @RequestParam(value = "query", required = false) String query) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String id = "8zcCrXfCacF47jmSOLA9";
        String secret = "nKU9InZEfJ";
        Map<String, Object> map = new HashMap<>();
        try {
            String url = URLEncoder.encode(query, "UTF-8");
            String result = search(id, secret, url);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(result);
            map.put("result", obj);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    // 베이스 URL
    final String baseUrl = "https://openapi.naver.com/v1/search/shop.json?query=";

    public String search(String clientId, String secret, String _url) {
        HttpURLConnection con = null;
        String result = "";
        try {
            URL url = new URL(baseUrl + _url);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", secret);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) result = readBody(con.getInputStream());
            else result = readBody(con.getErrorStream());
        } catch (Exception e) {
            System.out.println("연결 오류 : " + e);
        } finally {
            con.disconnect();
        }
        return result;
    }

    /**
     * 결과를 읽는다 * * @param body * @return
     */
    public String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
