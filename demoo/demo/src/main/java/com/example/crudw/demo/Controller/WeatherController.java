package com.example.crudw.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WeatherController {
    @GetMapping("/weather")
    public String getWeather(@RequestParam("nx") String nx, @RequestParam("ny") String ny) throws Exception {

        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "PXL4ObzaxgbrcVcEe5c8QTzAdF5bD9Y714d3X2Tus6DgtWsj10nEfjP6lUL6Z%2FAPd2pM1XQhuvJx%2BWrEo5%2BLfw%3D%3D";
        String dataType = "JSON";
        String numOfRows = "50";
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");
        String base_date = currentDateTime.format(dateTimeFormatter);
        int hour = currentDateTime.getHour();
        int min = currentDateTime.getMinute();
            if (hour >= 23 || (hour >= 2 && hour < 5)) {
                hour = 2;
            } else if (hour >= 5 && hour < 8) {
                hour = 5;
            } else if (hour >= 8 && hour < 11) {
                hour = 8;
            } else if (hour >= 11 && hour < 14) {
                hour = 11;
            } else if (hour >= 14 && hour < 17) {
                hour = 14;
            } else if (hour >= 17 && hour < 20) {
                hour = 17;
            } else if (hour >= 20 && hour < 23) {
                hour = 20;
            }
        String base_time = hour +"00";
        if (nx.contains(".")) {
            nx = nx.substring(0, nx.indexOf("."));
        }

        if (ny.contains(".")) {
            ny = ny.substring(0, ny.indexOf("."));
        }
        String url = apiUrl + "?serviceKey=" + serviceKey + "&nx=" + nx + "&ny=" + ny + "&base_date=" + base_date + "&base_time=" + base_time + "&dataType=" + dataType + "&numOfRows=" + numOfRows;
        JSONObject jsonObject = new JSONObject();
        HashMap<String, Object> resultMap = getDataFromJson(url, "UTF-8", "get", "");
        System.out.println("# RESULT :" + resultMap);
        jsonObject.put("result",resultMap);
        HashMap<String, Object> response = (HashMap<String, Object>) resultMap.get("response");
        HashMap<String, Object> body = (HashMap<String, Object>) response.get("body");
        HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
        ArrayList<HashMap<String, Object>> itemArray = (ArrayList<HashMap<String, Object>>) items.get("item");
        HashMap<String, Object> dataToSend = new HashMap<>();
        for (HashMap<String, Object> item : itemArray) {
            String category = (String) item.get("category");
            if (category.equals("TMP")){
                String fcstValue = (String) item.get("fcstValue");
                dataToSend.put("TMP", fcstValue);
            }if(category.equals("POP")){
                String fcstValue = (String)item.get("fcstValue");
                dataToSend.put("POP", fcstValue);
            }if(category.equals("REH")){
                String fcstValue = (String)item.get("fcstValue");
                dataToSend.put("REH", fcstValue);
            }if(category.equals("WSD")){
                String fcstValue = (String)item.get("fcstValue");
                dataToSend.put("WSD", fcstValue);
            }if(category.equals("SKY")){
                String fcstValue = (String)item.get("fcstValue");
                if(fcstValue.equals("1")){
                    dataToSend.put("SKY", "맑음");
                } else if (fcstValue.equals("3")) {
                    dataToSend.put("SKY", "구름많음");
                }else if(fcstValue.equals("4")){
                    dataToSend.put("SKY", "흐림");
                }
            }if(category.equals("PTY")){
                String fcstValue = (String)item.get("fcstValue");
                if(fcstValue.equals("0")){
                    dataToSend.put("PTY", "맑음");
                }else if(fcstValue.equals("1")) {
                    dataToSend.put("PTY", "비");
                }else if(fcstValue.equals("2")){
                    dataToSend.put("PTY", "진눈깨비");
                }else if(fcstValue.equals("3")){
                    dataToSend.put("PTY", "눈");
                }else if(fcstValue.equals("4")){
                    dataToSend.put("PTY", "소나기");
                }
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(dataToSend);
        System.out.println(jsonData);
        //return jsonObject.toString();
        return jsonData;
    }
    public HashMap<String,Object> getDataFromJson(String url,String encoding,String type,String jsonStr)throws  Exception{
        boolean isPost = false;
        if("post".equals(type)){
            isPost=true;
        }else{
            url="".equals(jsonStr)?url:url+"?request=" +jsonStr;
        }
        return getStringFromURL(url,encoding,isPost,jsonStr,"application/json");
    }
    public HashMap<String,Object> getStringFromURL(String url,String encoding,boolean isPost,String parameter,String contentType)throws Exception{
        URL apiURL = new URL(url);
        HttpURLConnection conn= null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        HashMap<String ,Object> resultMap = new HashMap<String,Object>();
        try{
            conn=(HttpURLConnection) apiURL.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);

            if(isPost){
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",contentType);
                conn.setRequestProperty("Accept","*/*");
            }else{
                conn.setRequestMethod("GET");
            }
            conn.connect();
            if(isPost){
                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
                bw.write(parameter);
                bw.flush();
                bw=null;
            }
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding)); //버퍼에 있는 정보 날씨 정보 받아온다
            String line = null;
            StringBuffer result = new StringBuffer();
            while((line=br.readLine())!=null)result.append(line);//버퍼에 있는 정보 문자열로 전환중
            ObjectMapper mapper = new ObjectMapper();
            resultMap = mapper.readValue(result.toString(),HashMap.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(url + "interfacefailed" + e.toString());
        }finally{
            if (conn!=null)conn.disconnect();
            if(br!=null)br.close();
            if(bw!=null)bw.close();
        }
        return resultMap;
    }

}