# Docker 镜像构建
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>
FROM openjdk:17-alpine

# Copy local code to the container image.
WORKDIR /app
COPY target/spring-cloud-sandbox-0.0.1-SNAPSHOT.jar /app/spring-cloud-sandbox-0.0.1-SNAPSHOT.jar

EXPOSE 8088

# Run the web service on container startup.
CMD ["java","-jar","/app/spring-cloud-sandbox-0.0.1-SNAPSHOT.jar"]