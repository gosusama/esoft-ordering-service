# Use a lightweight base image with JDK 17
FROM openjdk:17-oracle

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY ./target/order-service-0.0.1-SNAPSHOT.jar ./

# Expose the port that the application will run on
EXPOSE 8081

# Define the command to run your application
CMD ["java", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]

