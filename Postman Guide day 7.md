Run these in order. Make sure you have at least one student already in the DB from Day 6 (POST one if needed). Base URL: `http://localhost:8080`

Add these 2 endpoints to **CourseController** for the custom query tests (T7 + T8):  
  
`@GetMapping("/students-by-course")   public ResponseEntity<List<String>> getStudentsByCourse(@RequestParam String title) {     return ResponseEntity.ok(courseService.findStudentsByCourseTitle(title));   }`  
  
`@GetMapping("/students-with-many-courses")   public ResponseEntity<List<String>> getStudentsWithManyCourses(@RequestParam int count) {     return ResponseEntity.ok(courseService.findStudentsWithMoreThanNCourses(count));   }`

1. Create first course

POST `/api/courses`

{
  "title": "Data Structures",
  "credits": 4,
  "duration": "1 semester"
}

✓ 201 Created — course with id: 1

2. Create second course

POST `/api/courses`

{
  "title": "Machine Learning",
  "credits": 3,
  "duration": "6 months"
}

✓ 201 Created — course with id: 2

3. Create a third course

POST `/api/courses`

{
  "title": "Spring Boot",
  "credits": 3,
  "duration": "3 months"
}

✓ 201 Created — course with id: 3

4. Get all courses

GET `/api/courses`

✓ 200 OK — array of 3 courses

5. Enroll student 1 in course 1

POST `/api/students/1/enroll/1`

✓ 200 OK — "Alice enrolled in Data Structures"

6. Enroll student 1 in course 2 (multiple enrollments)

POST `/api/students/1/enroll/2`

✓ 200 OK — "Alice enrolled in Machine Learning"

7. Enroll student 1 in course 3

POST `/api/students/1/enroll/3`

✓ 200 OK — Alice now in 3 courses (for T8 query)

8. Custom query T7 — students by course title

GET `/api/courses/students-by-course?title=Data Structures`

✓ 200 OK — ["Alice"] (students enrolled in Data Structures)

9. Custom query T8 — students with more than 2 courses

GET `/api/courses/students-with-many-courses?count=2`

✓ 200 OK — ["Alice (3 courses)"]

10. Verify join table in Workbench

GET `(MySQL Workbench)`

✓ Run: SELECT * FROM student_course; — shows rows for studentId + courseId pairs