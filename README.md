# Brew Cafe System

This repository is for our "Cafe Ordering and Barista Fulfillment System" group project.

## Project Goal

We are building a Java 21+ JavaFX application for a cafe with three roles:

- Customer: browse menu, customize items, place orders
- Barista: view pending orders, update order status, complete orders
- Manager: manage menu, manage inventory, monitor fulfilled orders

## Required Technical Constraints

- JavaFX only, no Swing
- MVC architecture
- Observer pattern required
- Factory Method pattern required
- JSON load/save on startup and shutdown
- Java 21 LTS or newer
- Runnable JAR required

## Core Feature Areas

- Role-based access with hardcoded credentials for barista and manager
- Menu catalog with beverages and pastries
- Beverage sizes and add-on customizations
- Inventory tracking with ingredient deduction
- Seed data loaded from JSON files
- Customer order building and placement
- FIFO barista fulfillment queue
- Real-time UI updates for order and inventory changes
- Persistence for catalog, inventory, users, and orders

## Team Structure

- Project lead / GitHub coordinator
- UI + customer workflow owner
- Barista/order workflow owner
- Manager/inventory/persistence owner
- Design artifacts + documentation owner

## Immediate Next Steps

1. Create a GitHub repository from this folder.
2. Invite teammates and the instructor collaborator.
3. Create a GitHub Project board using Kanban.
4. Create issues for design, coding, testing, diagrams, and reflection work.
5. Decide whether we will use Maven or Gradle.
6. Draft wireframes and a high-level class model before coding heavily.

## Instructor Collaboration Requirement

I will add `benjamin.cassidy@metrostate.edu` as a collaborator to both the repository and the GitHub Project.
