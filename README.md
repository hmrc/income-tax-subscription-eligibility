[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/hmrc/income-tax-subscription-eligibility.svg)](https://travis-ci.org/hmrc/income-tax-subscription-eligibility)
[![Download](https://api.bintray.com/packages/hmrc/releases/income-tax-subscription-eligibility/images/download.svg)](https://bintray.com/hmrc/releases/income-tax-subscription-eligibility/_latestVersion)

# Income Tax Subscription Eligibility

This is a Scala/Play backend that provides an API for determining if a user is eligible to register to file income tax returns using 3rd party software.

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

1. [Quick start](#Quick-start)
    - [Prerequisites](#Prerequisites)
    - [How to start](#How-to-start)
    - [How to use](#How-to-use)
    - [How to test](#How-to-test)
2. [Persistence](#Persistence)

# Quick start

## Prerequisites

* [sbt](http://www.scala-sbt.org/)
* MongoDB (*[See Persistence](#Persistence)*)
* HMRC Service manager (*[Install Service-Manager](https://github.com/hmrc/service-manager/wiki/Install#install-service-manager)*)

# API (Not yet available)

| Route               | Identifier                                         | Description                                 |
| ------------------- | -------------------------------------------------- | ------------------------------------------- |
| /eligibility/:sautr | SA UTR (Self Assessment Unique Taxpayer Reference) | Returns OK and json containing eligibility status for a given SA UTR |

# Running the service

## How to start with scripts

**Run the service with `ITSA_SUBSC_ALL`:**  
```
./scripts/start
```

**Run the service with mininal downstreams:**  
```
./scripts/start --minimal
```

## Running locally using SBT

1. Clone the service
2. Navigate to the folder you cloned the service into in a terminal
3. Start the service with `sbt "run 9588 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

## How to use

There is only one api endpoint.  It returns a json representation of the eligibility for the provided utr.

If INCOME_TAX_SUBSCRIPTION_STUBS is running on port 9562 then it will service http requests made by this application.

See Route files for more information.

## How to test

* Run unit tests: `sbt clean test`
* Run integration tests: `sbt clean it:test`

# Persistence

Data is stored as key/value in Mongo DB. See json reads/writes implementations (especially tests) for details.

To connect to the mongo db provided by docker (recommended) please use

```
docker exec -it mongo-db mongosh
```

Various commands are available.  Start with `show dbs` to see which databases are populated.

### License.
 
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
