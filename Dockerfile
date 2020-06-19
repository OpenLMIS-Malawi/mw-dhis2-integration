FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

RUN update-ca-certificates