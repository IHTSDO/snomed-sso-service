[<img alt="Snomed CT" style="height:125px;" src="https://static.wixstatic.com/media/49d95c_f232b9c10b72410b802fbbd35b357698~mv2.png"/>](https://www.snomed.org/)

Main:
[![Build Status](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/main/badge/icon)](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/main/)

Develop:
[![Build Status](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/badge/icon)](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/)
[![Quality Gate Status](https://sonarqube.ihtsdotools.org/api/project_badges/measure?project=org.snomed%3Asnomed-sso-service&metric=alert_status&token=sqb_4ab88d20b777acb2f2ae7e9d0ffed5c55111f00e)](https://sonarqube.ihtsdotools.org/dashboard?id=org.snomed%3Asnomed-sso-service)

![Contributers](https://img.shields.io/github/contributors/IHTSDO/snomed-sso-service)
![Last Commit](https://img.shields.io/github/last-commit/ihtsdo/snomed-sso-service)
![GitHub commit activity the past year](https://img.shields.io/github/commit-activity/m/ihtsdo/snomed-sso-service)
&nbsp;&nbsp;
![Tag](https://img.shields.io/github/v/tag/IHTSDO/snomed-sso-service)
![Java Version](https://img.shields.io/badge/Java_Version-17-green)
[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.md)
&nbsp;&nbsp;
![Lines of code](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/badge/icon?subject=Lines%20Of%20Code&status=${lineOfCode}&color=blue)
![Line Coverage](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/badge/icon?subject=Line%20Coverage&status=${lineCoverage}&color=${colorLineCoverage})
![Instruction Coverage](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/badge/icon?subject=Instruction%20Coverage&status=${instructionCoverage}&color=${colorInstructionCoverage})
![Branch Coverage](https://jenkins.ihtsdotools.org/job/jobs/job/snomed-sso-service/job/develop/badge/icon?subject=Branch%20Coverage&status=${branchCoverage}&color=${colorBranchCoverage})

# IHTSDO Tools Identity Management Service

This module is the IMS API written in Spring Boot. It provides single sign-on authentication and authorisation functionality, backed by Atlasian Crowd. User groups (labeled as roles) are cached to provide a faster lookup.

The Swagger interfaces for the deployments are here:

* http://localhost:8080/swagger-ui/index.html

* http://localhost:8080/health
* http://localhost:8080/api/health - login with "user" and "password".
