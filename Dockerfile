FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

COPY kuunika_certs/kuunika-all.crt /usr/local/share/ca-certificates/kuunika-all.crt
COPY kuunika_certs/kuunika-org.crt /usr/local/share/ca-certificates/kuunika-org.crt
COPY kuunika_certs/kuunika-org-second.crt /usr/local/share/ca-certificates/kuunika-org-second.crt

RUN update-ca-certificates

ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/etc/ssl/certs/ca-certificates.crt"
ENV JAVA_OPTS="-Djavax.net.ssl.trustStorePassword=openlmis"
