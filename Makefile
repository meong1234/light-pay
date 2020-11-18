all: run-docker clean db-setup build test

db-setup: db-migrate testdb-migrate

db-migrate:
	./gradlew migrateDb

testdb-migrate:
	APP_ENVIRONMENT=test ./gradlew migrateTestDb

run-server:
	bash -c "set -a && source ./environtment.monolith.sample && set +a && java -jar ./app/build/libs/app-all.jar"

run-docker:
	docker-compose down && docker-compose up -d

test:
	APP_ENVIRONMENT=TEST ./gradlew test

build:
	./gradlew build

clean:
	./gradlew clean