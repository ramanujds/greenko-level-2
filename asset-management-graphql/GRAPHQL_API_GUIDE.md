# Asset Management GraphQL API Documentation

## API Endpoint
- **URL**: `http://localhost:8080/graphql`
- **Method**: POST
- **Content-Type**: application/json

---

## QUERY OPERATIONS

### 1. Get All Assets
Fetches all assets from the database.

**Query:**
```graphql
{
  getAllAssets {
    assetId
    assetName
    type
    installedDate
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAllAssets": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "installedDate": "2022-01-01",
        "location": "Hyderabad",
        "status": "Active"
      },
      {
        "assetId": "AA22-SOLAR-1",
        "assetName": "Solar Panel 101",
        "type": "Solar Panel",
        "installedDate": "2022-03-01",
        "location": "Hyderabad",
        "status": "Active"
      }
    ]
  }
}
```

---

### 2. Get Asset by ID
Fetches a specific asset by its ID.

**Query:**
```graphql
{
  getAssetById(assetId: "AA22-TURBINE-1") {
    assetId
    assetName
    type
    installedDate
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAssetById": {
      "assetId": "AA22-TURBINE-1",
      "assetName": "Wind Turbine 101",
      "type": "Turbine",
      "installedDate": "2022-01-01",
      "location": "Hyderabad",
      "status": "Active"
    }
  }
}
```

---

### 3. Get Assets by Exact Name
Fetches assets by their exact name.

**Query:**
```graphql
{
  getAssetsByName(assetName: "Wind Turbine 101") {
    assetId
    assetName
    type
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAssetsByName": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "status": "Active"
      }
    ]
  }
}
```

---

### 4. Search Assets by Name
Performs a case-insensitive search for assets containing the search term.

**Query:**
```graphql
{
  searchAssets(searchTerm: "Turbine") {
    assetId
    assetName
    type
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "searchAssets": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "status": "Active"
      },
      {
        "assetId": "AA22-TURBINE-2",
        "assetName": "Wind Turbine 102",
        "type": "Turbine",
        "status": "Active"
      },
      {
        "assetId": "AA22-TURBINE-3",
        "assetName": "Wind Turbine 103",
        "type": "Turbine",
        "status": "Inactive"
      }
    ]
  }
}
```

---

### 5. Get Assets by Type
Fetches all assets of a specific type.

**Query:**
```graphql
{
  getAssetsByType(type: "Turbine") {
    assetId
    assetName
    type
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAssetsByType": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "location": "Hyderabad",
        "status": "Active"
      },
      {
        "assetId": "AA22-TURBINE-2",
        "assetName": "Wind Turbine 102",
        "type": "Turbine",
        "location": "Hyderabad",
        "status": "Active"
      }
    ]
  }
}
```

---

### 6. Get Assets by Status
Fetches all assets with a specific status.

**Query:**
```graphql
{
  getAssetsByStatus(status: "Active") {
    assetId
    assetName
    type
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAssetsByStatus": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "status": "Active"
      },
      {
        "assetId": "AA22-SOLAR-1",
        "assetName": "Solar Panel 101",
        "type": "Solar Panel",
        "status": "Active"
      }
    ]
  }
}
```

---

### 7. Get Assets by Location
Fetches all assets at a specific location.

**Query:**
```graphql
{
  getAssetsByLocation(location: "Hyderabad") {
    assetId
    assetName
    type
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "getAssetsByLocation": [
      {
        "assetId": "AA22-TURBINE-1",
        "assetName": "Wind Turbine 101",
        "type": "Turbine",
        "location": "Hyderabad",
        "status": "Active"
      },
      {
        "assetId": "AA22-SOLAR-1",
        "assetName": "Solar Panel 101",
        "type": "Solar Panel",
        "location": "Hyderabad",
        "status": "Active"
      }
    ]
  }
}
```

---

## MUTATION OPERATIONS

### 1. Create Asset
Creates a new asset in the system.

**Mutation:**
```graphql
mutation {
  createAsset(
    assetId: "AA22-TURBINE-4"
    assetName: "Wind Turbine 104"
    type: "Turbine"
    installedDate: "2023-01-15"
    location: "Chennai"
    status: "Active"
  ) {
    assetId
    assetName
    type
    installedDate
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "createAsset": {
      "assetId": "AA22-TURBINE-4",
      "assetName": "Wind Turbine 104",
      "type": "Turbine",
      "installedDate": "2023-01-15",
      "location": "Chennai",
      "status": "Active"
    }
  }
}
```

---

### 2. Update Asset
Updates an existing asset with new values.

**Mutation:**
```graphql
mutation {
  updateAsset(
    assetId: "AA22-TURBINE-1"
    assetName: "Wind Turbine 101 - Updated"
    type: "Turbine"
    installedDate: "2022-01-01"
    location: "Hyderabad"
    status: "Maintenance"
  ) {
    assetId
    assetName
    type
    installedDate
    location
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "updateAsset": {
      "assetId": "AA22-TURBINE-1",
      "assetName": "Wind Turbine 101 - Updated",
      "type": "Turbine",
      "installedDate": "2022-01-01",
      "location": "Hyderabad",
      "status": "Maintenance"
    }
  }
}
```

---

### 3. Delete Asset
Deletes an asset from the system.

**Mutation:**
```graphql
mutation {
  deleteAsset(assetId: "AA22-TURBINE-4")
}
```

**Response:**
```json
{
  "data": {
    "deleteAsset": true
  }
}
```

---

## Implementation Details

### Files Created

1. **AssetRepository.java** - JPA Repository for database operations
   - Supports queries by name, type, status, and location

2. **AssetService.java** - Business logic layer
   - CRUD operations
   - Search and filter functionality

3. **AssetGraphQLController.java** - GraphQL endpoint handler
   - Query resolvers for data retrieval
   - Mutation resolvers for data modification

4. **schema.graphqls** - GraphQL schema definition
   - Defines all queries, mutations, and types

### Technologies Used
- Spring Boot 4.0.3
- Spring Data JPA
- Spring GraphQL
- Lombok
- H2 Database

### Asset Fields
- `assetId` (String): Unique identifier
- `assetName` (String): Name of the asset
- `type` (String): Type of asset (Turbine, Solar Panel, Battery Storage, etc.)
- `installedDate` (String): Installation date (YYYY-MM-DD format)
- `location` (String): Physical location of the asset
- `status` (String): Current status (Active, Inactive, Maintenance, etc.)

---

## Testing with cURL

### Example Query Request
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ getAllAssets { assetId assetName type status } }"}'
```

### Example Mutation Request
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { createAsset(assetId: \"AA22-TEST-1\", assetName: \"Test Asset\", type: \"Test\", installedDate: \"2023-01-01\", location: \"Test\", status: \"Active\") { assetId assetName } }"}'
```

---

## Error Handling

If an asset is not found, the API will return `null` for queries and `null` for updates. For mutations that require creation or updates, ensure all required fields are provided.

---

