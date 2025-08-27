#informing the docker to use maven Image
FROM maven:3.8.3-openjdk-17 AS MAVEN_BUILD
#Setting the working directory
WORKDIR /build/
#Copy the pom.xml into working directory
COPY pom.xml /build/pom.xml
#Copy the src into working directory
COPY src /build/src
#Running the mvn command to perform packaging
RUN mvn package -DskipTests
#Inform the docker to use JRE image to execute the package generated in previous stage
FROM eclipse-temurin:17-jdk-jammy
#Setting current Working directory
WORKDIR /app

# Set library path for native dependencies
ENV LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu

#Copy the artifact generated in previous build to working directory
COPY --from=MAVEN_BUILD /build/target/*.jar /app/finalartifact.jar
#Defining the entrypoint
ENTRYPOINT ["java","-jar", "finalartifact.jar"]

