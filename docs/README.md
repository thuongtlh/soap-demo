# Architecture Documentation

This directory contains comprehensive architecture documentation for the REST to SOAP Integration Demo application. All diagrams are created using [Mermaid](https://mermaid.js.org/), a markdown-based diagramming tool that renders beautifully on GitHub.

## 📚 Documentation Index

### 1. [System Architecture](system-architecture.md)
High-level overview of the system architecture showing the major components and their interactions.

**Contents:**
- System components overview
- Communication flow between services
- Technology stack
- Layer responsibilities

**Diagrams:**
- System architecture diagram with REST and SOAP services
- Component interaction flow

**When to use:** Start here to understand the overall system design and how the pieces fit together.

---

### 2. [Component Diagram](component-diagram.md)
Detailed component-level view showing all classes, their dependencies, and relationships.

**Contents:**
- REST service components (Controllers, Services, Clients, Mappers, DTOs)
- SOAP service components (Endpoints, Services, Configuration)
- Configuration components
- Generated code components (JAX-WS stubs, JAXB types)
- Dependency relationships

**Diagrams:**
- Comprehensive component diagram with all classes
- Dependency arrows showing relationships

**When to use:** When you need to understand the internal structure of each service and how components depend on each other.

---

### 3. [Create Order Sequence Diagram](create-order-sequence.md)
Step-by-step sequence showing the complete flow when creating an order.

**Contents:**
- Request flow from REST client to SOAP service
- Data transformation at each layer
- Response flow back to client
- Detailed explanation of each step

**Diagrams:**
- Sequence diagram showing all interactions
- Time-ordered message flow

**When to use:** To understand the complete lifecycle of an order creation request.

---

### 4. [Get Order Sequence Diagram](get-order-sequence.md)
Step-by-step sequence showing the flow when retrieving an existing order.

**Contents:**
- Order retrieval flow
- Success and error scenarios
- Exception handling flow
- Data mapping for responses

**Diagrams:**
- Sequence diagram with success and error paths
- Alternative flows for not-found scenario

**When to use:** To understand order retrieval flow and error handling patterns.

---

### 5. [Data Model](data-model.md)
Comprehensive view of all data structures in the system.

**Contents:**
- REST layer DTOs (Data Transfer Objects)
- SOAP layer types (generated from XSD)
- Mapping relationships between DTOs and SOAP types
- Object compositions and relationships
- Validation rules and constraints

**Diagrams:**
- Class diagrams for REST DTOs
- Class diagrams for SOAP types
- Mapping relationship diagrams
- Object composition diagrams

**When to use:** When you need to understand data structures, object relationships, or how data is transformed between layers.

---

### 6. [Deployment Architecture](deployment-architecture.md)
Runtime deployment view showing how services are deployed and communicate.

**Contents:**
- Service deployment details (ports, endpoints)
- Network configuration
- Build and runtime processes
- Production considerations
- Configuration files

**Diagrams:**
- Deployment diagram showing services and ports
- Communication flow diagram
- Network topology
- Build process flow
- Production deployment topology

**When to use:** When deploying, configuring, or troubleshooting the application in different environments.

---

### 7. [Technology Stack and Layers](technology-stack.md)
Comprehensive view of the technology stack, architectural layers, and framework dependencies.

**Contents:**
- Technology stack for REST and SOAP services
- Architectural layers breakdown
- Framework dependencies and versions
- Code generation flow
- Build lifecycle and runtime startup
- Performance characteristics
- Scalability and security considerations

**Diagrams:**
- Technology stack overview
- Architectural layers
- Framework dependencies
- Code generation flow
- Dependency management
- Build lifecycle
- Runtime startup sequence

**When to use:** When evaluating technologies, understanding build processes, or making technology decisions.

---

## 🎯 Quick Navigation Guide

### I want to understand...

| Goal | Start Here |
|------|------------|
| Overall system design | [System Architecture](system-architecture.md) |
| How components are organized | [Component Diagram](component-diagram.md) |
| How order creation works | [Create Order Sequence](create-order-sequence.md) |
| How order retrieval works | [Get Order Sequence](get-order-sequence.md) |
| Data structures and DTOs | [Data Model](data-model.md) |
| How to deploy and run | [Deployment Architecture](deployment-architecture.md) |
| Technology choices and stack | [Technology Stack](technology-stack.md) |

### I'm working on...

| Task | Relevant Documentation |
|------|----------------------|
| Adding a new REST endpoint | Component Diagram, Data Model |
| Adding a new SOAP operation | System Architecture, Deployment Architecture |
| Modifying data structures | Data Model, both Sequence Diagrams |
| Debugging integration issues | Sequence Diagrams, System Architecture |
| Deploying to a new environment | Deployment Architecture |
| Understanding mappings | Data Model, Component Diagram |
| Evaluating technology choices | Technology Stack |
| Understanding the build process | Technology Stack, Deployment Architecture |

---

## 🔧 Technologies Documented

This architecture documentation covers:

- **Spring Boot 3.4.6**: Application framework
- **Spring Web Services**: SOAP service implementation
- **JAX-WS 4.0.2**: SOAP client implementation
- **JAXB**: XML/Java binding
- **MapStruct 1.5.5**: Object mapping
- **SpringDoc OpenAPI**: API documentation
- **Jakarta Validation**: Request validation

---

## 📖 Mermaid Diagrams

All diagrams in this documentation use Mermaid syntax, which is:

- ✅ **Version Controlled**: Stored as text in markdown files
- ✅ **Easy to Update**: Edit text to update diagrams
- ✅ **GitHub Native**: Renders automatically on GitHub
- ✅ **IDE Friendly**: Many IDEs have Mermaid preview plugins
- ✅ **Documentation as Code**: Lives alongside your code

### Viewing Mermaid Diagrams

**On GitHub:** Diagrams render automatically when viewing markdown files.

**In VS Code:** Install the "Markdown Preview Mermaid Support" extension.

**In IntelliJ:** Install the "Mermaid" plugin.

**In Browser:** Use the [Mermaid Live Editor](https://mermaid.live/) to preview and edit diagrams.

---

## 🎨 Diagram Types Used

This documentation uses several Mermaid diagram types:

| Type | Used For | Files |
|------|----------|-------|
| `graph TB` | System architecture, component relationships | system-architecture.md, component-diagram.md, deployment-architecture.md |
| `sequenceDiagram` | Request/response flows, interactions over time | create-order-sequence.md, get-order-sequence.md |
| `classDiagram` | Data models, class structures | data-model.md |

---

## 📝 Document Conventions

### Diagram Colors
- 🔵 **Blue**: REST service components
- 🟣 **Purple**: SOAP service components
- 🟢 **Green**: Data/storage components
- 🟡 **Yellow**: Configuration/schema components
- 🔴 **Pink**: Documentation/metadata components

### Arrow Types
- `-->` Solid arrow: Direct dependency or call
- `-.->` Dashed arrow: Indirect relationship or documentation
- `==>` Thick arrow: Primary data flow

### Naming Conventions
- **ClassName**: Actual class names from code
- **Component Name**: Logical grouping of classes
- **Process/Action**: Business process or action

---

## 🔄 Keeping Documentation Updated

### When to Update

Update the documentation when:

1. **Adding new endpoints**: Update sequence diagrams and component diagram
2. **Modifying data structures**: Update data model diagram
3. **Changing service architecture**: Update system architecture
4. **Adding new components**: Update component diagram
5. **Changing deployment**: Update deployment architecture

### How to Update

1. Edit the markdown file in a text editor
2. Modify the Mermaid syntax within the code blocks
3. Preview the changes (GitHub, IDE plugin, or Mermaid Live Editor)
4. Commit the changes with descriptive commit message

### Mermaid Syntax Resources

- [Official Mermaid Documentation](https://mermaid.js.org/)
- [Mermaid Live Editor](https://mermaid.live/) - Test your diagrams
- [Mermaid Cheat Sheet](https://jojozhuang.github.io/tutorial/mermaid-cheat-sheet/)

---

## 🤝 Contributing to Documentation

### Best Practices

1. **Keep diagrams simple**: Focus on clarity over completeness
2. **Use consistent styling**: Follow the color conventions
3. **Add explanatory text**: Diagrams should be supplemented with text
4. **Test rendering**: Verify diagrams render correctly on GitHub
5. **Link related docs**: Cross-reference related documentation

### Review Checklist

- [ ] Diagram renders correctly on GitHub
- [ ] All components are labeled clearly
- [ ] Colors follow the convention
- [ ] Explanatory text is included
- [ ] Links to other docs are valid
- [ ] Formatting is consistent

---

## 📞 Support

### For Architecture Questions
Review the relevant documentation first. If you need clarification, consider:
- Reviewing the actual source code
- Checking the main README.md for implementation details
- Looking at the XSD schema for SOAP contracts

### For Documentation Issues
If you find errors or omissions in this documentation:
- Create an issue with specific details
- Suggest corrections via pull request
- Update outdated diagrams

---

## 📊 Documentation Statistics

| Document | Diagrams | Lines | Focus Area |
|----------|----------|-------|------------|
| System Architecture | 1 | ~80 | High-level overview |
| Component Diagram | 1 | ~200 | Component relationships |
| Create Order Sequence | 1 | ~150 | Order creation flow |
| Get Order Sequence | 1 | ~150 | Order retrieval flow |
| Data Model | 4 | ~300 | Data structures |
| Deployment Architecture | 4 | ~350 | Deployment & runtime |
| Technology Stack | 7 | ~400 | Technology & layers |

**Total:** 19 diagrams across 7 documents

---

## 🗺️ Architecture Decision Records (ADRs)

While not in separate files, key architectural decisions are documented:

### Why REST to SOAP Integration?
- Demonstrates modernizing legacy SOAP services with REST APIs
- Shows practical integration patterns
- Real-world scenario many enterprises face

### Why WSDL-First Approach?
- Single source of truth (XSD)
- Type-safe client generation
- Contract-driven development

### Why MapStruct?
- Compile-time code generation
- Type-safe mappings
- Zero runtime overhead

### Why JAX-WS over Spring WebServiceTemplate?
- Industry standard
- Better type safety
- Native WSDL-first support

These decisions are detailed in the main README.md "Why These Technologies?" section.

---

Last Updated: December 2024
