# 1. 빌드 스테이지
FROM amazoncorretto:17-alpine AS build
WORKDIR /app

# 모든 프로젝트 파일을 복사합니다.
COPY . .

# 윈도우 환경에서 올라온 gradlew 파일의 줄바꿈과 권한 문제를 도커 내부에서 강제로 강수 처방합니다.
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

# 캐시나 로컬 환경에 구애받지 않도록 가장 원초적인 컴파일 명령을 수행합니다.
RUN ./gradlew bootJar --no-daemon -x test

# 2. 실행 스테이지
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]