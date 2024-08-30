#FROM eclipse-temurin:21
#MAINTAINER isaev
#COPY build/libs/catalog-1.jar catalog-1.jar
#ENTRYPOINT ["java","-jar","catalog-1.jar"]

FROM eclipse-temurin:17
MAINTAINER isaev
COPY build/libs/catalog-1.jar catalog-1.jar
ENTRYPOINT ["java","-jar","catalog-1.jar"]
