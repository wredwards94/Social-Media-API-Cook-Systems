Assessment 1
===============================

## Overview

For this assessment, you are tasked with implementing a RESTful API using Spring Boot, JPA, and Postgresql. Specifically, you will be implementing an API that exposes operations for social media data that resembles the conceptual model of Twitter.

You will implement this API from scratch, working from a series of endpoint specifications (found at the end of this document) to develop a mental model of the data. You will develop a suitable database schema and write Spring services and controllers to handle requests, perform validation and business logic, and to transform data between the API and database models.

## Testing the API
Included in this skeleton are 2 json files required to run the test suite for this final project. To run the tests you will need postman's newman CLI. To install newman run the command `npm install -g newman`. Once newman is installed you need to navigate to the folder containing the Assessment 1 Test Suite & Assessment 1 environment json files. Once there you can run the command `newman run "Assessment 1 Test Suite.postman_collection.json" -e "Assessment 1.postman_environment.json"`. When all tests are passing successfully you will pass 330 assertions and should see something similar to the following in your terminal:

<img width="458" alt="successful_tests" src="https://user-images.githubusercontent.com/12191780/222555974-53992ad3-155c-4e77-9205-bc3b908e093c.png">


## Reading these Requirements

### RESTful Endpoint Methods and URLs
Each endpoint you are required to implement is documented by the REST method and URL used to access it. For example, an endpoint used to access the list of dogs maintained by a server might be described like:

`GET  dogs`

This tells us that the endpoint requires the `GET` HTTP method and is located at the `dogs` url, i.e. at `http://host:port/dogs`.

#### URL Variables
Some endpoints have variables in their urls, and these are represented by a variable name surrounded by curly braces. For example, an endpoint that returns a breed of dog by name might be described by the following syntax:

`GET breeds/{breedName}`

This tells us that the endpoint captures the path segment following `breeds/` with the variable `breedName`.

Remember that the curly braces themselves are not part of the url, but anything outside of them is.

#### Trailing Slashes
The endpoint specifications never supply a trailing slash, but they are allowed. It is up to you to decide whether you prefer trailing slashes for API endpoint URLs or not, but whichever you choose, be consistent from endpoint to endpoint.

### Types and Object Properties
The syntax used to describe the request and response bodies for each required api endpoint is a variation of javascript's object literal syntax, in order to promote legibility, but the endpoints themselves should use JSON to represent data.

Object literals are used to describe the shape of each data model, and property values are used to describe the property's data type. For example, a `Dog` data type might be described by the following syntax:
```javascript
{ // Dog
  name: 'string',
  age: 'integer'
}
```
This tells us that a dog has two properties, `name` and `age`, and that they should be a `string` and `integer`, respectively.

#### Optional properties
Some properties are optional, meaning that they can be represented by `undefined` in javascript or `null` in java or sql. This is represented by giving a `?` suffix to the property name. For example, a `Dog` type like the one defined before could have an optional nickname, which might be described by the following syntax:
```javascript
{ // Dog
  name: 'string',
  nickname?: 'string',
  age: 'integer'
}
```
This tells us that a `Dog` has a property `nickname` that may be 'string' or may not be present at all.

Any properties without a `?` suffix should be considered required.

Keep in mind this is not valid javascript or JSON syntax, and that the `?` is not part of the property name.

#### Built-in Types
Some types, like `'string'` and '`integer`', mean exactly what you would expect them to - they refer to simple types common to both Java and JSON. Others, though, are less obvious, and some require different representations in Java, JSON, and SQL.

To ensure consistency, here is a quick overview of some of the common types used in this specification.

- `'string'` refers to a string of unicode characters, and can be represented by the `String` types in all relevant languages
- `'integer'` refers to a 32-bit signed integer, and can be represented by the `number` type in JSON and the `Integer` type in Java.
- `'timestamp'` refers to a UNIX timestamp, i.e. the number of milliseconds since the beginning of the UNIX epoch - January 1, 1970. In JSON, this should be represented as a number, specifically a `long`. On the server side, as well as in the database, this should be represented as an instance of `java.sql.Timestamp`.

#### Custom Types
Property types can also refer to types defined in this specification. For example, an owner for the `Dog` type defined above might be described by the following syntax:
```javascript
{ // Owner
  dog: 'Dog'
}
```
This tells us that an `Owner` has a property `dog` that is described by the `Dog` type, defined elsewhere in the specification.

