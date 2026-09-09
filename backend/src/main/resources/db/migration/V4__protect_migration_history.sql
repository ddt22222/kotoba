-- Flyway schema history is managed internally by the backend.
-- Do not ALTER flyway_schema_history from within a Flyway migration.

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'anon') THEN
    REVOKE ALL ON TABLE public.flyway_schema_history FROM anon;
  END IF;

  IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'authenticated') THEN
    REVOKE ALL ON TABLE public.flyway_schema_history FROM authenticated;
  END IF;
END $$;