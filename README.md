 # LightPay
LightPay is a fintech apps, use for sharing how to build a apps in java

The project itself has 4 modules
 - api
   - a module that abstracts interface for all services
 - commons
   - a module that groups all common functionalities used by all services
 - domain
   - the bussiness logic for all services goes here
 - apps
   - an entry point of our monolithic app, including all handlers
   
 
## Tools
* Java
* Gradle
* Postgres
* Docker

## Java Dependency
* Junit 5
* Spark (http server) [link](http://sparkjava.com/)
* GSON (Json Marshaller) [link](https://github.com/google/gson)
* jdbi (idiomatic access to relational data ) [link](https://jdbi.org/)

## Tutorial
this tutorial 
1. [problem statement](tutorial/problem_statement.md)
2. [references](tutorial/reference.md)  
2. [sequence_diagram](tutorial/sequence_diagram.md)  
## How to use

### Dependencies
 ```
brew cask install java
brew cask install postman
 ```

#### Running DB only Docker
 ```
 make run-docker
 ```

#### Running DB migration
 ```
 make db-setup
 ```

#### How to build
 ```
 make build
 ```

#### Running All Preparations
 ```
 make all
 ```


#### How to run server
 ```
 make run-server
 ```

  


