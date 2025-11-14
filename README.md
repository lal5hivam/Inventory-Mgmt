# 📦 Inventory Management System

A full-stack enterprise inventory management system built with React and Spring Boot, featuring role-based access control, real-time analytics, and comprehensive inventory tracking capabilities.

---

## 🌟 Features

### 🔐 Authentication & Authorization
- **JWT-based authentication** with secure token management
- **Role-based access control** (ADMIN, MANAGER roles)
- **Encrypted local storage** for sensitive data using CryptoJS
- **Protected routes** with custom guards

### 📊 Dashboard & Analytics
- **Real-time transaction analytics** with interactive charts (Recharts)
- **Monthly/Yearly data visualization** for sales and purchases
- **Daily transaction tracking** with amount and quantity metrics
- **Dynamic filtering** by date and data type

### 🛍️ Product Management
- **CRUD operations** for products with image upload
- **SKU-based tracking** with unique identifiers
- **Stock quantity management** with real-time updates
- **Category association** and filtering
- **Product search** functionality
- **Expiry date tracking**

### 📦 Inventory Operations
- **Purchase transactions** - Record incoming inventory
- **Sales transactions** - Track outgoing inventory
- **Transaction history** with detailed records
- **Status tracking** (PENDING, COMPLETED, CANCELLED)
- **Transaction filtering** by type, status, and date

### 👥 User Management
- **User registration** and profile management
- **Admin panel** for user administration
- **Role assignment** and permission control
- **User profile** viewing and editing

### 🏢 Supplier Management
- **Supplier directory** with contact information
- **Email and phone tracking**
- **Product-supplier associations**

### 🏷️ Category Management
- **Product categorization** system
- **Category CRUD operations**
- **Category-based filtering**

---

## 🛠️ Technology Stack

### Frontend
- **React 18.3.1** - UI framework
- **React Router DOM 6.27.0** - Client-side routing
- **Axios 1.7.7** - HTTP client
- **Recharts 2.13.3** - Data visualization
- **CryptoJS 4.2.0** - Encryption library
- **CSS3** - Custom styling

### Backend
- **Spring Boot 3.3.5** - Application framework
- **Java 21** - Programming language
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **MySQL** - Relational database
- **JWT (jjwt 0.12.6)** - Token-based authentication
- **Lombok** - Code generation
- **ModelMapper 3.2.1** - Object mapping
- **Maven** - Build tool

### Security
- **BCrypt password encoding**
- **JWT token validation**
- **CORS configuration**
- **Role-based authorization**
- **Secure password storage**

---

## 📁 Project Structure

```
inventory-management-system/
├── backend/                          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/phegondev/InventoryMgtSystem/
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controllers/     # REST API endpoints
│   │   │   │   ├── dtos/            # Data Transfer Objects
│   │   │   │   ├── enums/           # Enum types
│   │   │   │   ├── exceptions/      # Custom exceptions
│   │   │   │   ├── models/          # JPA entities
│   │   │   │   ├── repositories/    # Data access layer
│   │   │   │   ├── security/        # Security configurations
│   │   │   │   ├── services/        # Business logic
│   │   │   │   └── specification/   # JPA specifications
│   │   │   └── resources/
│   │   │       ├── application.properties           # Main config
│   │   │       ├── application-dev.properties       # Dev config
│   │   │       └── application-prod.properties      # Prod config
│   │   └── test/                    # Test classes
│   ├── Dockerfile                   # Docker configuration
│   └── pom.xml                      # Maven dependencies
│
├── src/                             # React frontend
│   ├── component/
│   │   ├── Layout.jsx               # Main layout wrapper
│   │   ├── Sidebar.jsx              # Navigation sidebar
│   │   └── PaginationComponent.jsx  # Reusable pagination
│   ├── pages/
│   │   ├── LoginPage.jsx            # Authentication
│   │   ├── RegisterPage.jsx         # User registration
│   │   ├── DashboardPage.jsx        # Analytics dashboard
│   │   ├── ProductPage.jsx          # Product listing
│   │   ├── AddEditProductPage.jsx   # Product form
│   │   ├── CategoryPage.jsx         # Category management
│   │   ├── SupplierPage.jsx         # Supplier listing
│   │   ├── AddEditSupplierPage.jsx  # Supplier form
│   │   ├── PurchasePage.jsx         # Purchase transactions
│   │   ├── SellPage.jsx             # Sales transactions
│   │   ├── TransactionsPage.jsx     # Transaction history
│   │   ├── TransactionDetailsPage.jsx # Transaction details
│   │   └── ProfilePage.jsx          # User profile
│   ├── service/
│   │   ├── ApiService.js            # API client
│   │   └── Guard.js                 # Route protection
│   ├── App.js                       # Main app component
│   └── index.js                     # Entry point
│
├── public/                          # Static assets
├── package.json                     # Frontend dependencies
├── create-admin-user.sql           # Admin user setup
├── DEPLOYMENT_GUIDE.md             # Deployment instructions
├── ENV_CONFIGURATION.md            # Environment setup guide
└── README.md                       # This file
```

---

## 🚀 Getting Started

### Prerequisites

- **Node.js 16+** and npm
- **Java 21**
- **Maven 3.9+**
- **MySQL 8.0+**
- **Git**

### Local Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/lal5hivam/Inventory-Mgmt.git
cd Inventory-Mgmt
```

#### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE inventory_db;
```

Run the admin user creation script:

```bash
mysql -u root -p inventory_db < create-admin-user.sql
```

