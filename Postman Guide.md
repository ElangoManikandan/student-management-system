
DAY -5

Test all 5 endpoints in this order. Set base URL to http://localhost:8080.

| Method | URL                  | Expected status |
| ------ | -------------------- | --------------- |
| GET    | `/api/students`      | 200 OK          |
| GET    | `/api/students/{id}` | 200 / 404       |
| POST   | `/api/students`      | 201 Created     |
| PUT    | `/api/students/{id}` | 200 / 404       |
| DELETE | `/api/students/{id}` | 204 No Content  |

Step-by-step test sequence

1

POST `http://localhost:8080/api/students` Create a student first

{
  "name": "Alice",
  "age": 20,
  "email": "alice@sms.com",
  "department": "Computer Science"
}

✓ Status: 201 Created — response body has the student with id: 1

2

POST `http://localhost:8080/api/students` Add a second student

{
  "name": "Bob",
  "age": 22,
  "email": "bob@sms.com",
  "department": "Mathematics"
}

✓ Status: 201 Created — id: 2

3

GET `http://localhost:8080/api/students` Get all students

✓ Status: 200 OK — JSON array with both students

4

GET `http://localhost:8080/api/students/1` Get student by ID

✓ Status: 200 OK — Alice's details

5

GET `http://localhost:8080/api/students/999` Test 404 — non-existent ID

✓ Status: 404 Not Found — empty body

6

PUT `http://localhost:8080/api/students/1` Update a student (full replace)

{
  "name": "Alice Smith",
  "age": 21,
  "email": "alice.smith@sms.com",
  "department": "Data Science"
}

✓ Status: 200 OK — updated student returned

7

DELETE `http://localhost:8080/api/students/2` Delete a student

✓ Status: 204 No Content — empty body (Bob is deleted)

8

GET `http://localhost:8080/api/students` Verify deletion

✓ Status: 200 OK — only Alice remains in the array


After all tests pass → File → Save As → name it **Student API - Day5**. You will reuse this collection on Day 6.


DAY -6

Postman Tests T4 + T8
Test these scenarios in Postman to verify validation, error handling, and pagination all work correctly. Base URL: http://localhost:8080
Request flow: Postman → Controller + @Valid → Service + ModelMapper → Repository → MySQL
1. Valid POST — should return 201
   POST
   /api/students

{
"name": "Alice",
"age": 20,
"email": "alice@sms.com",
"department": "Computer Science"
}

✓ 201 Created — StudentResponseDTO with id assigned
2. Invalid POST — blank name → 400
   POST
   /api/students

{
"name": "",
"age": 20,
"email": "alice@sms.com",
"department": "CS"
}

✓ 400 Bad Request — errors: ["Name must not be blank"]
3. Invalid POST — bad email → 400
   POST
   /api/students

{
"name": "Bob",
"age": 22,
"email": "not-an-email",
"department": "Math"
}

✓ 400 Bad Request — errors: ["Email must be a valid email address"]
4. GET non-existent ID → 404
   GET
   /api/students/999
   ✓ 404 Not Found — { status: 404, message: "Student not found with id: 999" }
5. Pagination — first page of 3
   GET
   /api/students/paged?page=0&size=3
   ✓ 200 OK — Page object with content[], totalElements, totalPages, number
6. Pagination — second page
   GET
   /api/students/paged?page=1&size=3
   ✓ 200 OK — next 3 students (or fewer if less than 6 total)
   The 400 error response body will look like:
   { "timestamp": "...", "status": 400, "message": "Validation failed", "errors": ["Email must be a valid email address"] }