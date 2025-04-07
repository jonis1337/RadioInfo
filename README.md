# RadioInfo
## Table of Contents
1. [Project Overview](#project-overview)
2. [Installation](#installation)
3. [Screenshots](#screenshots)

## Project Overview
```RadioInfo``` is a Java application that fetches and displays radio schedules from Sveriges Radio’s open API. Built with Java 17, it features a user-friendly GUI, XML parsing, and thread-safe programming using the Model-View-Controller (MVC) pattern. Key components include ApiParser for data retrieval, SwingWorker for background tasks, and a responsive interface with channel and program views.

## Installation
1. Run:
   ```bash
   git clone https://github.com/jonis1337/RadioInfo.git
   ```
2. Run:
   ```bash
   cd RadioInfo
   ```
3. Run:
   ```bash
   javac *.java
   ```
4. Run:
   ```bash
   touch manifest.txt
   ```
5. Run:
   ```bash
   vim manifest.txt
   ```
6. Add:
   ```bash
    Main-Class: Main
   ```
7. Generate a JAR file:
   ```bash
   jar cvfm RadioInfo.jar manifest.txt *.class
   ```
8. Run application:
   ```bash
   java -jar RadioInfo.jar
   
   ```
   

## Screenshots
![Channel Menu Screenshot](photos/RadioInfoChannelMenu.png)
![Table](photos/RadioInfoTable.png)
![Program](photos/RadioInfo.png)
