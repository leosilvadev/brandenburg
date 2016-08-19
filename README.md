# Vert.x API Proxy [![Build Status](https://travis-ci.org/leosilvadev/vertx-api-proxy.svg?branch=master)](https://travis-ci.org/leosilvadev/vertx-api-proxy) [![Coverage Status](https://coveralls.io/repos/github/leosilvadev/vertx-api-proxy/badge.svg?branch=master)](https://coveralls.io/github/leosilvadev/vertx-api-proxy?branch=master)
Implement a lightweight and simple API Proxy with Vert.x

### Goals
- [ ] Flexible way to bind all endpoints from an API to the Proxy
- [ ] Flexible way to bind only specifics endpoints to the Proxy
- [ ] Distributed Configuration based on Github
- [ ] Tests coverage
- [ ] How To Documentation
- [ ] Benchmarks using some cool tool such as [loader.io](http://loader.io)


### How to contribute?
- Fork it
- Find bugs and open issues
- Implement tests (Spock)
- Implement features


### How to use

Clone the project
```
git clone https://github.com/leosilvadev/vertx-api-proxy.git
cd vertx-api-proxy
```

Configure your routes files (routes.json)
```json
{
	"books-api": {
		"url": "http://localhost:8090",
		"bind": {
			"active": true,
			"path":"/books",
			"append_path":true
		}
	},
	"sale-api": {
		"url": "http://localhost:8090",
		"endpoints": [{
			"from": {
				"method":"PUT",
				"/v1/users",
			},
			"to": {
				"method":"POST",
				"/users/update"
			}
		}, {
			"from": {
				"method":"GET",
				"/v1/users",
			},
			"to": {
				"/users"
			}
		}]
	}
}
```
- **books-api:** is your api identifier (must be unique)
- **books-api.url:** its url (really?)
- **books-api.bind:** define if your proxy will bind all endpoints from the given api
- **books-api.bind.active:** must be configured but inactivated
- **books-api.bind.path:** all the api endpoints will be bound to this path
- **books-api.bind.append_path:** define if your api will have the requests appended with the given path
- **books-api.endpoints:** the list of your endpoints (used to define specifics endpoints instead of the whole api)
- **books-api.endpoints.from:** define the endpoint registered at your proxy (that will forward to your api)
- **books-api.endpoints.from.method:** the method to be used at your proxy
- **books-api.endpoints.from.path:** the path to be used at your proxy
- **books-api.endpoints.to:** define the endpoint to be forwarded (inside the given api url)
- **books-api.endpoints.to.method:** the method to be forwarded (if empty the from.method will be used)
- **books-api.endpoints.to.path:** the path to be forwarded

Build your proxy
```
./gradlew installDist
```

Run your proxy
```
./build/install/vertx-api-proxy/bin/vertx-api-proxy
```
