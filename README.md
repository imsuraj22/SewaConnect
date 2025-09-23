# 👐 NGO Donation Platform

<p>A microservices-based platform to connect <b>Donors</b> and <b>NGOs</b>, making it easier to donate items or money securely.</p>

---

### 🚀 Features
<ul>
  <li><b>User Service</b>: Handles user registration, authentication (JWT), and role management.</li>
  <li><b>Donation Service</b>: Records donations (items/money), links donors with NGOs, and keeps donation history.</li>
  <li><b>NGO Service</b>: NGOs can create donation packages (e.g., ₹500 = Shirt + Pant), showcase details, and request items.</li>
  <li><b>Admin Service</b>: Manage NGOs, users, and oversee platform activities.</li>
  <li><b>Event-driven</b> communication with Kafka.</li>
  <li><b>RestTemplates</b> Access other services RESTAPIs</li>
  <li><b>PostgreSQL</b> integration for persistence.</li>
  <li><b>React Frontend</b> (in progress) to provide an intuitive UI.</li>
</ul>

---

### 🛠️ Tech Stack
<ul>
  <li><b>Backend:</b> Spring Boot (Java 17)</li>
  <li><b>Microservices:</b> User, Donation, NGO, Admin</li>
  <li><b>Security:</b> JWT Authentication</li>
  <li><b>Messaging:</b> Apache Kafka</li>
  <li><b>Database:</b> PostgreSQL</li>
  <li><b>Containerization:</b> Docker & Docker Compose</li>
  <li><b>Frontend:</b> React (TypeScript)</li>
</ul>

---

# 👐 NGO Donation Platform

<p>A microservices-based platform to connect <b>Donors</b> and <b>NGOs</b>, making it easier to donate items or money securely.</p>

---

### 🚀 Features
<ul>
  <li><b>User Service</b>: Handles user registration, authentication (JWT), and role management.</li>
  <li><b>Donation Service</b>: Records donations (items/money), links donors with NGOs, and keeps donation history.</li>
  <li><b>NGO Service</b>: NGOs can create donation packages (e.g., ₹500 = Shirt + Pant), showcase details, and request items.</li>
  <li><b>Admin Service</b>: Manage NGOs, users, and oversee platform activities.</li>
  <li><b>Event-driven</b> communication with Kafka.</li>
  <li><b>PostgreSQL</b> integration for persistence.</li>
  <li><b>React Frontend</b> (in progress) to provide an intuitive UI.</li>
</ul>

---

### 🛠️ Tech Stack
<ul>
  <li><b>Backend:</b> Spring Boot (Java 11)</li>
  <li><b>Microservices:</b> User, Donation, NGO, Admin</li>
  <li><b>Security:</b> JWT Authentication</li>
  <li><b>Messaging:</b> Apache Kafka</li>
  <li><b>Database:</b> PostgreSQL</li>
  <li><b>Containerization:</b> Docker & Docker Compose</li>
  <li><b>Frontend:</b> React (TypeScript)</li>
</ul>

---

### 🏗️ Architecture
    UserService --> DonationService
    UserService --> NGOService
    DonationService -->|Kafka| NGOService
    NGOService -->|Kafka| DonationService
    AdminService --> UserService
    AdminService --> NGOService
    PostgreSQL[(PostgreSQL)] --> UserService
    PostgreSQL[(PostgreSQL)] --> DonationService
    PostgreSQL[(PostgreSQL)] --> NGOService


### ⚙️ Setup & Run
<li><b>Clone Repository</b></li>

 - git clone https://github.com/your-username/ngo-donation-platform.git
 - cd ngo-donation-platform

<li><b>Start Infrastructure (Kafka + Zookeeper + PostgreSQL)</b></li>

 - Make sure Docker & Docker Compose are installed. Then run:
 - docker-compose up -d

This will start:

<li><b>Zookeeper on port 2181</b></li>
<li><b>Kafka broker on port 9092</b></li>


PostgreSQL (if configured in docker-compose.yml)


<h3>▶️ Run Microservices</h3>
<p>Each microservice has its own folder. Start them one by one:</p>
<ul>
  <li><code>cd user-service</code></li>
  <li><code>./mvnw spring-boot:run</code></li>
</ul>

<p><i>(or build a JAR and run: <code>java -jar target/app.jar</code>)</i></p>

<p><b>Repeat the same for:</b></p>
<ul>
  <li>donation-service</li>
  <li>ngo-service</li>
  <li>admin-service</li>
</ul>

<h3>🌐 Access Services</h3>
<ul>
  <li>👤 <b>User Service</b> → <code>http://localhost:8080</code></li>
  <li>🎁 <b>Donation Service</b> → <code>http://localhost:8081</code></li>
  <li>🏢 <b>NGO Service</b> → <code>http://localhost:8082</code></li>
  <li>⚙️ <b>Admin Service</b> → <code>http://localhost:8083</code></li>
</ul>



📡 Sample API Endpoints
<table> <tr> <th>Service</th> <th>Endpoint</th> <th>Method</th> <th>Description</th> </tr> <tr> <td>User</td> <td><code>/api/users/register</code></td> <td>POST</td> <td>Register a new user</td> </tr> <tr> <td>User</td> <td><code>/api/users/login</code></td> <td>POST</td> <td>Login and get JWT</td> </tr> <tr> <td>Donation</td> <td><code>/api/donations/create</code></td> <td>POST</td> <td>Create a new donation</td> </tr> <tr> <td>NGO</td> <td><code>/api/ngos/packages</code></td> <td>POST</td> <td>Create donation package</td> </tr> <tr> <td>Admin</td> <td><code>/api/admin/ngo-approval</code></td> <td>PUT</td> <td>Approve or reject NGO request</td> </tr> </table>
📌 Future Enhancements
<ul> <li>Deploy to cloud (AWS/GCP/Azure)</li> <li>CI/CD integration with GitHub Actions</li> <li>Complete React frontend integration</li> <li>More detailed analytics dashboard for NGOs/Admins</li> </ul>
👨‍💻 Contributors

Suraj (Backend & Architecture)

