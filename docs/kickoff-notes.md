# Kickoff Notes

## What the Assignment Actually Requires

### Users and roles

- Customer enters a name and does not log in
- Barista uses hardcoded credentials
- Manager uses hardcoded credentials
- Each role must only access its own functions

### Product model

- Beverages and pastries
- Coffee and tea examples are explicitly required
- Beverage sizes affect pricing
- Beverage customizations have extra cost
- Pastries have variations but not size/customization complexity

### Inventory model

- Ingredients have stock levels
- Menu items consume ingredients
- Orders deduct inventory
- Out-of-stock items cannot be added to an order
- Managers can restock ingredients

### UI behavior

- Customer screen builds an order and places it
- Barista screen processes orders in FIFO order
- Manager screen manages menu and inventory
- Views should update dynamically as data changes
- Use in-app status labels or custom UI messages, not alert dialogs

### Persistence

- App starts with JSON seed data
- App saves state back to JSON on exit
- Persist product catalog, inventory, user accounts/roles, and orders

## Required Design and Documentation Outputs

- Use case diagram
- Wireframes
- Conceptual classes to software classes explanation
- High-level UML class diagram
- At least three sequence diagrams
- Optional activity diagram for one use case
- Layer/MVC explanation
- OO principles and patterns explanation
- Group process reflection
- Evidence of project management workflow

## Mandatory Design Decisions To Make Early

- Choose Maven or Gradle
- Define package structure for MVC and persistence layers
- Decide how Observer is implemented
- Decide how Factory Method creates beverage and pastry objects
- Define JSON schemas for menu, inventory, users, and orders
- Agree on branch strategy and issue workflow

## Recommended Initial Class Areas

- `User`, `CustomerSession`, `BaristaUser`, `ManagerUser`
- `MenuItem`, `Beverage`, `Pastry`
- `Customization`, `SizeOption`
- `Ingredient`, `InventoryService`
- `Order`, `OrderItem`, `OrderStatus`, `OrderQueue`
- `MenuService`, `OrderService`, `PersistenceService`
- JavaFX views and controllers per role

## Biggest Risk Areas

- Mixing UI code with business logic
- Skipping diagrams until the end
- Weak JSON persistence design
- No central source of truth for order/inventory updates
- Not tracking non-coding tasks in GitHub Projects
