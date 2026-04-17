# Socius Platform Alignment

## Product Positioning
Socius is a moderated community platform built around calm, text-first discussion spaces. It focuses on ethical participation, digital wellbeing, accessibility, civic care, and social innovation.

## Core Identity
- Brand: `Socius`
- Purpose: help people create and participate in healthier online communities
- Topic focus: constructive communities with clear moderation and safety workflows
- UI direction: editorial, premium, calm, and inspired by the `stitch` references without copying their exact layout or styling
- Technology target: Java, Java EE, JSP, CSS, MySQL, MVC, and Tomcat 10 Jakarta namespaces

## Platform Capabilities
- Database design with dynamic users, communities, posts, reports, bans, and bulletins
- Public pages for discovery, individual communities, individual posts, purpose, and contact
- Member portal for profile editing, joined communities, post creation, and community creation
- Moderator portal for approval queues, reports, bans, bulletins, and penalty decisions
- Admin portal for users, communities, moderator assignments, and report oversight
- Authentication and authorization through role-aware controllers and filters
- MVC structure with `model`, `dao`, `service`, `controller`, `filter`, and `util` packages
- JSP and CSS frontend without external UI frameworks

## Product Narrative
Socius supports ethical online participation by making community activity intentional instead of impulsive. Members can create and join communities, submit posts, and take part in text-based conversations. Moderators protect each space by reviewing posts, handling reports, issuing guidance, and applying penalties when needed. Administrators keep the whole platform organised by managing users, communities, reports, and moderation authority.

## Design Direction
- No photo-led feed or decorative image dependency
- Clean modern layouts with generous spacing
- Text-first cards, tables, forms, and readable content hierarchy
- Calm editorial styling with strong typography and warm neutral surfaces
- Clear separation between public browsing, member activity, moderator review, and admin governance

## Recommended Next Build Order
1. Keep DAO queries aligned with the `socius_db` schema
2. Test registration, login, logout, and role redirects
3. Test member community creation and post submission
4. Test moderator approval, rejection, reports, bans, and bulletins
5. Test admin management screens with multiple user roles
