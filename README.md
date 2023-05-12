# Url Shortener Application

## Build

To build application run command `./gradlew[.bat] buildspringapp`. This command will create an executable jar file with
name `url-shortener-app.jar` under `./build/spring/` directory.

## Run

To run application you can execute command `./gradlew[.bat] runspringapp`. This command will execute build command under
the hood. To pass environment variables to task you can pass them in this format : `-P[ENV_NAME]=[ENV_VALUE]`
eg: `-PSERVER_PORT=9001`
**_Note: also you can run the executable jar file directly._**

## Test

To run tests you can use command `./gradlew[.bat] test`.

## API

To see api documentation you can visit `http[s]://domain:[port]/swagger` in your favorite browser.

## Overview

This is web based application which help to create short url backed by any url. The whole system can be parted as 2
parts, the application itself and external tools to make observability for the service.

## Application

The application fully written in **java-17** and **spring** framework. The version of spring boot is **3.0.6**.

The application layer 3 main layers

- Database
- Service
- RestFul API

### Database

The system will use **PostgreSQL** as main database and **Derby** for test environment database.

There are several configs available for this section which you can use them as environment variables.

| Config Name | Value Type | Default Value | Description|
| :---:   | :---: | :---: | :---: |
| `DB_HOST` | String  | localhost   | database host name
| `DB_PORT` | Int16   | 5432   | database port
| `DB_NAME` | String   | url_shortener_app_db   | database name
| `DB_SSL`  | Boolean   | false   | to use ssl or plain connection
| `DB_USER` | String   | postgres   | database username
| `DB_PASSWORD` | String   | postgres   | database password


### Service
This layer use database and cache to handle application business. With caching urls application prevent from
frequently database request and make more efficient way to handle requests.

Everything in this layer written in java reactor model.

There are several configs available for this section which you can use them as environment variables.

| Config Name | Value Type | Default Value | Description|
| :---:   | :---: | :---: | :---: |
| `URL_REPOSITORY_RETRY_ON_SAVE` | Int  | 3   | number of times which service try to save url in database
| `URL_REPOSITORY_URL_ID_LENGTH` | Int   | 8   | len of url-id which can be between in maximum 20, bigger len will produce bigger url-id
| `URL_REPOSITORY_REACTOR_THREAD_POOL_SIZE` | Int   | 10   | size of reactor thread pools which use to handle database operations.
| `URL_CACHE_TYPE`  | Enum   | IN_MEMORY   | set the cache type for service layer, there is 3 options available. `DISABLE`, `IN_MEMORY` and `REDIS`.
| `URL_CACHE_IN_MEMORY_SIZE` | Int   | 1000   | this option effect only if `URL_CACHE_TYPE` set to `IN_MEMORY`. The system will this as maximum size of in-memory cache
| `URL_CACHE_IN_MEMORY_MAX_VALID_DURATION` | Duration   | 30s   | this option effect only if `URL_CACHE_TYPE` set to `IN_MEMORY`. The system will this as maximum time of in-memory cache value expiration time
| `URL_CACHE_REDIS_ADDRESS` | String   | redis://localhost:6379   | this option effect only if `URL_CACHE_TYPE` set to `REDIS`. The system will this as redis server address
| `URL_CACHE_REDIS_PASSWORD` | String   | redispassword   | this option effect only if `URL_CACHE_TYPE` set to `REDIS`. The system will this as redis server password
| `URL_CACHE_REDIS_CONNECTION_POOL_SIZE` | Int   | 20   | this option effect only if `URL_CACHE_TYPE` set to `REDIS`. The system will this as redis connection-pool size

Also, There is some options available for metrics as follows. If you use this options the application will send metrics to prometheus push-gateway and
prometheus server can scrap the metrics and all metrics will be available on prometheus. Also, you can use grafana for visualization.

| Config Name | Value Type | Default Value | Description|
| :---:   | :---: | :---: | :---: |
| `METRICS_PROMETHEUS_PUSH_GATEWAY_ENABLED` | Boolean  | true   | The system will send metrics to prometheus gateway if this option set to `true`. In case of `false` other configs will be skip.
| `METRICS_PROMETHEUS_PUSH_GATEWAY_ADDRESS` | String   | http://localhost:9091   | Application will use it as prometheus push-gateway address
| `METRICS_PROMETHEUS_PUSH_GATEWAY_USERNAME` | String   | $null   | prometheus push-gateway username
| `METRICS_PROMETHEUS_PUSH_GATEWAY_PASSWORD` | String   | $null   | prometheus push-gateway password
| `METRICS_PROMETHEUS_PUSH_GATEWAY_JOB_NAME` | String   | url-shortener-app   | the job name in prometheus server

And There is some other options available for logging. The option `LOGSTASH_LOG_FILE` will use by application to put logs in a logstash file.
With a good logstash strategy you could send all the data of logstash to elasticsearch and use kibana to observe the system logs.

| Config Name | Value Type | Default Value | Description|
| :---:   | :---: | :---: | :---: |
| `LOG_LEVEL` | Enum  | INFO   | Set the log level of whole system.
| `LOGSTASH_LOG_FILE` | String   | ./docker/files/logstash/logs/url-shortener-app.log   | The system will write logs in logstash encoded format to this file. You can disable it by setting this config to `disable`
| `CONSOLE_LOG_PATTERN` | String   | %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n   | The system will use this pattern to submit logs in console. You can disable console logging by setting this config to `disable`

### RestFul API
You can use this restfull api by using http/1.1 protocol to use application. There is 3 routes exists for application.
To see api documentation you can visit `http[s]://domain:[port]/swagger` in your favorite browser.

There is only one config available for this section which you can use it as environment variables.

| Config Name | Value Type | Default Value | Description|
| :---:   | :---: | :---: | :---: |
| `SWAGGER_ENABLED` | Boolean  | true   | The application will serve swagger if this config setting to `true`

## Observability
The implementation of application will let you have full observability by setting it for. With a good config
you will be able to use `Prometheus` and `Grafana` as metric observability system and `Elasticsearch` and `**Kibana**` for log
monitoring.


## Setup local environment
To set up your local machine to run the application with full feature of application and observability you can use `Docker`.
There is a `docker-compose.yml` file exists under `[ROOT]/docker/docker-compose.yml` which will set up all necessary services in one shot.
You can use command `docker-compose up` to run services.

After docker run services correctly you have to make one thing to make application works. In this step you have
to create database with name and specific credential that you set for application. In cause of default setting values
you have to create a database with name `url_shortener_app_db` ins `PostgreSQL` server which is up now. The server address is
`localhost:5432` and the `postgres` will be use as `username` and `password`.

After this part you can easily run the application and everything will work correctly. For running application you can execute
`./gradlew[.bat] runspringapp` command in root project directory.

After application run successfully you can visit `http://localhost:2999` for `Grafana` and `http://localhost:5601` for `Kibana`.
The Grafana use `admin` as both `username` and `password`. After you logged in successfully you can set a prometheus resource to visualize metrics.
The address of prometheus which you have use in default setting is `http://prometheus:9090`.

# Done ! =)