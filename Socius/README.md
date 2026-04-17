# Socius

Socius is a Java EE web application for moderated, text-first communities. It is designed around clear member, moderator, and administrator workflows, with an editorial visual direction inspired by the `stitch` reference folder without copying that design system directly.

This scaffold is set up for Tomcat 10 style Jakarta namespaces (`jakarta.servlet.*`), not the older `javax.servlet.*` API.
It now also includes a Maven `pom.xml` so IDEs can resolve Servlet/JSP dependencies without relying only on manually attached server libraries.

## Stack
- Java
- Java EE / Servlets / JSP
- CSS
- MySQL
- MVC structure with `model`, `dao`, `service`, `controller`, `filter`, and `util`

## What Is Included
- Original Socius branding and editorial UI
- JSP screens for public, member, moderator, and admin areas
- MySQL schema with realistic seed data
- Utility classes for password hashing, validation, and slug generation
- Controller and filter scaffold
- Platform alignment notes in [`docs/platform-alignment.md`](./docs/platform-alignment.md)

## Current Capabilities
- Database-backed registration and login
- Role-aware dashboards for members, moderators, and administrators
- Dynamic community creation, joining, listing, and discovery
- Dynamic post creation, public post viewing, and moderation review flow
- Admin management screens for communities, users, moderators, and reports

## Setup
1. Open the project as a Maven web project if your IDE supports it.
2. Add Tomcat 10 as the target runtime.
3. If your IDE is not using Maven, manually attach the Tomcat 10 Jakarta Servlet/JSP libraries.
4. Run [`sql/socius_schema_reset.sql`](./sql/socius_schema_reset.sql) in MySQL to rebuild the database.
5. Update database credentials in [`src/dao/DBConnection.java`](./src/dao/DBConnection.java).
6. Deploy to Tomcat 10 and open Socius in your browser.

## Canonical UI Source
- The live JSP, CSS, JS, and public assets are sourced from [`../src/main/webapp`](../src/main/webapp).
- The old `WebContent` tree is no longer used and should not be imported or deployed.

## Notes
- The database name used by the reset schema is `socius_db`.
- Penalty tracking is handled through warnings, `penalty_points`, and ban workflows.
