[<img alt="Snomed CT" style="height:125px;" src="https://static.wixstatic.com/media/49d95c_f232b9c10b72410b802fbbd35b357698~mv2.png"/>](https://www.snomed.org/)

[![Build Status](https://jenkins.ihtsdotools.org/view/all/job/jobs/job/identity-management-service/job/master/badge/icon)](https://jenkins.ihtsdotools.org/view/all/job/jobs/job/identity-management-service/job/master/)
&nbsp;&nbsp;
[Dev [![Build Status](https://jenkins.ihtsdotools.org/view/all/job/jobs/job/identity-management-service/job/develop/badge/icon)](https://jenkins.ihtsdotools.org/view/all/job/jobs/job/identity-management-service/job/develop/)
[![Quality Gate Status](https://sonarqube.ihtsdotools.org/api/project_badges/measure?project=org.ihtsdo.otf.common%3Aotf-common-parent&metric=alert_status&token=sqb_7dfed5b311d026dedfc39a2a494f207139d2f3bb)](https://sonarqube.ihtsdotools.org/dashboard?id=org.ihtsdo.otf.common%3Aotf-common-parent)]
&nbsp;&nbsp;
![Contributers](https://img.shields.io/github/contributors/IHTSDO/identity-management-service)
![Last Commit](https://img.shields.io/github/last-commit/ihtsdo/identity-management-service)
![GitHub commit activity the past year](https://img.shields.io/github/commit-activity/m/ihtsdo/identity-management-service)
&nbsp;&nbsp;
![Tag](https://img.shields.io/github/v/tag/IHTSDO/identity-management-service)
![Java Version](https://img.shields.io/badge/Java_Version-17-green)
[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.md)
&nbsp;&nbsp;
![LOC](https://jenkins.ihtsdotools.org/buildStatus/icon?job=identity-management-service&status=lineOfCode&subject=line%20of%20code&color=blue)
![Coverage](https://jenkins.ihtsdotools.org/buildStatus/icon?job=identity-management-service&subject=Coverage)
![Tests](https://jenkins.ihtsdotools.org/buildStatus/icon?job=identity-management-service&status=numberOfTest&subject=Tests&color=brightgreen)
![Tests](https://jenkins.ihtsdotools.org/buildStatus/icon?job=identity-management-service&subject=Coverage&status=instructionCoverage)

# IHTSDO Tools Identity Management Service

This module is the IMS API written in Spring Boot. It provides single sign-on authentication and authorisation functionality, backed by Atlasian Crowd. User groups (labeled as roles) are cached to provide a faster lookup.

The Swagger interfaces for the deployments are here:

* http://localhost:8080/swagger-ui/index.html

* http://localhost:8080/health
* http://localhost:8080/api/health - login with "user" and "password".

* https://ims.ihtsdotools.org/swagger-ui.html
