FROM java:openjdk-8-jre-alpine

MAINTAINER hellozjf 908686171@qq.com

ARG JAR_FILE
ADD target/${JAR_FILE} test12306.jar

EXPOSE 12306 12306

VOLUME /log

ENTRYPOINT ["java", "-jar", "test12306.jar"]