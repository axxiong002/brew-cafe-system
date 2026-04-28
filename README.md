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

## Current Project Status

- Maven-based JavaFX project is set up and builds with the Maven wrapper.
- Package layout is split across `controller`, `model`, `service`, `persistence`, and `view`.
- Customer, barista, and manager flows are merged into `main`.
- Customer flow supports entering a name, selecting menu items, choosing beverage sizes/customizations, building an order, and placing it.
- Barista flow supports login, pending order review, status updates, and completing orders into fulfilled history.
- Manager flow supports login, menu add/edit/remove, beverage size/customization edits, ingredient usage edits, and inventory updates.
- Shared services handle authentication, menu state, inventory checks, pricing, order queues, and application state.
- Menu, inventory, users, pending orders, and fulfilled orders load from JSON.
- Menu, inventory, and order changes save back to JSON.
- Observer and Factory Method pattern structures are present.
- Runnable jar packaging is configured for final submission.

## Build And Run

Use the Maven wrapper from the project root:

```powershell
.\mvnw.cmd compile
.\mvnw.cmd javafx:run
```

If Maven is installed directly on your machine, you can also use:

```powershell
mvn compile
mvn javafx:run
```

To build the runnable submission jar:

```powershell
.\mvnw.cmd clean package
java -jar target\brew-cafe-system-0.1.0-runnable.jar
```

## Team Structure

- Project lead / GitHub coordinator
- UI + customer workflow owner
- Barista/order workflow owner
- Manager/inventory/persistence owner
- Design artifacts + documentation owner

## Immediate Next Steps

1. Pull the latest `main` before doing more work.
2. Finish manual testing across customer, barista, and manager workflows.
3. Finish the UML class diagram, sequence diagrams, and remaining wireframes.
4. Finish the design PDF and group reflection PDF.
5. Capture GitHub Issues/Project screenshots for the final submission.
6. Build and verify the runnable jar before submitting.

## Project Notes

See `docs/project-notes.md` for a lightweight shared log of repo-level progress and setup changes.

## Instructor Collaboration Requirement

I will add `benjamin.cassidy@metrostate.edu` as a collaborator to both the repository and the GitHub Project.
