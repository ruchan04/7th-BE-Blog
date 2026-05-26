# 1. 빌드 스테이지 (유찬님 프로젝트에 맞춘 자바 21 환경)
FROM amazoncorretto:21-alpine AS build
WORKDIR /app

# 모든 프로젝트 파일을 복사합니다.
COPY . .

# 줄바꿈 문자 호환성 해결
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

# 툴체인 자동 다운로드를 강제로 끄고, 컨테이너에 깔린 자바 21 엔진으로 빌드합니다.
RUN ./gradlew bootJar --no-daemon -x test -Porg.gradle.java.installations.auto-download=false

# 2. 실행 스테이지
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]