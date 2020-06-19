FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

COPY ./cert/kuunika-all.crt /usr/local/share/ca-certificates/kuunika-all.crt
COPY ./cert/kuunika-org.crt /usr/local/share/ca-certificates/kuunika-all.crt
COPY ./cert/kuunika-org-second.crt /usr/local/share/ca-certificates/kuunika-all.crt

RUN update-ca-certificates