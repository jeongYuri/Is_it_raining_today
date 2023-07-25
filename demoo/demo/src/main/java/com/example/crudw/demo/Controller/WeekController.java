package com.example.crudw.demo.Controller;

import com.example.crudw.demo.Weather.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.json.simple.JSONObject;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.*;
;


@RestController
@RequestMapping("/api")
public class WeekController {
    private final EntityManager em;
    private static final Logger logger = LogManager.getLogger(WeekController.class);
    public WeekController(EntityManager em) {
        this.em = em;
    }

    @PostMapping("/region")
    @Transactional
    public ResponseEntity<String> resetRegionList() {
        String fileLocation = "C:\\Users\\storage\\init\\중기예보코드.csv";
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                String id = splits[0];
                String code = splits[1];
                em.persist(new Region(id, code));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("초기화에 성공했습니다");
    }
    @GetMapping("/week")
    public String getWeekWeather(@RequestParam("city")String city) throws Exception {
        String apiUrl = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
        String serviceKey = "PXL4ObzaxgbrcVcEe5c8QTzAdF5bD9Y714d3X2Tus6DgtWsj10nEfjP6lUL6Z%2FAPd2pM1XQhuvJx%2BWrEo5%2BLfw%3D%3D";
        String dataType = "JSON";
        String pageNo = "1";
        String numOfRows = "50";
        TypedQuery<Region> query = em.createQuery("SELECT r FROM Region r WHERE r.id = :city", Region.class);
        query.setParameter("city", city);
        Region region = query.getSingleResult();
        String regId = region.getCode();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");
        String base_date = currentDateTime.format(dateTimeFormatter);
        int hour = currentDateTime.getHour();
        String name = region.getId();
        String tmFc = null;
        logger.info("getWeekWeather called with city: {}",city);
        if (name != null) {
            if(name.equals(city)){
                System.out.println(city);
                regId = region.getCode();
                System.out.println(regId);
            }
        } else {
            System.out.println("bye");
        }
        if(18<=hour && hour>6) {
            hour = 18;
        }else if(6<=hour && hour<18){
            hour =6;
        }
        if(hour==6){
            tmFc = base_date+"0"+hour+"00";
        }else{
            tmFc = base_date+hour+"00";
        }
        //String tmFc = base_date+hour+"00";
        String url = apiUrl+"?serviceKey="+serviceKey+"&pageNo="+pageNo+"&numOfRows="+numOfRows+"&dataType="+dataType+"&regId="+regId+"&tmFc="+tmFc;
        System.out.println(tmFc);
        JSONObject jsonObject = new JSONObject();
        HashMap<String, Object> resultMap = getDataFromJson(url, "UTF-8", "get", "");
        System.out.println("# RESULT :" + resultMap);
        jsonObject.put("result",resultMap);
        return url;//이거 임시임 돌리지마..
    }


    public HashMap<String,Object> getDataFromJson(String url, String encoding, String type, String jsonStr)throws  Exception{
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
