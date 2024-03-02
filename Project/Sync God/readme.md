# Sync God

#### Installation
---

---
---
Simply use the command
```
```
mvn package
#### Pre Run - Only for Linux

Unfortunatly you have to build OpenCV yourself, this is how to do it!

1. Download [OpenCV](https://opencv.org/releases.html) Sources Version 3.4.1 or later
2. Run following lines in terminal (you might want to grab a cup of coffee, this can take a while)

    ```
    make -j4
    cmake -D BUILD_SHARED_LIBS=false <path to OpenCV Sources>

4. Run it!

You can place the .so file anywhere if you want to. The project folder is the default folder where Sync God expects it, you have to select it before starting Sync God when it is not in the project folder.
##### Note: 


To run the programm use
```
java -jar SyncGod-3.0.jar
```

Windows
```
java -javaagent:WinFixAgent.jar -jar SyncGod-3.0.jar
```
#### Run
