NVD CVE Tracker (Spring Boot Project)

A Vulnerability Intelligence Web Application built using Spring Boot that fetches real-time CVE (Common Vulnerabilities and Exposures) data from the National Vulnerability Database (NVD) and stores it in a local database.

The application allows users to:
- View paginated CVE list
- See CVSS severity scores
- Click individual CVE IDs to view full details
- Sync latest CVEs from NVD API
- Avoid duplicate CVE entries
  
Project Overview
This project integrates with the **National Vulnerability Database (NVD)** REST API (v2.0) to fetch vulnerability data and persist it into a relational database using Spring Data JPA.
It demonstrates:
- REST API integration
- JSON parsing
- Database persistence
- MVC architecture
- Thymeleaf templating
- Pagination
- Clean repository usage
- Duplicate prevention logic

Tech Stack
- Java 17+
- Spring Boot
- Spring Data JPA
- Thymeleaf
- MySQL (or compatible database)
- Maven
- org.json (JSON parsing)

External API Used
Data is fetched from:
NVD REST API 2.0  
https://services.nvd.nist.gov/rest/json/cves/2.0
Maintained by:
- National Vulnerability Database (NVD)
- National Institute of Standards and Technology (NIST)

Setup Instructions
1.Clone the Repository
git clone https://github.com/your-username/nvd-cve-tracker.git
cd nvd-cve-tracker

2.Configure Database

Create a database:
Example (MySQL):
CREATE DATABASE nvd_db;

3.Update application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/nvd_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
app.sync.seed-on-startup=true

4.Install Dependencies

If using IntelliJ:
Right-click project
Maven → Reload Project
Or via terminal:
mvn clean install

5.Run the Application
Using IntelliJ:
Open NvdCveApplication.java
Click Run
Or using terminal:
mvn sprig-boot:run

Application runs at:
http://localhost:8080
API Endpoints
1. Sync CVEs from NVD
Fetch and store CVEs from the NVD API:
GET /sync
<img width="420" height="256" alt="image" src="https://github.com/user-attachments/assets/188d6213-ac9d-47c4-b8ec-c60884b35a9b" />
Example:
http://localhost:8080/sync

3. View CVE List (Paginated)
GET /
Example:
http://localhost:8080/
<img width="455" height="752" alt="Screenshot 2026-02-26 110915" src="https://github.com/user-attachments/assets/e87327fe-64d9-4b2b-9452-9704e8efb13d" />

Displays:
CVE ID
CVSS Score
Status

4. View Individual CVE Details
GET /cve/{cveId}
<img width="719" height="519" alt="Screenshot 2026-02-26 110922" src="https://github.com/user-attachments/assets/f9e127d8-8084-4ca1-81a2-b2ff3924f8b3" />
Example:
http://localhost:8080/cve/CVE-2023-12345
Displays:
CVE ID
Description
CVSS Score
Published Date
Last Modified Date
Status

6.Database Structure
Table: cve_records
Fields:
id (Primary Key)
cveId (Unique)
identifier
description
cvssScore
status
publishedDate
lastModifiedDate
