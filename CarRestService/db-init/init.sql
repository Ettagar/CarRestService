-- Create the database (must be outside of a transaction)
CREATE DATABASE cars_database OWNER postgres;

-- Switch to the new database
\c cars_database;

-- Create the application user if it doesn't exist
DO
$$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'cars_manager'
    ) THEN
        CREATE USER cars_manager WITH PASSWORD '12345';
    END IF;
END
$$;

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON DATABASE cars_database TO cars_manager;

-- Create the schema and assign ownership
CREATE SCHEMA IF NOT EXISTS cars_catalog AUTHORIZATION cars_manager;

-- Grant privileges on schema
GRANT ALL PRIVILEGES ON SCHEMA cars_catalog TO cars_manager;
