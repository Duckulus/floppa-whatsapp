FROM amazoncorretto:17 AS builder

WORKDIR /gradle

RUN yum install binutils -y

#Build JRE to reduce image size
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules java.base,java.desktop,java.instrument,java.management,jdk.crypto.cryptoki,java.naming,java.net.http,java.rmi,java.sql,jdk.compiler,jdk.unsupported \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew shadowJar

FROM ubuntu:latest AS runner

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=builder /customjre $JAVA_HOME

WORKDIR /app

RUN apt-get update && apt-get install -y ffmpeg && apt-get install -y locales && apt-get install -y yt-dlp

# Set the locale
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN apt-get clean && apt-get autoclean && apt-get autoremove

COPY --from=builder /gradle/build/libs/*.jar /app/floppa.jar

CMD ["/jre/bin/java", "--enable-preview", "-jar", "./floppa.jar"]