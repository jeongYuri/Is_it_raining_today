package com.example.crudw.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class WeatherController {
    @GetMapping("/weather")
    public String getWeather(@RequestParam("nx") String nx, @RequestParam("ny") String ny) throws Exception {

        String apiUrl = "http://apis.data.go.kr/1360" +
                "000/VilageFcstInfoService_2.0/getUltraSrtNcst";
        String serviceKey = "PXL4ObzaxgbrcVcEe5c8QTzAdF5bD9Y714d3X2Tus6DgtWsj10nEfjP6lUL6Z%2FAPd2pM1XQhuvJx%2BWrEo5%2BLfw%3D%3D";
        String dataType = "JSON";
        String numOfRows = "10";
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");
        String base_date = currentDateTime.format(dateTimeFormatter);
        int hour = currentDateTime.getHour();
        int min = currentDateTime.getMinute();
        if(min<=30){
            hour -=1;
        }
        String base_time = hour +"00";
        if (nx.contains(".")) {
            nx = nx.substring(0, nx.indexOf("."));
        }

        if (ny.contains(".")) {
            ny = ny.substring(0, ny.indexOf("."));
        }
        String url = apiUrl + "?serviceKey=" + serviceKey + "&nx=" + nx + "&ny=" + ny + "&base_date=" + base_date + "&base_time=" + base_time + "&dataType=" + dataType + "&numOfRows=" + numOfRows;

        HashMap<String, Object> resultMap = getDataFromJson(url, "UTF-8", "get", "");
        System.out.println("# RESULT :" + resultMap);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", resultMap);
        return jsonObject.toString();
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
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
            String line = null;
            StringBuffer result = new StringBuffer();
            while((line=br.readLine())!=null)result.append(line);

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