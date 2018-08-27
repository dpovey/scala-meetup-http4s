-- Add UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create Books table for PostgreSQL.
CREATE TABLE books(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title VARCHAR NOT NULL,
  author VARCHAR NOT NULL,
  dewey_decimal_class VARCHAR NOT NULL,
  library_of_congress VARCHAR NOT NULL,
  pages INTEGER NOT NULL
);
