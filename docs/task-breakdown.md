# Initial Task Breakdown

## Sprint 0

- Create GitHub repository
- Add team members and instructor collaborator
- Create GitHub Project board
- Create issue templates or initial backlog
- Choose Maven or Gradle
- Agree on branch naming and pull request workflow

## Sprint 1: Analysis and Design

- Draft use case diagram
- Draft wireframes for customer, barista, and manager screens
- Identify conceptual classes
- Convert conceptual classes into software classes
- Draft high-level UML class diagram
- Decide MVC layer boundaries
- Decide which patterns besides Observer and Factory Method will be used

## Sprint 2: Core Domain and Persistence

- Implement menu item model
- Implement beverage sizes and customizations
- Implement pastry variations
- Implement inventory model and ingredient consumption
- Implement order model and statuses
- Load seed data from JSON
- Save state to JSON

## Sprint 3: Customer and Barista Flows

- Build customer ordering UI
- Build barista queue UI
- Implement order placement
- Implement FIFO queue behavior
- Implement status transitions
- Implement dynamic updates between screens

## Sprint 4: Manager and Integration

- Build manager menu management UI
- Build manager inventory restock UI
- Support adding, editing, and removing menu items
- Support inventory updates and validation
- Integrate all persistence flows
- Improve in-app error messaging

## Sprint 5: Polish and Submission

- Package runnable JAR
- Regression test all flows
- Finish sequence diagrams
- Write architecture and OO-pattern explanations
- Write group reflection
- Capture GitHub Project evidence
- Final document proofreading

## Suggested Owners

- Lead: GitHub Projects, weekly status, integration
- Member 1: Customer UI + order placement
- Member 2: Barista workflow + status handling
- Member 3: Manager UI + inventory/menu management
- Member 4: Persistence + documentation diagrams

## First GitHub Issues To Create

- Set up repository structure and build tool
- Define domain model and package layout
- Create seed JSON files for menu and inventory
- Design customer ordering wireframe
- Design barista dashboard wireframe
- Design manager dashboard wireframe
- Implement menu item factory
- Implement observer-based update mechanism
- Implement order queue and status transitions
- Implement JSON persistence layer
- Prepare UML use case diagram
- Prepare UML class diagram
- Prepare sequence diagrams
- Draft group process reflection outline
