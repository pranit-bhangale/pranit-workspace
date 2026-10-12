-- Flyway Migration V2: Portfolio Module Schema

CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    proficiency_level VARCHAR(30) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_skills_user_id ON skills(user_id);
CREATE INDEX IF NOT EXISTS idx_skills_category ON skills(category);
CREATE INDEX IF NOT EXISTS idx_skills_proficiency ON skills(proficiency_level);
CREATE INDEX IF NOT EXISTS idx_skills_order ON skills(display_order);

CREATE TABLE IF NOT EXISTS educations (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    degree_title VARCHAR(200) NOT NULL,
    degree_type VARCHAR(50) NOT NULL,
    institution VARCHAR(200) NOT NULL,
    start_date DATE,
    end_date DATE,
    period_display VARCHAR(100) NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT false,
    grade_value VARCHAR(50),
    grade_scale VARCHAR(50),
    distinction_status VARCHAR(100),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_educations_user_id ON educations(user_id);
CREATE INDEX IF NOT EXISTS idx_educations_degree_type ON educations(degree_type);
CREATE INDEX IF NOT EXISTS idx_educations_order ON educations(display_order);

CREATE TABLE IF NOT EXISTS tech_stacks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_tech_stacks_name ON tech_stacks(name);

CREATE TABLE IF NOT EXISTS experiences (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    company VARCHAR(150) NOT NULL,
    role VARCHAR(100) NOT NULL,
    employment_type VARCHAR(30) NOT NULL,
    workplace_type VARCHAR(30) NOT NULL,
    start_date DATE,
    end_date DATE,
    period_display VARCHAR(100) NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT false,
    description_markdown TEXT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_experiences_user_id ON experiences(user_id);
CREATE INDEX IF NOT EXISTS idx_experiences_order ON experiences(display_order);

CREATE TABLE IF NOT EXISTS experience_tech_stacks (
    experience_id BIGINT NOT NULL,
    tech_stack_id BIGINT NOT NULL,
    CONSTRAINT pk_experience_tech_stacks PRIMARY KEY (experience_id, tech_stack_id),
    CONSTRAINT fk_experience_tech_stack FOREIGN KEY (experience_id) REFERENCES experiences(id) ON DELETE CASCADE,
    CONSTRAINT fk_tech_stack_experience FOREIGN KEY (tech_stack_id) REFERENCES tech_stacks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS coding_achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    platform VARCHAR(50) NOT NULL,
    problems_solved INT NOT NULL,
    profile_link VARCHAR(2048),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_coding_achievements_user_id ON coding_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_coding_achievements_platform ON coding_achievements(platform);
CREATE INDEX IF NOT EXISTS idx_coding_achievements_order ON coding_achievements(display_order);

CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_name VARCHAR(100) NOT NULL,
    sender_email VARCHAR(100) NOT NULL,
    sender_phone VARCHAR(30),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    message_content TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNREAD',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_contact_messages_status ON contact_messages(status);
CREATE INDEX IF NOT EXISTS idx_contact_messages_created ON contact_messages(created_at);

CREATE TABLE IF NOT EXISTS features (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    color VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_features_name ON features(name);

CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_tags_name ON tags(name);

CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description TEXT NOT NULL,
    demo_link VARCHAR(500),
    github_link VARCHAR(500),
    video_url VARCHAR(500),
    image_key VARCHAR(500),
    is_featured BOOLEAN NOT NULL DEFAULT false,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_projects_category ON projects(category);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_featured ON projects(is_featured);
CREATE INDEX IF NOT EXISTS idx_projects_order ON projects(display_order);

CREATE TABLE IF NOT EXISTS project_tech_stacks (
    project_id BIGINT NOT NULL,
    tech_stack_id BIGINT NOT NULL,
    CONSTRAINT pk_project_tech_stacks PRIMARY KEY (project_id, tech_stack_id),
    CONSTRAINT fk_project_tech_stack FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tech_stack_project FOREIGN KEY (tech_stack_id) REFERENCES tech_stacks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_features (
    project_id BIGINT NOT NULL,
    feature_id BIGINT NOT NULL,
    CONSTRAINT pk_project_features PRIMARY KEY (project_id, feature_id),
    CONSTRAINT fk_project_feature FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_feature_project FOREIGN KEY (feature_id) REFERENCES features(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_tags (
    project_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT pk_project_tags PRIMARY KEY (project_id, tag_id),
    CONSTRAINT fk_project_tag FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag_project FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
