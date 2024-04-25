# Inventory Back-end
This API provides functionalities to manage items in an inventory system. It allows you to retrieve details of existing items, search for items based on criteria, create new items, and delete them.

## Base URL:
`http://localhost:8080/items`

## Authentication:
This API does not require authentication.

## Data Format:
Requests and responses use JSON format for data exchange.

## Error Handling:
API errors follow standard HTTP status codes. Refer to the specific endpoint descriptions for expected status codes and error messages.


<h2><span style="color:green;">GET</span> Get a list of items </h2>
```
curl --location 'http://localhost:8080/items?state={state}'
```
Retrieves a list of items based on the specified state provided in the query parameters. If no state is provided, it returns the list of all items.
### Query Parameters (optional):
- `state`: Filter items by state.

### Response:
- Status Codes:
    - 200 OK: One or more items were found matching the criteria.
    - 404 Not Found: No items match the specified criteria.


### Example Response (200 OK):
```json
[
    {
        "id": 11,
        "name": "Mouse",
        "description": "Ergonomic mouse for comfortable computing",
        "location": {
            "state": "Yucatan",
            "address": "C. 35 SN, Centro, 87000 Merida, Yuc.",
            "phoneNumber": 9999303100,
            "id": 4
        }
    },
    {
        "id": 12,
        "name": "Keyboard",
        "description": "Mechanical keyboard for precise typing",
        "location": {
            "state": "Yucatan",
            "address": "C. 35 SN, Centro, 87000 Merida, Yuc.",
            "phoneNumber": 9999303100,
            "id": 4
        }
    }
]
```

<h2><span style="color:green;">GET</span> Get an item by Id </h2>
```
curl --location 'http://localhost:8080/items/{itemId}'
```
Retrieves details of a specific item by its unique identifier.
### Path Parameters:
- `itemId`: The ID of the item to retrieve (required).

### Response:
- Status Codes:
  - 200 OK: The item was found and its details are returned.
  - 404 Not Found: The item with the specified ID does not exist.

### Example Response (200 OK):
```json
{
    "id": 38,
    "name": "Router",
    "description": "Reliable Wi-Fi coverage for your home or office",
    "location": {
        "state": "Yucatan",
        "address": "C. 35 SN, Centro, 87000 Merida, Yuc.",
        "phoneNumber": 9999303100,
        "id": 4
    }
}
```


<h2><span style="color:goldenrod;">POST</span> Create a new item </h2>
```
curl --location 'http://localhost:8080/items' \
--header 'Content-Type: application/json' \
--data '{
    "id": 50,
    "name": "Speaker",
    "description": "Fill your space with rich sound with this wireless speaker",
    "location_id": 2
}'
```
Creates a new item in the inventory.
### Request Body:
JSON object representing the new item (required).
- `id`: The ID of the item to create (required).
- `name`: The name of the item to create (required).
- `description`: The description of the item to create (optional).
- `location_id`: The reference to the location where the item is located (required).


### Response:
- Status Codes:
  - 201 Created: The item was successfully created and its details are returned.
  - 400 Bad Request: The request body does not contain the required data or the data is invalid.
  - 409 Conflict: An item with the same ID already exists.

### Example Response (201 Created):
```json
{
    "id": 50,
    "name": "Speaker",
    "description": "Fill your space with rich sound with this wireless speaker",
    "location": {
        "state": "Queretaro",
        "address": "Blvd. Bernardo Quintana Arrioja Sn, Villas del Parque, 86140 Santiago de Queretaro, Qro.",
        "phoneNumber": 4422194149,
        "id": 2
    }
}
```
<h2><span style="color:indianred;">DELETE</span> Delete an item </h2>
```
curl --location --request DELETE 'http://localhost:8080/items/{itemId}'
```

Deletes an item from the inventory.
### Path Parameters:
- `itemId`: The ID of the item to delete (required).

### Response:
- Status Codes: 
  - 204 No Content: The item was successfully deleted.
  - 404 Not Found: The item with the specified ID does not exist.



