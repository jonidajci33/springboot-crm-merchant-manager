# Quick Start Guide - Database Setup

Get your Merchant Manager CRM database up and running in 5 minutes!

## Prerequisites Checklist

- [ ] PostgreSQL 12+ installed
- [ ] Database client (pgAdmin or psql)
- [ ] Database credentials ready
- [ ] 5 minutes of time

## Step-by-Step Setup

### 1. Create Database (30 seconds)

**Option A: Using psql**
```bash
createdb merchant_manager_crm
```

**Option B: Using pgAdmin**
1. Right-click "Databases" → "Create" → "Database"
2. Name: `merchant_manager_crm`
3. Click "Save"

### 2. Run SQL Scripts (2 minutes)

**Important:** Run scripts in this exact order!

#### Using psql:
```bash
cd SQL
psql -d merchant_manager_crm -f 01_create_tables.sql
psql -d merchant_manager_crm -f 02_indexes_and_constraints.sql
psql -d merchant_manager_crm -f 03_seed_data.sql
```

#### Using pgAdmin:
1. Open Query Tool (Tools → Query Tool)
2. Open and execute `01_create_tables.sql`
3. Open and execute `02_indexes_and_constraints.sql`
4. Open and execute `03_seed_data.sql`

### 3. Verify Setup (1 minute)

Run the verification script:
```bash
psql -d merchant_manager_crm -f 04_verify_schema.sql
```

Look for:
- ✓ PASS indicators
- 17 tables created
- No FAIL messages

### 4. Configure Application (2 minutes)

1. Copy `SQL/application.properties.example` to `src/main/resources/application.properties`

2. Update database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/merchant_manager_crm
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Set hibernate to validate:
```properties
spring.jpa.hibernate.ddl-auto=validate
```

### 5. Test Connection (30 seconds)

Start your Spring Boot application:
```bash
./mvnw spring-boot:run
```

Look for:
```
HikariPool-1 - Start completed.
Hibernate: ...
```

No errors = Success! 🎉

## Default Test Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| manager | password123 | MANAGER |
| user | password123 | USER |

**⚠️ Change these passwords immediately in production!**

## What's Created?

### Core Tables (17 total)
- **User Management:** `_user`
- **CRM:** `merchant`, `contact`, `lead`, `contact_merchant`
- **Templates:** `menu`, `template`, `template_form`, `template_form_value`
- **Miles System:** `pointing_system`, `merchant_miles`
- **Payments:** `dejavoo_credentials`
- **Files:** `file_metadata`
- **Defaults:** `template_default`, `template_form_default`, `template_form_value_default`, `merchant_tpl`

### Sample Data
- 3 users (admin, manager, user)
- 5 menus
- 4 pointing systems
- 3 merchants
- 3 contacts
- 3 leads

### Performance Features
- 40+ indexes for fast queries
- JSONB GIN indexes for template forms
- Foreign key indexes
- Composite indexes for common queries

## Common Issues & Solutions

### ❌ "database does not exist"
```bash
# Create the database first
createdb merchant_manager_crm
```

### ❌ "relation already exists"
```bash
# Drop existing tables first
psql -d merchant_manager_crm -f 99_drop_tables.sql
# Then recreate
psql -d merchant_manager_crm -f 01_create_tables.sql
```

### ❌ "password authentication failed"
```bash
# Check your credentials
psql -U your_username -d merchant_manager_crm
```

### ❌ Spring Boot connection refused
```properties
# Verify your application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/merchant_manager_crm
spring.datasource.username=correct_username
spring.datasource.password=correct_password
```

## Next Steps

### Development
1. ✅ Database is ready
2. Configure Supabase storage (see `SUPABASE_STORAGE_SETUP.md`)
3. Set up Dejavoo credentials
4. Test API endpoints via Swagger: `http://localhost:8080/swagger-ui.html`

### Production
1. Change all default passwords
2. Use environment variables for credentials
3. Enable SSL connections
4. Set up automated backups
5. Configure connection pooling
6. Review security settings

## Quick Commands

```bash
# Start PostgreSQL (Windows)
pg_ctl -D "C:\Program Files\PostgreSQL\XX\data" start

# Start PostgreSQL (Mac)
brew services start postgresql

# Start PostgreSQL (Linux)
sudo service postgresql start

# Connect to database
psql -d merchant_manager_crm

# Check database size
psql -d merchant_manager_crm -c "SELECT pg_size_pretty(pg_database_size('merchant_manager_crm'));"

# List all tables
psql -d merchant_manager_crm -c "\dt"

# Backup database
pg_dump merchant_manager_crm > backup_$(date +%Y%m%d).sql

# Restore database
psql merchant_manager_crm < backup_20250115.sql
```

## Need Help?

1. Check `README.md` for detailed documentation
2. Run `04_verify_schema.sql` to diagnose issues
3. Review application logs
4. Check PostgreSQL logs

## Success Checklist

- [ ] Database created
- [ ] All scripts executed without errors
- [ ] Verification script shows PASS
- [ ] Application.properties configured
- [ ] Spring Boot connects successfully
- [ ] Can login with test credentials
- [ ] Swagger UI accessible

**All checked?** You're ready to go! 🚀

---

**Estimated Total Time:** 5 minutes
**Difficulty:** Easy
**Next:** See `SUPABASE_STORAGE_SETUP.md` for file storage configuration