**Default Admin Credentials:**
- Email: `admin@admin.com`
- Password: `password123`

#### 3. Backend Setup

```bash
cd backend

# Update application-dev.properties with your database credentials
# Edit: backend/src/main/resources/application-dev.properties

# Build and run
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:5050`

#### 4. Frontend Setup

```bash
# From project root
npm install

# Create environment file
cp .env.example .env.local

# Update .env.local if needed (defaults should work for local development)
REACT_APP_API_BASE_URL=http://localhost:5050/api
REACT_APP_ENCRYPTION_KEY=phegon-dev-inventory

# Start development server
npm start
```

The frontend will start on `http://localhost:3000`

---

## 🔧 Configuration

### Environment Variables

#### Frontend (.env.local / .env.production)
```bash
REACT_APP_API_BASE_URL=http://localhost:5050/api
REACT_APP_ENCRYPTION_KEY=your-encryption-key
```

#### Backend (application-prod.properties)
```properties
# Server Configuration
server.port=${SERVER_PORT:5050}

# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}

# CORS Configuration
cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

For detailed configuration, see [ENV_CONFIGURATION.md](ENV_CONFIGURATION.md)

---

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Users
- `GET /api/users/all` - Get all users (ADMIN)
- `GET /api/users/current` - Get current user info
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/update/{id}` - Update user
- `DELETE /api/users/delete/{id}` - Delete user (ADMIN)

### Products
- `POST /api/products/add` - Add product (ADMIN)
- `PUT /api/products/update` - Update product (ADMIN)
- `GET /api/products/all` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search` - Search products
- `DELETE /api/products/delete/{id}` - Delete product (ADMIN)

### Categories
- `POST /api/categories/add` - Create category (ADMIN)
- `GET /api/categories/all` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `PUT /api/categories/update/{id}` - Update category (ADMIN)
- `DELETE /api/categories/delete/{id}` - Delete category (ADMIN)

### Suppliers
- `POST /api/suppliers/add` - Create supplier (ADMIN)
- `GET /api/suppliers/all` - Get all suppliers
- `GET /api/suppliers/{id}` - Get supplier by ID
- `PUT /api/suppliers/update/{id}` - Update supplier (ADMIN)
- `DELETE /api/suppliers/delete/{id}` - Delete supplier (ADMIN)

### Transactions
- `POST /api/transactions/create` - Create transaction
- `GET /api/transactions/all` - Get all transactions
- `GET /api/transactions/{id}` - Get transaction by ID
- `PUT /api/transactions/update/{id}` - Update transaction
- `DELETE /api/transactions/delete/{id}` - Delete transaction

---

## 🎭 User Roles & Permissions

### ADMIN
- Full system access
- User management
- Product, category, and supplier CRUD operations
- All transaction operations
- Dashboard analytics

### MANAGER
- Dashboard access
- View products, categories, suppliers
- Create and manage transactions (buy/sell)
- View transaction history
- Profile management

---

## 🐳 Docker Deployment

### Build Docker Image

```bash
cd backend
mvn clean package -DskipTests
docker build -t inventory-management-backend .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://your-db-host:3306/inventory_db \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  -e CORS_ALLOWED_ORIGINS=https://your-frontend.com \
  inventory-management-backend
```

---

## 🌐 Production Deployment

### Recommended Platforms

**Backend:**
- Railway (Recommended)
- Render
- AWS Elastic Beanstalk
- Heroku

**Frontend:**
- Vercel (Recommended)
- Netlify
- AWS Amplify

**Database:**
- Railway MySQL
- AWS RDS
- DigitalOcean Managed Database

For complete deployment instructions, see [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

---

## 🧪 Testing

### Run Backend Tests
```bash
cd backend
mvn test
```

### Run Frontend Tests
```bash
npm test
```

---

## 📊 Database Schema

### Core Entities

- **users** - System users with roles
- **products** - Inventory items
- **categories** - Product categories
- **suppliers** - Supplier information
- **transactions** - Purchase/sale records

### Relationships

- User → Transactions (One-to-Many)
- Product → Category (Many-to-One)
- Transaction → Product (Many-to-One)
- Transaction → User (Many-to-One)

---

## 🔒 Security Features

- **Password Encryption** - BCrypt hashing
- **JWT Authentication** - Stateless token-based auth
- **CORS Protection** - Configured origins
- **Input Validation** - Bean validation
- **SQL Injection Prevention** - Parameterized queries
- **XSS Protection** - React's built-in escaping
- **Encrypted Storage** - AES encryption for local storage

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License.

---

## 👨‍💻 Developer

**Shivam Lal**
- GitHub: [@lal5hivam](https://github.com/lal5hivam)

---

## 🐛 Known Issues & Troubleshooting

### Backend won't start
- Ensure MySQL is running
- Verify database credentials in application-dev.properties
- Check Java version (must be 21)

### Frontend can't connect to backend
- Verify REACT_APP_API_BASE_URL is correct
- Check CORS configuration on backend
- Ensure backend is running

### JWT token errors
- Verify JWT_SECRET is properly set
- Check token expiration time
- Clear browser local storage

---

## 📚 Additional Documentation

- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Complete deployment instructions
- [ENV_CONFIGURATION.md](ENV_CONFIGURATION.md) - Environment setup guide
- [FINAL_DEPLOYMENT_CONFIG.md](FINAL_DEPLOYMENT_CONFIG.md) - Production configuration

---

## 🙏 Acknowledgments

- Spring Boot Team
- React Team
- All open-source contributors

---

**Built with ❤️ using React and Spring Boot**