#### Anonymous Types
Sometimes a type is never reused in the specification. In those cases, an object literal can be used to describe the type without naming it. For example, a `ChewToy` data type might be described by the following syntax:
```javascript
{ // ChewToy
  material: 'string',
  color: 'string',
  dimensions: {
    width: 'integer',
    height: 'integer'
  }
}
```
Here we could have defined a `Dimensions` type with the following syntax:
```javascript
{ // Dimensions
  width: 'integer',
  height: 'integer'
}
```
But if `ChewToy` is the only type that makes use of `Dimensions`, it's easier to define `Dimensions` as an anonymous type.

#### Array Types
If a property should be an array of a specific type of element, it is represented as an array literal with the element type as a string inside the array. For example, a kennel with a list of dogs might be described by the following syntax:
```javascript
{ // Kennel
  dogs: ['Dog']
}
```
This tells us that a `Kennel` has a property `dogs` that is an array of elements, the type of each of which is described by the `Dog` type

## Entity Relationship Diagram
![Spring Assessment ERD](https://user-images.githubusercontent.com/12191780/187276918-ccb2d373-be3b-42ff-a74d-5560ba806a10.png)


This ERD represents the database that students will create for this project. Students should only create three classes, `User`, `Tweet`, and `Hashtag`, annotated with `@Entity`. There are, however, two additional classes that students will need to create for this project: `Credentials` and `Profile`. These two classes will be annotated with `@Embeddable` and will be used inside of the `User` entity class with the `@Embedded` annotation. This allows us to maintain credentials and profile as seperate objects in Java while still being stored in just one table in the database.

**IMPORTANT:** The `User` entity will also need to use an `@Table(name=<newName>)` annotation to give its table a different name as `user` is a reserved keyword in PostgreSQL.

## API Data Types
The semantics of the operations exposed by the API endpoints themselves are discussed in the following section, but in this section, the API data model is defined and the conceptual model for the application is explained in some depth. Additionally, some hints and constraints for the database model are discussed here.

In general, the data types defined here are in their general, read-only forms. That means that these are the versions of the models that are returned by `GET` operations or nested inside other objects as auxiliary data. Most `POST` operations, which often create new records in the database, require specialized versions of these models. Those special cases are covered by the endpoint specifications themselves unless otherwise noted.

### User
A user of the application. The `username` must be unique. The `joined` timestamp should be assigned when the user is first created, and must never be updated.
```javascript
{ // User
  username: 'string',
  profile: 'Profile',
  joined: 'timestamp'
}
```

### Profile
A user's profile information. Only the `email` property is required.
```javascript
{ // Profile
  firstName?: 'string',
  lastName?: 'string',
  email: 'string',
  phone?: 'string'
}
```

### Credentials
A user's credentials. These are mostly used for validation and authentication during operations specific to a user. Passwords are plain text for the sake of academic simplicity, and it should be kept in mind that this is never appropriate in the real world.
```javascript
{ // Credentials
  username: 'string',
  password: 'string'
}
```

### Hashtag
A hashtag associated with tweets that contain its label. The `label` property must be unique, but is case-insensitive. The `firstUsed` timestamp should be assigned on creation, and must never be updated. The `lastUsed` timestamp should be updated every time a new tweet is tagged with the hashtag.
```javascript
{ // Hashtag
  label: 'string',
  firstUsed: 'timestamp',
  lastUsed: 'timestamp'
}
```

## Tweet
A tweet posted by a user. The `posted` timestamp should be assigned when the tweet is first created, and must not be updated.

There are three distinct variations of tweets: simple, repost, and reply.
- A simple tweet has a `content` value but no `inReplyTo` or `repostOf` values
- A repost has a `repostOf` value but no `content` or `inReplyTo` values
- A reply has `content` and `inReplyTo` values, but no `repostOf` value

```javascript
{ // Tweet
  id: 'integer'
  author: 'User',
  posted: 'timestamp',
  content?: 'string',
  inReplyTo?: 'Tweet',
  repostOf?: 'Tweet'
}
```

### Context
The reply context of a tweet. The `before` property represents the chain of replies that led to the `target` tweet, and the `after` property represents the chain of replies that followed the `target` tweet.

The chains should be in chronological order, and the `after` chain should include all replies of replies, meaning that all branches of replies must be flattened into a single chronological list to fully satisfy the requirements.
```javascript
{ // Context
  target: 'Tweet',
  before: ['Tweet'],
  after: ['Tweet']
}
```

## API Endpoints

### `GET   validate/tag/exists/{label}`
Checks whether or not a given hashtag exists.

#### Response
```javascript
'boolean'
```

### `GET   validate/username/exists/@{username}`
Checks whether or not a given username exists.

#### Response
```javascript
'boolean'
```

### `GET   validate/username/available/@{username}`
Checks whether or not a given username is available.

#### Response
```javascript
'boolean'
```

### `GET     users`
Retrieves all active (non-deleted) users as an array.

#### Response
```javascript
['User']
```

### `POST    users`
Creates a new user. If any required fields are missing or the `username` provided is already taken, an error should be sent in lieu of a response.

If the given credentials match a previously-deleted user, re-activate the deleted user instead of creating a new one.

#### Request
```javascript
{
  credentials: 'Credentials',
  profile: 'Profile'
}
```

#### Response
```javascript
'User'
```

### `GET     users/@{username}`
Retrieves a user with the given username. If no such user exists or is deleted, an error should be sent in lieu of a response.

#### Response
```javascript
'User'
```


### `PATCH   users/@{username}`
Updates the profile of a user with the given username. If no such user exists, the user is deleted, or the provided credentials do not match the user, an error should be sent in lieu of a response. In the case of a successful update, the returned user should contain the updated data.

#### Request
```javascript
{
  credentials: 'Credentials',
  profile: 'Profile'
}
```

#### Response
```javascript
'User'
```

### `DELETE  users/@{username}`
"Deletes" a user with the given username. If no such user exists or the provided credentials do not match the user, an error should be sent in lieu of a response. If a user is successfully "deleted", the response should contain the user data prior to deletion.

**IMPORTANT:** This action should not actually drop any records from the database! Instead, develop a way to keep track of "deleted" users so that if a user is re-activated, all of their tweets and information are restored.

#### Request
```javascript
'Credentials'
```

#### Response
```javascript
'User'
```

### `POST    users/@{username}/follow`
Subscribes the user whose credentials are provided by the request body to the user whose username is given in the url. If there is already a following relationship between the two users, no such followable user exists (deleted or never created), or the credentials provided do not match an active user in the database, an error should be sent as a response. If successful, no data is sent.

#### Request
```javascript
'Credentials'
```

### `POST    users/@{username}/unfollow`
Unsubscribes the user whose credentials are provided by the request body from the user whose username is given in the url. If there is no preexisting following relationship between the two users, no such followable user exists (deleted or never created), or the credentials provided do not match an active user in the database, an error should be sent as a response. If successful, no data is sent.

#### Request
```javascript
'Credentials'
```

### `GET     users/@{username}/feed`
Retrieves all (non-deleted) tweets authored by the user with the given username, as well as all (non-deleted) tweets authored by users the given user is following. This includes simple tweets, reposts, and replies. The tweets should appear in reverse-chronological order. If no active user with that username exists (deleted or never created), an error should be sent in lieu of a response.

#### Response
```javascript
['Tweet']
```

### `GET     users/@{username}/tweets`
Retrieves all (non-deleted) tweets authored by the user with the given username. This includes simple tweets, reposts, and replies. The tweets should appear in reverse-chronological order. If no active user with that username exists (deleted or never created), an error should be sent in lieu of a response.

#### Response
```javascript
['Tweet']
```

### `GET     users/@{username}/mentions`
Retrieves all (non-deleted) tweets in which the user with the given username is mentioned. The tweets should appear in reverse-chronological order. If no active user with that username exists, an error should be sent in lieu of a response.

A user is considered "mentioned" by a tweet if the tweet has `content` and the user's username appears in that content following a `@`.

#### Response
```javascript
['Tweet']
```

### `GET     users/@{username}/followers`
Retrieves the followers of the user with the given username. Only active users should be included in the response. If no active user with the given username exists, an error should be sent in lieu of a response.

#### Response
```javascript
['User']
```

### `GET     users/@{username}/following`
Retrieves the users followed by the user with the given username. Only active users should be included in the response. If no active user with the given username exists, an error should be sent in lieu of a response.

#### Response
```javascript
['User']
```

### `GET     tags`
Retrieves all hashtags tracked by the database.

#### Response
```javascript
['Hashtag']
```

### `GET     tags/{label}`
Retrieves all (non-deleted) tweets tagged with the given hashtag label. The tweets should appear in reverse-chronological order. If no hashtag with the given label exists, an error should be sent in lieu of a response.

A tweet is considered "tagged" by a hashtag if the tweet has `content` and the hashtag's label appears in that content following a `#`

#### Response
```javascript
['Tweet']
```

### `GET     tweets`
Retrieves all (non-deleted) tweets. The tweets should appear in reverse-chronological order.

#### Response
```javascript
['Tweet']
```

### `POST    tweets`
Creates a new simple tweet, with the author set to the user identified by the credentials in the request body. If the given credentials do not match an active user in the database, an error should be sent in lieu of a response.

The response should contain the newly-created tweet.

Because this always creates a simple tweet, it must have a `content` property and may not have `inReplyTo` or `repostOf` properties.

**IMPORTANT:** when a tweet with `content` is created, the server must process the tweet's content for `@{username}` mentions and `#{hashtag}` tags. There is no way to create hashtags or create mentions from the API, so this must be handled automatically!

#### Request
```javascript
{
  content: 'string',
  credentials: 'Credentials'
}
```

#### Response
```javascript
'Tweet'
```

### `GET     tweets/{id}`
Retrieves a tweet with a given id. If no such tweet exists, or the given tweet is deleted, an error should be sent in lieu of a response.

#### Response
```javascript
'Tweet'
```

### `DELETE  tweets/{id}`
"Deletes" the tweet with the given id. If no such tweet exists or the provided credentials do not match author of the tweet, an error should be sent in lieu of a response. If a tweet is successfully "deleted", the response should contain the tweet data prior to deletion.

**IMPORTANT:** This action should not actually drop any records from the database! Instead, develop a way to keep track of "deleted" tweets so that even if a tweet is deleted, data with relationships to it (like replies and reposts) are still intact.

#### Request
```javascript
'Credentials'
```

#### Response
```javascript
'Tweet'
```

### `POST    tweets/{id}/like`
Creates a "like" relationship between the tweet with the given id and the user whose credentials are provided by the request body. If the tweet is deleted or otherwise doesn't exist, or if the given credentials do not match an active user in the database, an error should be sent. Following successful completion of the operation, no response body is sent.

#### Request
```javascript
'Credentials'
```

### `POST    tweets/{id}/reply`
Creates a reply tweet to the tweet with the given id. The author of the newly-created tweet should match the credentials provided by the request body. If the given tweet is deleted or otherwise doesn't exist, or if the given credentials do not match an active user in the database, an error should be sent in lieu of a response.

Because this creates a reply tweet, content is not optional. Additionally, notice that the `inReplyTo` property is not provided by the request. The server must create that relationship.

The response should contain the newly-created tweet.

**IMPORTANT:** when a tweet with `content` is created, the server must process the tweet's content for `@{username}` mentions and `#{hashtag}` tags. There is no way to create hashtags or create mentions from the API, so this must be handled automatically!

#### Request
```javascript
{
  content: 'string',
  credentials: 'Credentials'
}
```

#### Response
```javascript
'Tweet'
```

### `POST    tweets/{id}/repost`
Creates a repost of the tweet with the given id. The author of the repost should match the credentials provided in the request body. If the given tweet is deleted or otherwise doesn't exist, or the given credentials do not match an active user in the database, an error should be sent in lieu of a response.

Because this creates a repost tweet, content is not allowed. Additionally, notice that the `repostOf` property is not provided by the request. The server must create that relationship.

The response should contain the newly-created tweet.

#### Request
```javascript
'Credentials'
```

#### Response
```javascript
'Tweet'
```

### `GET     tweets/{id}/tags`
Retrieves the tags associated with the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

**IMPORTANT** Remember that tags and mentions must be parsed by the server!

#### Response
```javascript
['Hashtag']
```

### `GET     tweets/{id}/likes`
Retrieves the active users who have liked the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

Deleted users should be excluded from the response.

#### Response
```javascript
['User']
```

### `GET     tweets/{id}/context`
Retrieves the context of the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

**IMPORTANT:** While deleted tweets should not be included in the `before` and `after` properties of the result, transitive replies should. What that means is that if a reply to the target of the context is deleted, but there's another reply to the deleted reply, the deleted reply should be excluded but the other reply should remain.

#### Response
```javascript
'Context'
```

### `GET     tweets/{id}/replies`
Retrieves the direct replies to the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

Deleted replies to the tweet should be excluded from the response.

#### Response
```javascript
['Tweet']
```

### `GET     tweets/{id}/reposts`
Retrieves the direct reposts of the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

Deleted reposts of the tweet should be excluded from the response.

#### Response
```javascript
['Tweet']
```

### `GET     tweets/{id}/mentions`
Retrieves the users mentioned in the tweet with the given id. If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.

Deleted users should be excluded from the response.

**IMPORTANT** Remember that tags and mentions must be parsed by the server!

#### Response
```javascript
['User']
```
