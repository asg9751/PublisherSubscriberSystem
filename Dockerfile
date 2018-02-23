# Download and open openjdk image from net
FROM openjdk
# Make project directory in image
CMD mkdir project
# Copy corrent folder to project directory
COPY . project
# Switch working directory to project
WORKDIR /project
# Run program
CMD java -cp "out:lib/gson-2.6.2.jar:lib/sqlite-jdbc-3.21.0.jar:database/pubsub.db" edu.rit.CSCI652.impl.$SYSTEM_TYPE
