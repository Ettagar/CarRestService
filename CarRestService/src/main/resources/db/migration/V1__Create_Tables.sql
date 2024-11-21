-- Create the Manufacturers table
CREATE TABLE IF NOT EXISTS cars_catalog.manufacturers (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 START 1),
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name) -- Manufacturer name should still be unique
);

-- Create the Categories table
CREATE TABLE IF NOT EXISTS cars_catalog.categories (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 START 1),
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Create the Cars table
CREATE TABLE IF NOT EXISTS cars_catalog.cars (
    id VARCHAR(255) NOT NULL,
    make BIGINT NOT NULL,  -- Reference to Manufacturer
    year INTEGER NOT NULL,
    model VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (make) REFERENCES cars_catalog.manufacturers(id) ON DELETE CASCADE
);

-- Create a table to establish a many-to-many relationship between Cars and Categories
CREATE TABLE IF NOT EXISTS cars_catalog.cars_categories (
    car_id VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (car_id, category_id),
    FOREIGN KEY (car_id) REFERENCES cars_catalog.cars(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES cars_catalog.categories(id) ON DELETE CASCADE
);

-- Set the owner of the tables to cars_manager
ALTER TABLE cars_catalog.manufacturers OWNER TO cars_manager;
ALTER TABLE cars_catalog.categories OWNER TO cars_manager;
ALTER TABLE cars_catalog.cars OWNER TO cars_manager;
ALTER TABLE cars_catalog.cars_categories OWNER TO cars_manager;
