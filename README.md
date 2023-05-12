# Url Shortener Application

## Build

To build application run command `./gradlew[.bat] buildspringapp`.
This command will create an executable jar file with name `url-shortener-app.jar` under `./build/spring/` directory.

## Run
To run application you can execute command `./gradlew[.bat] runspringapp`.
This command will execute build command under the hood.
To pass environment variables to task you can pass them in this format : `-P[ENV_NAME]=[ENV_VALUE]` eg: `-PSERVER_PORT=9001`

**_Note: also you can run the executable jar file directly._**

## API
To see api documentation you can visit `http[s]://domain:[port]/swagger` in your favorite browser.

## TODO 
complete full document !
