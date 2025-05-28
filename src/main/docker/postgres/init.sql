-- Create appuser and database
CREATE USER appuser WITH PASSWORD 'app@123';

-- Allow user to connect to database
GRANT CONNECT ON DATABASE netflix TO appuser;

-- Grant permissions on public schema
GRANT USAGE, CREATE ON SCHEMA public TO appuser;

-- Grant all permissions on existing tables
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO appuser;

-- Grant all permissions on sequences (if using SERIAL/BIGSERIAL ids)
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO appuser;

-- Ensure future tables/sequences will be accessible too
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO appuser;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO appuser;
