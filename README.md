# Pet tracker service [![Java CI with Gradle](https://github.com/sekury/pet-tracker/actions/workflows/gradle.yml/badge.svg?branch=mongo)](https://github.com/sekury/pet-tracker/actions/workflows/gradle.yml) ![Coverage](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg)
## Requirements
- Java 21

## Run tests
Run `./gradlew test`

After running the tests, test coverage can be found in `./build/jacocoHtml/index.html`

## Start application
Run `./gradlew bootRun`

## API call examples
### Create a cat tracker
```
curl -X POST --location "http://localhost:8080/api/v1/trackers" \
    -H "Content-Type: application/json" \
    -d '{
          "ownerId": 1,
          "petType": "cat",
          "trackerType": "l",
          "inZone": false,
          "lostTracker": false
        }'
```
### Create a dog tracker
```
curl -X POST --location "http://localhost:8080/api/v1/trackers" \
    -H "Content-Type: application/json" \
    -d '{
          "ownerId": 1,
          "petType": "dog",
          "trackerType": "m",
          "inZone": false
        }'
```
### Update a cat tracker
```
curl -X PUT --location "http://localhost:8080/api/v1/trackers/1" \
    -H "Content-Type: application/json" \
    -d '{
          "ownerId": 2,
          "petType": "cat",
          "trackerType": "l",
          "inZone": false,
          "lostTracker": false
        }'
```
### Update a dog tracker
```
curl -X PUT --location "http://localhost:8080/api/v1/trackers/1" \
    -H "Content-Type: application/json" \
    -d '{
          "ownerId": 2,
          "petType": "dog",
          "trackerType": "l",
          "inZone": false
        }'
```
### Delete a tracker
```
curl -X DELETE --location "http://localhost:8080/api/v1/trackers/1"
```
### Get a tracker
```
curl -X GET --location "http://localhost:8080/api/v1/trackers/1"
```
### Get all trackers
```
curl -X GET --location "http://localhost:8080/api/v1/trackers?page=0&size=10&sort=ownerId%2Cdesc"
```
### Get trackers statistics (returns the number of trackers outside the power safe zone)
```
curl -X GET --location "http://localhost:8080/api/v1/trackers/stats"
```
