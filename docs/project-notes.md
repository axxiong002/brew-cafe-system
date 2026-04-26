# Project Notes

This file is the shared repo-level notes log for setup changes, progress updates, and important implementation milestones.

## March 19 2026

- Created the initial project repo structure and planning docs
- Added starter files for kickoff, task breakdown, and repo setup
- Set up a Maven-based JavaFX project structure
- Added a JavaFX application entry point
- Added starter package layout for `controller`, `model`, `service`, `persistence`, and `view`
- Added placeholder role screens for customer, barista, and manager
- Added starter model classes for menu items, ingredients, and orders
- Added starter service classes including early Factory Method and Observer pattern structure
- Added sample JSON files for menu, inventory, and users
- Added Maven wrapper files so the team can build more easily
- Verified the project compiles successfully with Maven
- Built out the starter navigation for customer, barista, and manager flows
- Added the first observer-pattern structure for future live updates
- Added brief comments to key scaffold files so the code is easier to follow
- Added the team Git branch and pull request workflow to `docs/task-breakdown.md`

## March 24 2026

- Clarified the working split for the team:
  - Andrew: code lead, architecture, integration, and manager/inventory direction
  - Chee: customer workflow and customer UI
  - Ameer: barista workflow, queue handling, and order status updates
  - Riss: UML diagrams, wireframes, and documentation support
- Broke out the first wireframes to work from:
  - main landing / role selection
  - customer ordering
  - barista fulfillment
  - manager dashboard
  - barista login
  - manager login
- Next step is assigning GitHub issues and adding more specific implementation issues where needed

## April 7 2026

- Started a new branch for Andrew's manager-side work: `andre/manager-architecture`
- Added the shared user/auth setup for employee logins:
- `User`
- `UserRole`
- `AuthService`
- Added `CafeApplicationState` so the app shares the same service objects across the screens
- Expanded JSON loading so users, menu items, and inventory load into the app at startup
- Added `IngredientUsage` so menu items can be tied to inventory usage
- Added `MenuService` for menu list and manager-side add/edit/remove work
- Added `InventoryService` for stock checks, restocking, and ingredient deduction support
- Confirmed the project still compiles after these updates
- Replaced the manager placeholder with a real manager login screen
- Added a first manager dashboard with:
- menu list
- selected item details
- add/remove item controls
- inventory table
- restock controls
- Confirmed the project still compiles after the manager UI was added
- Next step is expanding the manager dashboard and save/load behavior

## April 14 2026

- UML Use Case Diagram
- Class Diagrams (STARTED WILL HAVE QUESTIONS)

## April 21 2026

- Merged the latest `main` changes into `andre/manager-architecture`, including the use case diagram files.
- Added manager menu item editing on the manager dashboard:
  - selecting a menu item now fills editable fields
  - managers can update the selected item's name and base price
  - pastry variations can also be edited
  - successful edits save back to `data/menu.json`
- Confirmed the project still compiles with the Maven wrapper after the manager edit changes.
- Expanded the local manager/data foundation further:
  - beverage size price adjustments can be edited from the manager dashboard
  - beverage customization costs can be edited from the manager dashboard
  - menu item ingredient usage can be edited with inventory ingredient IDs
  - seed menu JSON now preserves size prices, customization prices, and ingredient usage
  - inventory seed data now includes the core ingredients used by the menu
  - pending and fulfilled order queues now load/save through `data/orders.json`
- Reviewed teammate branch diffs before integration:
  - `customer-flow` has useful customer service/model work, but some Java/FXML files are currently outside the Maven package structure
  - `ameer/barista-flow` appears behind the current shared scaffold and should be updated before integration

## April 22 2026

- Started `andre/order-flow-foundation` from the updated `main` branch.
- Added shared order-flow support for customer and barista screens:
  - `OrderItem` now stores selected beverage size and customizations
  - `OrderPricingService` centralizes unit, line, and order total calculations
  - `CustomerOrderService` manages a customer's draft order before submitting it
  - `InventoryService` can validate and consume inventory for a whole order
  - `OrderService` exposes pending lookup and next-order FIFO helper methods
  - order JSON persistence now keeps selected size/customization information
- Confirmed the project compiles with `.\mvnw.cmd clean compile`.
