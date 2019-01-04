package com.hellozjf.test.test12306;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.hellozjf.test.test12306.config.CustomConfig;
import com.hellozjf.test.test12306.constant.PictureNames;
import com.hellozjf.test.test12306.constant.TrainTypeEnum;
import com.hellozjf.test.test12306.dataobject.Station;
import com.hellozjf.test.test12306.dataobject.StationVersion;
import com.hellozjf.test.test12306.dataobject.VerificationCode;
import com.hellozjf.test.test12306.repository.StationRepository;
import com.hellozjf.test.test12306.repository.StationVersionRepository;
import com.hellozjf.test.test12306.repository.VerificationCodeRepository;
import com.hellozjf.test.test12306.service.IQuestionAnswer;
import com.hellozjf.test.test12306.util.*;
import com.hellozjf.test.test12306.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
public class Test12306Application {

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {

        };
    }

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

    @Bean
    public BaiduTokenVO baiduTokenVO() {
        BaiduTokenVO baiduTokenVO = BaiduAIUtils.getBaiduTokenVO(customConfig, restTemplate);
        log.debug("baiduTokenVO = {}", baiduTokenVO);
        log.debug("token = {}", baiduTokenVO.getAccessToken());
        return baiduTokenVO;
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationVersionRepository stationVersionRepository;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private BaiduTokenVO baiduTokenVO;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private IQuestionAnswer questionAnswer;

    /**
     * 获取图片并存放在文件夹中
     */
    private void getPictures() {
        for (int i = 0; i < 1; i++) {

            log.info("start loop {}", i);

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date date = new Date();
            String folderName = dateFormat.format(date);

            try {

                VerificationCode verificationCode = new VerificationCode();
                verificationCode.setFolderName(folderName);

                File jpegFile = captchaImage(folderName);
                JpgUtils.writeQuestionImage(jpegFile);
//                    String question = getJpegWords(jpegFile);
//                    log.debug("question = {}", question);
//                    if (StringUtils.isEmpty(question)) {
//                        jpegFile.getParentFile().delete();
//                        Thread.sleep(60 * 1000);
//                        continue;
//                    }
//                    verificationCode.setQuestion(question);

                JpgUtils.writeSubImage(jpegFile, 0, 0);
                JpgUtils.writeSubImage(jpegFile, 1, 0);
                JpgUtils.writeSubImage(jpegFile, 2, 0);
                JpgUtils.writeSubImage(jpegFile, 3, 0);
                JpgUtils.writeSubImage(jpegFile, 0, 1);
                JpgUtils.writeSubImage(jpegFile, 1, 1);
                JpgUtils.writeSubImage(jpegFile, 2, 1);
                JpgUtils.writeSubImage(jpegFile, 3, 1);
//                    List<String> keywordList = getSubImageKeywordList(jpegFile, 0, 0);
//                    verificationCode.setPic00Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 1, 0);
//                    verificationCode.setPic01Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 2, 0);
//                    verificationCode.setPic02Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 3, 0);
//                    verificationCode.setPic03Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 0, 1);
//                    verificationCode.setPic10Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 1, 1);
//                    verificationCode.setPic11Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 2, 1);
//                    verificationCode.setPic12Desc(keywordList.toString());
//                    keywordList = getSubImageKeywordList(jpegFile, 3, 1);
//                    verificationCode.setPic13Desc(keywordList.toString());

                // 未处理
                verificationCode.setDisposeResult("0");

                // 将结果存入数据库中
                verificationCodeRepository.save(verificationCode);

                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("e = {}", e);
                File folder = new File(customConfig.getForder12306() + "/" + folderName);
                File[] files = folder.listFiles();
                for (File file : files) {
                    file.delete();
                }
                folder.delete();

                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e1) {
                    log.error("e1 = {}", e1);
                }
            }
        }
    }

    /**
     * 从数据库中读取所需的图片所在文件夹，然后从网络上获取图片，再交给http://littlebigluo.qicp.net:47720/进行解析
     */
    private void getChoose() {

        for (int i = 0; i < 10000; i++) {

            VerificationCode verificationCode = verificationCodeRepository.findTopByChooseNullOrderByFolderNameAsc();

            try {

                // 下载图片
                String url = "https://aliyun.hellozjf.com:7004/Pictures/12306/" + verificationCode.getFolderName() + "/full.jpg";
                File file = FileUtils.downloadFile(url, "full.jpg");

                HttpHeaders headers = new HttpHeaders();
                MediaType contentType = MediaType.parseMediaType("multipart/form-data");
                headers.setContentType(contentType);
                MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
                FileSystemResource fileSystemResource = new FileSystemResource(file);
                form.add("file", fileSystemResource);
                HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
                String result = restTemplate.postForObject("http://littlebigluo.qicp.net:47720/", files, String.class);
                log.debug("result = {}", result);

                // 从文档中获取B标签的内容
                Document document = Jsoup.parse(result);
                Elements elements = document.body().getElementsByTag("B");
                String data = elements.text();
                log.debug("elements = {}, data = {}", elements, data);

                String choose = data.replace(" ", ",");
                verificationCode.setChoose(choose);
                verificationCodeRepository.save(verificationCode);

                Thread.sleep(1 * 1000);

            } catch (Exception e) {
                log.error("e = {}", e);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e1) {
                    log.error("e1 = {}", e1);
                }
            }

        }
    }

    /**
     * 获取两个文本的相似度
     *
     * @param text1
     * @param text2
     * @return
     */
    public Double getShortTextSimilarResult(String text1, String text2) {
        ShortTextSimilarReqVO shortTextSimilarReqVO = new ShortTextSimilarReqVO();
        shortTextSimilarReqVO.setText1(text1);
        shortTextSimilarReqVO.setText2(text2);

        String url = String.format("https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet?charset=%s&access_token=%s",
                "UTF-8",
                baiduTokenVO.getAccessToken());
        ShortTextSimilarRespVO shortTextSimilarRespVO = restTemplate.postForObject(url, shortTextSimilarReqVO, ShortTextSimilarRespVO.class);
        return shortTextSimilarRespVO.getScore();
    }

    /**
     * 从JPEG文件中，获取8张子图
     *
     * @param jpegFile
     * @param x        0-3
     * @param y        0-1
     * @return
     */
    public List<String> getSubImageKeywordList(File jpegFile, int x, int y) throws Exception {

        // 将子图片下载到文件夹中
        BufferedImage subImage = JpgUtils.writeSubImage(jpegFile, x, y);

        // 识别图片
        String url = String.format("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=%s",
                baiduTokenVO.getAccessToken());
        HttpEntity httpEntity = HttpEntityUtils.getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, ImmutableMap.of(
                "image", JpgUtils.changeJpegToBase64(subImage)
        ));
        ImageClassifyVO imageClassifyVO = restTemplate.postForObject(url, httpEntity, ImageClassifyVO.class);
        List<String> keywordList = ImageClassifyVOUtils.getKeywordList(imageClassifyVO);
        log.debug("keywordList = {}", keywordList);
        return keywordList;
    }

    /**
     * 获取验证码图片中的问题
     *
     * @param jpegFile
     * @return
     * @throws Exception
     */
    public List<String> getJpegQuestions(File jpegFile) throws Exception {

        List<String> questions = new ArrayList<>();
        BufferedImage bufImage = ImageIO.read(jpegFile);

        // 获取右上角的文字信息
        BufferedImage subImage1 = bufImage.getSubimage(119, 0, 47, 30);
        ImageIO.write(subImage1, "JPEG", new File("images/tmp2.jpg"));
        BufferedImage subImage2 = bufImage.getSubimage(166, 0, 47, 30);
        ImageIO.write(subImage2, "JPEG", new File("images/tmp3.jpg"));

        // 识别文字
        String url = String.format("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=%s",
                baiduTokenVO.getAccessToken());
        HttpEntity httpEntity = HttpEntityUtils.getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, ImmutableMap.of(
                "image", JpgUtils.changeJpegToBase64(subImage1)
        ));
        OrcResultVO orcResultVO = restTemplate.postForObject(url, httpEntity, OrcResultVO.class);
        if (orcResultVO.getWordsResultNum() >= 1) {
            log.debug("orcResultVO = {}", orcResultVO);
            questions.add(orcResultVO.getWordsResult().get(0).getWords());
        }

        httpEntity = HttpEntityUtils.getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, ImmutableMap.of(
                "image", JpgUtils.changeJpegToBase64(subImage2)
        ));
        orcResultVO = restTemplate.postForObject(url, httpEntity, OrcResultVO.class);
        if (orcResultVO.getWordsResultNum() >= 1) {
            log.debug("orcResultVO = {}", orcResultVO);
            questions.add(orcResultVO.getWordsResult().get(0).getWords());
        }

        return questions;
    }

    /**
     * 查询想要的火车票
     *
     * @throws Exception
     */
    private void queryWantedTickets() throws Exception {
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
    }

    /**
     * 从这个函数可以从12306获取验证码图片，并保存到本地images文件夹下面
     *
     * @return 下载好的File对象
     */
    private File captchaImage() {
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/passport/captcha/captcha-image", byte[].class);
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        log.debug("httpHeaders = {}", httpHeaders);
        byte[] bytes = responseEntity.getBody();
        log.debug(Arrays.toString(bytes));
        String fileName = UUIDUtils.genId() + ".jpg";
        // 如果没有images文件夹，那就创建一个
        File folder = new File("images");
        if (!folder.exists()) {
            folder.mkdir();
        } else if (!folder.isDirectory()) {
            folder.delete();
            folder.mkdir();
        }
        File file = new File("images/" + fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
        return file;
    }

    /**
     * 从这个函数可以从12306获取验证码图片，并保存到本地images文件夹下面
     *
     * @return 下载好的File对象
     */
    private File captchaImage(String folderName) {

        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand", byte[].class);
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        log.debug("httpHeaders = {}", httpHeaders);
        byte[] bytes = responseEntity.getBody();
        log.debug(Arrays.toString(bytes));
        String fileName = PictureNames.FULL;
        // 如果没有images文件夹，那就创建一个
        File folder = new File(customConfig.getForder12306() + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        } else if (!folder.isDirectory()) {
            folder.delete();
            folder.mkdir();
        }
        File file = new File(folder, fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
        return file;
    }

    /**
     * 从这个函数可以获取到登录所需要的jsessionid
     */
    private String getJSessionIdBylogin() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/otn/login/init", String.class);
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        log.debug("httpHeaders = {}", httpHeaders);
        String jsessionId = HttpHeaderUtils.getJsessionId(httpHeaders);
        log.debug("jsessionId = {}", jsessionId);
        String body = responseEntity.getBody();
        log.debug("body = {}", body);
        return jsessionId;
    }

    /**
     * 获取想要的火车列表
     *
     * @param trainInfoVOList  根据日期和出发结束站点查出来的火车列表
     * @param deptStartTime    需要的出发开始时间，例如19:00
     * @param deptEndTime      需要的出发结束时间，例如20:00
     * @param trainCodeList    需要的列车code，例如["G7661", "D655"]
     * @param bBusinessSeat    是否可以是商务座
     * @param bFirstClassSeat  是否可以是一等座
     * @param bSecondClassSeat 是否可以是二等座
     * @param bHardSeat        是否可以是硬座
     * @param bSoftBerth       是否可以是软卧
     * @param bHardBerth       是否可以是硬卧
     * @param bNoSeat          是否可以是无座
     * @param bG               是否可以是高铁
     * @param bD               是否可以是动车
     * @param bK               是否可以是特快
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
            if (!StringUtils.isEmpty(deptStartTime)) {
                if (trainInfoVO.getDepTime().compareTo(deptStartTime) < 0) {
                    // 我们需要的是>=deptStartTime的火车，所以过滤掉它
                    continue;
                }
            }
            if (!StringUtils.isEmpty(deptEndTime)) {
                if (trainInfoVO.getDepTime().compareTo(deptEndTime) > 0) {
                    // 我们需要的是<=deptEndTime的火车，所以过滤掉它
                    continue;
                }
            }
            if (trainCodeList != null && trainCodeList.size() != 0) {
                if (!trainCodeList.contains(trainInfoVO.getTrainCode())) {
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

        InitParamsVO initParamsVO = new InitParamsVO();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://kyfw.12306.cn/otn/leftTicket/init", String.class);
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        log.debug("httpHeaders = {}", httpHeaders);
        initParamsVO.setJsessionId(HttpHeaderUtils.getJsessionId(httpHeaders));
        String body = responseEntity.getBody();
        log.debug("body = {}", body);
        // 通过查看日志，我们能够知道我们需要取出CLeftTicketUrl这个变量的值，我们使用正则表达式去获取这个变量的值
        // 正则表达式参考http://www.runoob.com/java/java-regular-expressions.html
        String cLeftTicketUrlPatternString = "^\\s*var\\s*CLeftTicketUrl\\s*=\\s*'(.*)'\\s*;\\s*$";
        Pattern cLeftTicketUrlPattern = Pattern.compile(cLeftTicketUrlPatternString);
        String stationNamePatternString = "^.*script.*src=\"(.*station_name[^\"]*)\".*$";
        Pattern stationNamePattern = Pattern.compile(stationNamePatternString);
        try (
                StringReader stringReader = new StringReader(body);
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
            if (!stationVersion.getStationNameUri().equals(stationNameUri)) {
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
     *
     * @param cLeftTicketUri  这个URI会一直发生变化，所以需要动态传入
     * @param deptDate        出发日期，例如2018-12-29
     * @param deptStationName 出发站点名称，例如杭州东，注意必须是无歧义的站点名称
     * @param arrStationName  到达站点名称，例如宁波，注意必须是无歧义的站点名称
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

