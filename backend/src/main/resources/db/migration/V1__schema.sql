CREATE TABLE profiles(id uuid PRIMARY KEY, timezone varchar(80) NOT NULL DEFAULT 'Asia/Tokyo', daily_goal integer NOT NULL DEFAULT 10 CHECK(daily_goal BETWEEN 1 AND 100),theme varchar(20) NOT NULL DEFAULT 'system',target_level varchar(2) NOT NULL DEFAULT 'N3');
CREATE TABLE learning_items (
 id uuid PRIMARY KEY,owner_id uuid REFERENCES profiles(id) ON DELETE CASCADE,type varchar(255) NOT NULL CHECK(type IN ('VOCABULARY','GRAMMAR','KANJI','PERSONAL')),
 word varchar(200) NOT NULL,reading varchar(300),meaning_vi varchar(1000) NOT NULL,jlpt_level varchar(255) CHECK(jlpt_level IN ('N5','N4','N3','N2','N1')),part_of_speech varchar(255),example_sentence varchar(2000),example_reading varchar(2000),example_meaning_vi varchar(2000),notes varchar(8000),formation varchar(2000),explanation varchar(8000),onyomi varchar(255),kunyomi varchar(255),stroke_count integer,mnemonic varchar(2000),extra_examples varchar(4000),created_at timestamptz NOT NULL DEFAULT now(),updated_at timestamptz NOT NULL DEFAULT now(),version bigint NOT NULL DEFAULT 0,
 CHECK ((type='PERSONAL' AND owner_id IS NOT NULL) OR (type<>'PERSONAL' AND owner_id IS NULL))
);
CREATE INDEX items_catalog ON learning_items(type,jlpt_level,created_at,id) WHERE owner_id IS NULL;
CREATE INDEX items_owner ON learning_items(owner_id,created_at,id) WHERE owner_id IS NOT NULL;
CREATE TABLE user_learning_progress(id uuid PRIMARY KEY,user_id uuid NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,item_id uuid NOT NULL REFERENCES learning_items(id) ON DELETE CASCADE,status varchar(255) NOT NULL DEFAULT 'NEW' CHECK(status IN ('NEW','LEARNING','REVIEW','MASTERED')),favorite boolean NOT NULL DEFAULT false,review_count integer NOT NULL DEFAULT 0,correct_count integer NOT NULL DEFAULT 0,incorrect_count integer NOT NULL DEFAULT 0,interval_days integer NOT NULL DEFAULT 0,difficulty float8 NOT NULL DEFAULT 2.5,last_reviewed_at timestamptz,next_review_at timestamptz,version bigint NOT NULL DEFAULT 0,UNIQUE(user_id,item_id));
CREATE INDEX review_due ON user_learning_progress(user_id,next_review_at);
CREATE TABLE review_history(id uuid PRIMARY KEY,user_id uuid NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,item_id uuid NOT NULL REFERENCES learning_items(id) ON DELETE CASCADE,request_id uuid NOT NULL,grade varchar(255) NOT NULL CHECK(grade IN ('AGAIN','HARD','GOOD','EASY')),reviewed_at timestamptz NOT NULL,UNIQUE(user_id,request_id));
CREATE INDEX history_user_time ON review_history(user_id,reviewed_at DESC);
-- REST data access is exclusively through Spring Boot. No anon/authenticated policies.
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE learning_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_learning_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE review_history ENABLE ROW LEVEL SECURITY;
