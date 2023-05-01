# Tick Tack Toe - Backend

The backend of my Tick Tack Toe application. Built with [Spring Boot](https://spring.io), it uses both REST and
Websockets to communicate with the client. This is my first ever project with Spring Boot, and so all of my conventions
and usage are derived from various example projects and the documentation.

## Running

To run the backend, you first must have [MongoDB](https://www.mongodb.com/) running with the default settings
(localhost:27017, no login credentials). Make sure to not have a database with the name `ticktacktoe` already existing.
Run the project with the [Gradle](https://gradle.org/) task `application/bootRun` or download a runnable jar file from
[Releases](https://github.com/melodicore/ticktacktoe/releases) and run it. The project requires Java 17 or newer to run.