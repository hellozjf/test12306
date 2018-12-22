package com.hellozjf.test.test12306;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellozjf.test.test12306.constant.TrainTypeEnum;
import com.hellozjf.test.test12306.dataobject.Station;
import com.hellozjf.test.test12306.dataobject.StationVersion;
import com.hellozjf.test.test12306.repository.StationRepository;
import com.hellozjf.test.test12306.repository.StationVersionRepository;
import com.hellozjf.test.test12306.util.SeatUtils;
import com.hellozjf.test.test12306.util.TrainTypeUtils;
import com.hellozjf.test.test12306.vo.InitParamsVO;
import com.hellozjf.test.test12306.vo.TrainInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private StationVersionRepository stationVersionRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            InitParamsVO initParamsVO = getInitParams();
            initStations(initParamsVO.getStationNameUri());
            List<TrainInfoVO> trainInfoVOList = getTrainList(
                    initParamsVO.getCLeftTicketUri(),
                    "2019-01-01",
                    "宁波",
                    "杭州东"
            );
            log.debug("trainInfoVOList.size() = {}", trainInfoVOList.size());
            List<TrainInfoVO> wantedTrainInfoVOList = getWantedTrainInfoVOList(
                    trainInfoVOList,
                    "12:00",
                    "21:00",
                    null,
                    false,
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false
            );
            log.debug("wantedTrainInfoVOList.size() = {}", wantedTrainInfoVOList.size());
            for (TrainInfoVO trainInfoVO : wantedTrainInfoVOList) {
                log.debug("trainCode={} deptTime={} arrTime={} secondClassSeat={}",
                        trainInfoVO.getTrainCode(),
                        trainInfoVO.getDepTime(),
                        trainInfoVO.getArrTime(),
                        trainInfoVO.getSecondClassSeat(),
                        trainInfoVO.getNoSeat());
            }
        };
    }

    /**
     * 获取想要的火车列表
     * @param trainInfoVOList   根据日期和出发结束站点查出来的火车列表
     * @param deptStartTime     需要的出发开始时间，例如19:00
     * @param deptEndTime       需要的出发结束时间，例如20:00
     * @param trainCodeList     需要的列车code，例如["G7661", "D655"]
     * @param bBusinessSeat     是否可以是商务座
     * @param bFirstClassSeat   是否可以是一等座
     * @param bSecondClassSeat  是否可以是二等座
     * @param bHardSeat         是否可以是硬座
     * @param bSoftBerth        是否可以是软卧
     * @param bHardBerth        是否可以是硬卧
     * @param bNoSeat           是否可以是无座
     * @param bG                是否可以是高铁
     * @param bD                是否可以是动车
     * @param bK                是否可以是特快
     * @return
     */
    private List<TrainInfoVO> getWantedTrainInfoVOList(List<TrainInfoVO> trainInfoVOList,
                                                       String deptStartTime,
                                                       String deptEndTime,
                                                       List<String> trainCodeList,
                                                       boolean bBusinessSeat,
                                                       boolean bFirstClassSeat,
                                                       boolean bSecondClassSeat,
                                                       boolean bHardSeat,
                                                       boolean bSoftBerth,
                                                       boolean bHardBerth,
                                                       boolean bNoSeat,
                                                       boolean bG,
                                                       boolean bD,
                                                       boolean bK
                                                       ) {
        List<TrainInfoVO> wantedTrainInfoVOList = new ArrayList<>();
        for (TrainInfoVO trainInfoVO : trainInfoVOList) {
            if (! StringUtils.isEmpty(deptStartTime)) {
                if (trainInfoVO.getDepTime().compareTo(deptStartTime) < 0) {
                    // 我们需要的是>=deptStartTime的火车，所以过滤掉它
                    continue;
                }
            }
            if (! StringUtils.isEmpty(deptEndTime)) {
                if (trainInfoVO.getDepTime().compareTo(deptEndTime) > 0) {
                    // 我们需要的是<=deptEndTime的火车，所以过滤掉它
                    continue;
                }
            }
            if (trainCodeList != null && trainCodeList.size() != 0) {
                if (! trainCodeList.contains(trainInfoVO.getTrainCode())) {
                    // 在剩下的火车中，只选择我们需要的火车，否则过滤掉它
                    continue;
                }
            }

            boolean bAddSeat = false;
            if (bBusinessSeat && SeatUtils.haveSeat(trainInfoVO.getBusinessSeat()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bFirstClassSeat && SeatUtils.haveSeat(trainInfoVO.getFirstClassSeat()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bSecondClassSeat && SeatUtils.haveSeat(trainInfoVO.getSecondClassSeat()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bHardSeat && SeatUtils.haveSeat(trainInfoVO.getHardSeat()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bSoftBerth && SeatUtils.haveSeat(trainInfoVO.getSoftBerth()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bHardBerth && SeatUtils.haveSeat(trainInfoVO.getHardBerth()) && !bAddSeat) {
                bAddSeat = true;
            }
            if (bNoSeat && SeatUtils.haveSeat(trainInfoVO.getNoSeat()) && !bAddSeat) {
                bAddSeat = true;
            }

            boolean bAddTrainType = false;
            if (bG && TrainTypeUtils.isWantedTrainType(trainInfoVO.getTrainCode(), TrainTypeEnum.G) && !bAddTrainType) {
                bAddTrainType = true;
            }
            if (bD && TrainTypeUtils.isWantedTrainType(trainInfoVO.getTrainCode(), TrainTypeEnum.D) && !bAddTrainType) {
                bAddTrainType = true;
            }
            if (bK && TrainTypeUtils.isWantedTrainType(trainInfoVO.getTrainCode(), TrainTypeEnum.K) && !bAddTrainType) {
                bAddTrainType = true;
            }

            // 这是我们需要的火车，加入到返回列表中
            if (bAddSeat && bAddTrainType) {
                wantedTrainInfoVOList.add(trainInfoVO);
            }
        }

        return wantedTrainInfoVOList;
    }

    private InitParamsVO getInitParams() {
        String html = restTemplate.getForObject("https://kyfw.12306.cn/otn/leftTicket/init", String.class);
        log.debug("{}", html);
        // 通过查看日志，我们能够知道我们需要取出CLeftTicketUrl这个变量的值，我们使用正则表达式去获取这个变量的值
        // 正则表达式参考http://www.runoob.com/java/java-regular-expressions.html
        String cLeftTicketUrlPatternString = "^\\s*var\\s*CLeftTicketUrl\\s*=\\s*'(.*)'\\s*;\\s*$";
        Pattern cLeftTicketUrlPattern = Pattern.compile(cLeftTicketUrlPatternString);
        String stationNamePatternString = "^.*script.*src=\"(.*station_name[^\"]*)\".*$";
        Pattern stationNamePattern = Pattern.compile(stationNamePatternString);
        InitParamsVO initParamsVO = new InitParamsVO();
        try (
                StringReader stringReader = new StringReader(html);
                BufferedReader bufferedReader = new BufferedReader(stringReader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher m = cLeftTicketUrlPattern.matcher(line);
                if (m.matches()) {
                    log.debug(m.group(1));
                    initParamsVO.setCLeftTicketUri(m.group(1));
                }
                m = stationNamePattern.matcher(line);
                if (m.matches()) {
                    log.debug(m.group(1));
                    initParamsVO.setStationNameUri(m.group(1));
                }
            }
        } catch (Exception e) {
            log.error("e = {}", e);
        }

        return initParamsVO;
    }

    private void initStations(String stationNameUri) throws Exception {

        // 首先要去数据库里面查看版本信息是否一致，一致的话就跳过初始化，否则删除Station所有数据重新初始化
        List<StationVersion> stationVersionList = stationVersionRepository.findAll();
        if (stationVersionList.size() != 1) {
            // 说明要么数据库出错了插入了2条及以上，要么压根就没有初始化过，我不管，直接把这个StationVersion表初始化一遍
            stationVersionRepository.deleteAll();
            StationVersion stationVersion = new StationVersion();
            stationVersion.setStationNameUri(stationNameUri);
            stationVersionRepository.save(stationVersion);
        } else {
            // 说明数据库的数据是正常的，那好我判断下版本号有没有发生改变
            StationVersion stationVersion = stationVersionList.get(0);
            if (! stationVersion.getStationNameUri().equals(stationNameUri)) {
                // 说明版本号发生改变了
                stationVersion.setStationNameUri(stationNameUri);
                stationVersionRepository.save(stationVersion);
            } else {
                // 说明啥都没变，不用重新初始化了
                return;
            }
        }

        // 初始化Station表
        // 初始化前先把Station表的旧数据全删了
        stationRepository.deleteAll();
        // 然后重新插入新的Station信息
        String result = restTemplate.getForObject("https://kyfw.12306.cn" + stationNameUri, String.class);
        log.debug("result = {}", result);
        result = result.substring(result.indexOf("@") + 1, result.length() - 2);
        log.debug("result = {}", result);
        String[] results = result.split("@");
        for (String r : results) {
            if (!StringUtils.isEmpty(r)) {
                log.debug("r = {}", r);
                Station station = Station.getFromText(r);
                stationRepository.save(station);
            }
        }
    }

    /**
     * 获取火车列表
     * @param cLeftTicketUri    这个URI会一直发生变化，所以需要动态传入
     * @param deptDate          出发日期，例如2018-12-29
     * @param deptStationName   出发站点名称，例如杭州东，注意必须是无歧义的站点名称
     * @param arrStationName    到达站点名称，例如宁波，注意必须是无歧义的站点名称
     * @throws Exception
     */
    private List<TrainInfoVO> getTrainList(String cLeftTicketUri, String deptDate, String deptStationName, String arrStationName) throws Exception {
        // 尝试获取火车列表
        // 参考https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2018-12-29&leftTicketDTO.from_station=HGH&leftTicketDTO.to_station=NGH&purpose_codes=ADULT
        String leftTicketUrl = "https://kyfw.12306.cn/otn/" + cLeftTicketUri + "?leftTicketDTO.train_date={trainDate}&leftTicketDTO.from_station={fromStation}&leftTicketDTO.to_station={toStation}&purpose_codes={purposeCodes}";

        // 根据站点名称去数据中找到它们的站点代码
        Station deptStation = stationRepository.findByName(deptStationName);
        Station arrStation = stationRepository.findByName(arrStationName);
        log.debug("depteDate={} deptStationCode={} arrStationCode={}", deptDate, deptStation.getCode2(), arrStation.getCode2());
        String result = restTemplate.getForObject(leftTicketUrl, String.class, deptDate, deptStation.getCode2(), arrStation.getCode2(), "ADULT");

        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        Iterator iter = jsonNode.get("data").get("result").iterator();
        List<TrainInfoVO> trainInfoVOList = new ArrayList<>();
        while (iter.hasNext()) {
            JsonNode node = (JsonNode) iter.next();
            log.debug("{}", node.textValue());
            TrainInfoVO ticketInfoVO = TrainInfoVO.getFromText(node.textValue());
            trainInfoVOList.add(ticketInfoVO);
        }
        return trainInfoVOList;
    }

}

