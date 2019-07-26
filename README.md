
# income-tax-subscription-eligibility

This service provides an API for determining if a user is eligible to register to file income tax returns using 3rd party software.

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

## API (Not yet available)

| Route               | Identifier                                         | Description                                 |
| ------------------- | -------------------------------------------------- | ------------------------------------------- |
| /eligibility/:sautr | SA UTR (Self Assessment Unique Taxpayer Reference) | Returns OK and json containing eligibility status for a given SA UTR |

## Running the service

### Running locally using SBT

1. Clone the service
2. Navigate to the folder you cloned the service into in a terminal
3. Start the service with `sbt "run 9588 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

### Using Service Manager

You can either:
* run all the income tax subscription services using `sm --start ITSA_SUBSC_ALL`
* run this service on its own using `sm --start INCOME_TAX_SUBSCRIPTION_ELIGIBILITY`

To stop the service(s), use either of the above commands with '--stop' in place of '--start'
