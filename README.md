# Tick Tack Toe

A relatively simple game server and client to play Tick Tack Toe and related games. The board size and the win condition
(amount of marks in a row) can be fully customised, and it also has a connect four -like mode where marks "fall" to the
bottom of the board when played.

This project was made for the [Twoday code challenge](https://twoday.fi/koodihaaste) (website is in Finnish)

## Overview

[The backend](backend) is built with [Spring Boot](https://spring.io), with [MongoDB](https://www.mongodb.com/) as the
database solution, and [the frontend](frontend) is using the [libGDX](https://libgdx.com/) game library, as well as the
[Apache HttpClient](https://hc.apache.org/httpcomponents-client-5.2.x/) for REST calls and Spring's Websocket components
for realtime communication. [The api module](api) contains the Dto objects that are shared between the backend and the
frontend, as well as some shared constants.

More information about the parts of the project can be found in their respective module readme files.

## Usage

Both the backend and the frontend require Java 17 or newer to run. Build the projects using [Gradle](https://gradle.org/)
or download prebuilt runnable jar files from [Releases](https://github.com/melodicore/ticktacktoe/releases). The backend
also needs MongoDB running with the default settings (localhost:27017, no login credentials)

## Rambling

This is my first time ever using Spring Boot. Before starting this project, I had heard of it but hadn't even seen any
code. It is also the first time I write an actual fullstack application. But learning new things is fun and despite the
stress about the deadline (which is today at the time of writing this) I've enjoyed this project a lot.

I'm running out of time so the code isn't the cleanest, there are no tests, there's a lot of spaghetti in the frontend
and lots of multiplied code that could be abstracted away, but right now my priority is getting this thing out in time.
If I have any motivation left afterwards, I'll revisit this project and clean some things up.