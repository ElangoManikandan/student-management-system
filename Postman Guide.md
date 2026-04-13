

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