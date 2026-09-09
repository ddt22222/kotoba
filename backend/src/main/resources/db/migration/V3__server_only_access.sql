-- Server-owned tables are accessed only by Spring Boot, not by browser roles.
DO $$
BEGIN
 IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'anon') THEN
  REVOKE ALL ON TABLE public.profiles, public.learning_items, public.user_learning_progress, public.review_history FROM anon;
 END IF;
 IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'authenticated') THEN
  REVOKE ALL ON TABLE public.profiles, public.learning_items, public.user_learning_progress, public.review_history FROM authenticated;
 END IF;
END $$;
CREATE INDEX progress_item_fk ON public.user_learning_progress(item_id);
CREATE INDEX history_item_fk ON public.review_history(item_id);
