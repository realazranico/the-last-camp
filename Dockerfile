FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY TheLastCampServer.java index.html ./
RUN javac TheLastCampServer.java
ENV PORT=10000
EXPOSE 10000
CMD ["java", "TheLastCampServer"]
