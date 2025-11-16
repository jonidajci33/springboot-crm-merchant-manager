# Merchant Manager CRM - Database Schema

This folder contains SQL scripts to create and manage the database schema for the Merchant Manager CRM system.

## Prerequisites

- PostgreSQL 12 or higher
- Database user with CREATE, DROP, and INSERT privileges
- pgAdmin or psql command-line tool

## Database Setup

### Method 1: Using psql (Command Line)

```bash
# Create database
createdb merchant_manager_crm

# Connect to database
psql -d merchant_manager_crm

# Run scripts in order
\i 01_create_tables.sql
\i 02_indexes_and_constraints.sql
\i 03_seed_data.sql
```

### Method 2: Using pgAdmin (GUI)

1. Open pgAdmin and connect to your PostgreSQL server
2. Right-click on "Databases" and create a new database named `merchant_manager_crm`
3. Open the Query Tool for your new database
4. Execute scripts in order:
   - `01_create_tables.sql`
   - `02_indexes_and_constraints.sql`
   - `03_seed_data.sql`

## Script Overview

### 01_create_tables.sql
Creates all database tables with proper relationships:
- User authentication and management
- Menu and template system
- Merchant and contact management
- Merchant miles and pointing system
- Dejavoo payment integration
- File storage metadata

**Tables Created:**
- `_user` - User accounts
- `menu` - Menu configuration
- `template` - Dynamic templates
- `template_form` - Form fields with JSONB
- `template_default` - Default templates
- `template_form_default` - Default form fields
- `merchant` - Merchant records
- `contact` - Contact records
- `lead` - Lead tracking
- `contact_merchant` - Contact-merchant relationships
- `merchant_tpl` - Merchant templates
- `template_form_value` - Template values
- `template_form_value_default` - Default template values
- `pointing_system` - Miles configurations
- `merchant_miles` - Miles tracking
- `dejavoo_credentials` - Payment processor credentials
- `file_metadata` - Cloud storage file tracking

### 02_indexes_and_constraints.sql
Creates database indexes for optimal performance:
- Primary key indexes (automatic)
- Foreign key indexes
- JSONB GIN indexes for fast JSON queries
- Composite indexes for common query patterns
- Partial indexes for filtered queries

**Performance Benefits:**
- Faster lookups on foreign keys
- Optimized JSONB queries
- Improved sorting and filtering
- Better JOIN performance

### 03_seed_data.sql
Inserts initial development data:
- 3 test users (admin, manager, user)
- 5 sample menus
- 4 pointing system configurations
- Sample merchants, contacts, and leads

**Default Credentials:**
- Username: `admin` / Password: `password123`
- Username: `manager` / Password: `password123`
- Username: `user` / Password: `password123`

**⚠️ IMPORTANT:** Change all passwords in production!

### 99_drop_tables.sql
**DANGER:** Drops all tables and data
- Use only for development/testing
- Cannot be undone
- Always backup before running

## Database Schema Diagram

```
_user (Authentication)
  ├─> template (via user_id)
  ├─> template_default (via user_id)
  ├─> merchant_miles (via user_id)
  └─> file_metadata (via uploaded_by)

menu (Configuration)
  ├─> template (via menu_id)
  └─> template_default (via menu_id)

template
  ├─> template_form (via template_id)
  └─> merchant_tpl (via template_id)

merchant
  ├─> contact_merchant (via merchant_id)
  ├─> merchant_tpl (via merchant_id)
  ├─> merchant_miles (via merchant_id)
  └─> dejavoo_credentials (via merchant_id)

contact
  └─> contact_merchant (via contact_id)

pointing_system
  └─> merchant_miles (via pointing_system_id)
```

## Application Configuration

### Spring Boot application.properties

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/merchant_manager_crm
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

**Note:** Set `spring.jpa.hibernate.ddl-auto=validate` to ensure Hibernate validates the schema against your entities without modifying it.

## Migration from Auto-Generated Schema

If you were using `spring.jpa.hibernate.ddl-auto=update`:

1. Backup your current database
2. Export existing data
3. Drop current tables using `99_drop_tables.sql`
4. Run creation scripts in order
5. Import your data

## Maintenance

### Analyzing Tables
Run after bulk inserts or updates:
```sql
ANALYZE _user;
ANALYZE merchant;
ANALYZE template_form;
-- etc.
```

### Vacuum Database
Reclaim storage and update statistics:
```sql
VACUUM ANALYZE;
```

### Check Table Sizes
```sql
SELECT
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## Backup and Restore

### Backup
```bash
pg_dump -U username -d merchant_manager_crm -f backup.sql
```

### Restore
```bash
psql -U username -d merchant_manager_crm -f backup.sql
```

## Troubleshooting

### Tables Already Exist
If you get "table already exists" errors:
```sql
DROP TABLE IF EXISTS table_name CASCADE;
```

### Foreign Key Violations
Check data integrity:
```sql
-- Find orphaned records
SELECT * FROM merchant_miles WHERE merchant_id NOT IN (SELECT id FROM merchant);
```

### JSONB Issues
Ensure PostgreSQL version 9.4+:
```sql
SELECT version();
```

## Production Considerations

1. **Security:**
   - Change all default passwords
   - Use strong, unique passwords
   - Restrict database user permissions
   - Enable SSL for database connections

2. **Performance:**
   - Monitor query performance
   - Add indexes based on actual usage patterns
   - Regular VACUUM and ANALYZE
   - Consider partitioning for large tables

3. **Backup:**
   - Implement automated daily backups
   - Test restore procedures regularly
   - Store backups securely off-site

4. **Monitoring:**
   - Set up connection pooling
   - Monitor slow queries
   - Track table growth
   - Alert on failed queries

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

## Support

For issues or questions:
1. Check the application logs
2. Verify database connectivity
3. Review entity annotations in your models
4. Ensure schema matches JPA entities

---

**Last Updated:** 2025-01-15
**Database Version:** PostgreSQL 12+
**Schema Version:** 1.0.0
