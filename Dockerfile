FROM alpine:latest AS builder

WORKDIR /gradle

RUN apk add openjdk17
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apk add binutils

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

#RUN chmod +x ./gradlew

RUN apk add gradle

RUN gradle shadowJar --no-daemon

FROM alpine:latest AS runner

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=builder /customjre $JAVA_HOME

WORKDIR /app

COPY --from=builder /gradle/build/libs/*.jar /app/floppa.jar

CMD ["/jre/bin/java", "--enable-preview", "-jar", "./floppa.jar"]