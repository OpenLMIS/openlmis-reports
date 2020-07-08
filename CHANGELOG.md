1.2.1 / 2020-07-08
=================

Bugs:
* [OLMIS-6887](https://openlmis.atlassian.net/browse/OLMIS-6887): Fixed a problem with subsequent jasper reports generation

1.2.0 / 2020-04-14
==================

New functionality added in a backwards-compatible manner:
* [OLMIS-6760](https://openlmis.atlassian.net/browse/OLMIS-6760): Update Spring Boot version to 2.x:
  * Spring Boot version is 2.2.2.
  * Flyway is at 6.0.8, new mechanism for loading Spring Security for OAuth2 (matching Spring Boot version), new versions for REST Assured, RAML tester, RAML parser, PowerMock, Mockito (so tests will pass) and Java callback mechanism has changed to a general handle() method.
  * Spring application properties for Flyway have changed.
  * Re-implement generation of Jasper reports (PDF, CSV, XLS, HTML).
  * Fix repository method signatures (findOne is now findById, etc.); additionally they return Optional.
  * Fix unit tests.
  * Fix integration tests.
  * API definitions require "Keep-Alive" header for web integration tests.

1.1.4 / 2019-10-17
=================

Improvements:
* [OLMIS-4128](https://openlmis.atlassian.net/browse/OLMIS-4128): Change maximum page size to max integer.
* [OLMIS-6129](https://openlmis.atlassian.net/browse/OLMIS-6129): Added package-lock.json file.
* [OLMIS-6494](https://openlmis.atlassian.net/browse/OLMIS-6494): Updated Pick Pack report to display the correct version of an orderable.
* [OLMIS-6564](https://openlmis.atlassian.net/browse/OLMIS-6564): Changed wiremock dependency configuration to avoid issue with HTTP response compression.

1.1.3 / 2019-05-27
==================

Improvements:
* [OLMIS-4531](https://openlmis.atlassian.net/browse/OLMIS-4531): Added compressing HTTP POST responses.

1.1.2 / 2018-12-12
==================

Improvements:
* [OLMIS-4940](https://openlmis.atlassian.net/browse/OLMIS-4940): Ensured that the microservice gets system time zone from configuration settings on startup.
* [OLMIS-4295](https://openlmis.atlassian.net/browse/OLMIS-4295): Updated checkstyle to use newest google style.
* [OLMIS-4942](https://openlmis.atlassian.net/browse/OLMIS-4942): Added currency, number and date settings to application properties.

1.1.1 / 2018-08-16
==================

Improvements:
* [OLMIS-4649](https://openlmis.atlassian.net/browse/OLMIS-4649): Added Jenkinsfile
* [OLMIS-4459](https://openlmis.atlassian.net/browse/OLMIS-4459): Pick Pack List no longer displays values in a separate line if there's no lot defined

Bugs:
* [OLMIS-4815](https://openlmis.atlassian.net/browse/OLMIS-4815): Fixed lack of update on Facility Assignment Configuration Errors report
* [OLMIS-3289](https://openlmis.atlassian.net/browse/OLMIS-3289): Fixed Null values in the Facility Assignment Report columns

1.1.0 / 2018-04-24
==================

New functionality added in a backwards-compatible manner::
* [OLMIS-239](https://openlmis.atlassian.net/browse/OLMIS-239): Add possibility to generate Pick Pack List report
* [OLMIS-4197](https://openlmis.atlassian.net/browse/OLMIS-4197): Add possibility to generate CCE alert history report 

Bug fixes added in a backwards-compatible manner::
* [OLMIS-3778](https://openlmis.atlassian.net/browse/OLMIS-3778): Fixed service checks the rights of a wrong user

1.0.0 / 2017-09-01
==================

* Released openlmis-report 1.0.0 as part of openlmis-ref-distro 3.2.1.
  * This was the first stable release of openlmis-report.
