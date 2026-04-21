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

- Maven-based JavaFX starter project is set up
- The project compiles successfully with Maven
- Maven wrapper files are included for easier team setup
- Starter package layout exists for `controller`, `model`, `service`, `persistence`, and `view`
- Role placeholder screens exist for customer, barista, and manager flows
- Sample JSON seed files exist under `data/`
- Early Factory Method and Observer pattern starter code has been added
- Shared application state now loads seeded users, menu items, and inventory on startup
- Shared `AuthService`, `MenuService`, and `InventoryService` foundations are now in place for later role-specific UI work
- Manager-side architecture now owns the central menu and inventory service layer the other role flows will use
- The manager button now opens a real manager login screen and first-pass dashboard instead of only a placeholder
- The manager dashboard supports adding, editing, and removing menu items, plus viewing and restocking inventory
- Manager menu and inventory changes save back to JSON

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

## Team Structure

- Project lead / GitHub coordinator
- UI + customer workflow owner
- Barista/order workflow owner
- Manager/inventory/persistence owner
- Design artifacts + documentation owner

## Immediate Next Steps

1. Invite teammates and the instructor collaborator.
2. Finish assigning major ownership areas across the team.
3. Create issues for design, coding, testing, diagrams, and reflection work.
4. Expand manager editing with richer beverage size/customization and ingredient-usage controls.
5. Let customer and barista role screens integrate against the shared service layer as those owners build their flows.

## Project Notes

See `docs/project-notes.md` for a lightweight shared log of repo-level progress and setup changes.

## Instructor Collaboration Requirement

I will add `benjamin.cassidy@metrostate.edu` as a collaborator to both the repository and the GitHub Project.
