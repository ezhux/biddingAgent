# Bidding agent


#### Start app

sbt run

#### Query test campaigns

Upon successful start, application generates 50 random Campaigns. You can query them using this URL:

http://localhost:8080/campaign/1

This will give you campaign details that you can use for testing.

#### Place a bid

URL: http://localhost:8080/bid

Example query:

curl -v --header "Content-Type: application/json" --request POST --data '{"id":"aaa","site":{"domain":"domain","id":4888},"user":{"geo":{"city":"Riga","country":"Latvia"},"id":"userId"}}' http://localhost:8080/bid

HTTP status code 204 means that no match was found

Only matching for user's location and Site id is implemented, but matching for banner size and price can be added in the same manner.

#### Implementation details

Each match is performed in a one-off actor.

