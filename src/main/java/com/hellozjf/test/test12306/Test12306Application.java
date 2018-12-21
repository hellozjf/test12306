package com.hellozjf.test.test12306;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.test.test12306.dataobject.Station;
import com.hellozjf.test.test12306.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.Map;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class Test12306Application {

    public static void main(String[] args) {
        SpringApplication.run(Test12306Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationRepository stationRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            if (stationRepository.count() == 0) {
                initStations();
            }
            getTrainList();
        };
    }

    private void initStations() throws Exception {
        String result = restTemplate.getForObject("https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9077", String.class);
        log.debug("result = {}", result);
        result = result.substring(result.indexOf("@") + 1, result.length() - 2);
        log.debug("result = {}", result);
        String[] results = result.split("@");
        for (String r : results) {
            if (! StringUtils.isEmpty(r)) {
                log.debug("r = {}", r);
                Station station = Station.getFromText(r);
                stationRepository.save(station);
            }
        }
    }

    private void getTrainList() throws Exception {
        // 尝试获取火车列表
        // 参考https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2018-12-29&leftTicketDTO.from_station=HGH&leftTicketDTO.to_station=NGH&purpose_codes=ADULT
        String leftTicketUrl = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date={trainDate}&leftTicketDTO.from_station={fromStation}&leftTicketDTO.to_station={toStation}&purpose_codes={purposeCodes}";
        String result = restTemplate.getForObject(leftTicketUrl, String.class, "2018-12-29", "HGH", "NGH", "ADULT");

        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        Iterator iter = jsonNode.get("data").get("result").iterator();
        while (iter.hasNext()) {
            JsonNode node = (JsonNode) iter.next();
            log.debug("{}", node.textValue());
        }
    }

}

