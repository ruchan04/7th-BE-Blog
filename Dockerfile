# 1. 빌드 스테이지
FROM amazoncorretto:17-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test -x compileTestJava -x processTestResources --no-daemon

# 2. 실행 스테이지 (이 부분도 똑같이 amazoncorretto로 통일!)
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]