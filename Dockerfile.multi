# 멀티 스테이지 빌드
# 1) 빌드 스테이지
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN ./gradlew clean build -x test

# 2) 실행에 필요한 최소 실행 스테이지
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/app.jar"]
