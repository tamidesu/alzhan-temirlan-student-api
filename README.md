# Alzhan Temirlan Student API

Owner: Temirlan Alzhan
Group: IT2-2307
Student ID: 38372

Project description: Spring Boot and PostgreSQL student records API maintained by Temirlan Alzhan.

## CRUD feature

Implemented by Temirlan Alzhan on feature/alzhan-temirlan-crud.
The API creates, lists, retrieves, updates and deletes students.
Missing IDs return 404 and invalid input returns 400.
The application uses PostgreSQL through Spring Data JPA.

## Continuous integration

The Alzhan_Temirlan_Student_API job checks main for changes through SCM polling.
Its stages compile, test, build a Docker image and verify its source revision.
Deployment is performed separately through Docker Compose.
