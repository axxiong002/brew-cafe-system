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

- Locked in the current working split a little more:
- Andrew: code lead, integration, manager/inventory direction
- Chee: customer side
- Ameer: barista side
- Riss: UML, wireframes, and docs support
- Broke out the first wireframes we want to work from:
- main landing / role selection
- customer ordering
- barista fulfillment
- manager dashboard
- barista login
- manager login
- Next step was assigning issues and making the tasks more specific

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
