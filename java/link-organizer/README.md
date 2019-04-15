# Links Organizer

## Introduction

I'm a tabaholic. One thing I haven't found a great tool for is collecting and sorting links I intend to read, bookmark,
or otherwise deal with later. There is Pocket and Raindrop, but the UIs of those services aren't designed to easily
handle large volumes of links. So this is a simple start to a solution that would address this.

## Building and running the app

### System requirements

These are the dependencies I used for this project.

* Java 8
* Gradle 5.3.1

### Running in Docker

1. `gradlew clean build`
1. `docker-compose up -d`
1. Visit _http://localhost:8080/_ (credentials are user/password and admin/password)

### Running from IDE or other local means

The default properties expect a MySQL running on 3306. You're free to do that any way you wish. If you want to run
just that in Docker, you can do so with `docker-compose up -d db`. You may need to do port forwarding in your VM if you
are running Docker on Mac or Windows so the port can be exposed to the host. 

## Future improvements

* Use database for Spring Security authentication (hashing and salting passwords, e.g. BCrypt)
* Associate links and tags with specific user accounts
* Add account creation and password recovery features
* Add duplicate link cleanup (including http vs https)
* Add TLS
* API protection (don't allow requests for pages too large, etc)
* Migrate away from HTTP Basic Auth
* Don't use shared credentials between UI and API
* Add pagination to UI
* Add UI for editing links
* Fix UI for editing tags
* Add UI for adding tags to links
* Add input validation/protection (probably don't use IDs) for deleting
* Improve UX
* Extract CSS from in-line
* Add UI test automation (UI is admittedly a quick hack and UI test
  automation is notoriously brittle; I'd add this when UI is more stable)
* I know Roy Fielding regards versions as anti-REST, but I think this would be good to have

## Things learned

[HATEOAS](https://en.wikipedia.org/wiki/HATEOAS), [HAL](http://stateless.co/hal_specification.html),
[@RepositoryRestResource](https://docs.spring.io/spring-data/rest/docs/current/api/org/springframework/data/rest/core/annotation/RepositoryRestResource.html),
and [Bowman](https://github.com/BlackPepperSoftware/bowman/) were new to me, but it seemed a quick and easy way to create an API.
I chose Thymeleaf not because I believe it's a superior technical choice, but it's what I was somewhat familiar with and
would let me get up and running quickly.

In retrospect, I'm not sure the choice of HAL was a good one. I spent a lot of time fighting getting it working, and for
some things, I didn't get it working. For example, editing tags, there has to be some way to map the Bowman client class
resource ID back to the database ID, but I don't see it documented.
