CREATE DATABASE IF NOT EXISTS socius_db;
USE socius_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    bio TEXT,
    avatar_url VARCHAR(255) DEFAULT 'assets/avatars/default-avatar.svg',
    role ENUM('member', 'moderator', 'admin') DEFAULT 'member',
    penalty_points INT DEFAULT 0,
    warning_count INT DEFAULT 0,
    is_active TINYINT(1) DEFAULT 1,
    is_globally_banned TINYINT(1) DEFAULT 0,
    failed_attempts INT DEFAULT 0,
    locked_until DATETIME DEFAULT NULL,
    reset_token VARCHAR(120) DEFAULT NULL,
    reset_token_expires_at DATETIME DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE communities (
    community_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    guidelines TEXT,
    banner_style VARCHAR(80),
    icon_name VARCHAR(80),
    is_private TINYINT(1) DEFAULT 0,
    requires_review TINYINT(1) DEFAULT 1,
    member_count INT DEFAULT 0,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

CREATE TABLE community_memberships (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_membership (community_id, user_id),
    FOREIGN KEY (community_id) REFERENCES communities(community_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE community_moderators (
    moderator_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    user_id INT NOT NULL,
    assigned_by INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_moderator (community_id, user_id),
    FOREIGN KEY (community_id) REFERENCES communities(community_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(user_id)
);

CREATE TABLE posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    author_id INT NOT NULL,
    title VARCHAR(300) NOT NULL,
    content LONGTEXT NOT NULL,
    post_type ENUM('text', 'resource', 'event') DEFAULT 'text',
    resource_url VARCHAR(500),
    status ENUM('draft', 'pending', 'approved', 'rejected', 'removed') DEFAULT 'pending',
    is_featured TINYINT(1) DEFAULT 0,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    rejection_reason VARCHAR(300),
    reviewed_by INT DEFAULT NULL,
    reviewed_at DATETIME DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(community_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    author_id INT NOT NULL,
    parent_id INT DEFAULT NULL,
    content TEXT NOT NULL,
    upvotes INT DEFAULT 0,
    is_removed TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id),
    FOREIGN KEY (parent_id) REFERENCES comments(comment_id) ON DELETE SET NULL
);

CREATE TABLE votes (
    vote_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    vote_type ENUM('up', 'down') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_vote (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE bans (
    ban_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    community_id INT DEFAULT NULL,
    banned_by INT NOT NULL,
    reason TEXT NOT NULL,
    is_global TINYINT(1) DEFAULT 0,
    expires_at DATETIME DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES communities(community_id) ON DELETE CASCADE,
    FOREIGN KEY (banned_by) REFERENCES users(user_id)
);

CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    reporter_id INT NOT NULL,
    post_id INT DEFAULT NULL,
    comment_id INT DEFAULT NULL,
    reason VARCHAR(300) NOT NULL,
    status ENUM('open', 'reviewed', 'dismissed') DEFAULT 'open',
    reviewed_by INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE bulletins (
    bulletin_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    sent_by INT NOT NULL,
    subject VARCHAR(300) NOT NULL,
    body LONGTEXT NOT NULL,
    recipient_count INT DEFAULT 0,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(community_id) ON DELETE CASCADE,
    FOREIGN KEY (sent_by) REFERENCES users(user_id)
);

INSERT INTO users (
    username,
    email,
    phone_number,
    password_hash,
    display_name,
    bio,
    role
) VALUES
    (
        'sociusadmin',
        'admin@socius.app',
        '07700111000',
        SHA2('Admin@1234', 256),
        'Socius Admin',
        'Platform administrator for the Socius ethical community network.',
        'admin'
    ),
    (
        'ava_clarke',
        'ava@socius.app',
        '07700111001',
        SHA2('Ava@2026', 256),
        'Ava Clarke',
        'Accessibility advocate and moderator.',
        'moderator'
    ),
    (
        'milan_green',
        'milan@socius.app',
        '07700111002',
        SHA2('Milan@2026', 256),
        'Milan Green',
        'Community member focused on digital wellbeing.',
        'member'
    );

INSERT INTO communities (
    name,
    slug,
    description,
    guidelines,
    banner_style,
    icon_name,
    created_by
) VALUES
    (
        'Inclusive Design',
        'inclusive-design',
        'Discussions on accessible interfaces, universal design, and ethical product decisions.',
        'Respect lived experience. Cite standards where possible. Share practical improvements.',
        'aurora',
        'palette',
        1
    ),
    (
        'Climate Action',
        'climate-action',
        'Community reflections on local sustainability, resilient cities, and public climate literacy.',
        'Stay evidence-based. Avoid misinformation. Keep solutions practical.',
        'civic',
        'eco',
        1
    ),
    (
        'Digital Wellbeing',
        'digital-wellbeing',
        'A moderated space for healthier online habits, mindful technology use, and humane systems.',
        'Speak constructively. No harassment. Personal stories are welcome.',
        'signal',
        'self_improvement',
        1
    ),
    (
        'Community Care',
        'community-care',
        'Neighbourhood support ideas, volunteering stories, and socially useful local initiatives.',
        'Protect privacy. Avoid posting personal data. Encourage action.',
        'ember',
        'groups',
        1
    );

INSERT INTO community_moderators (community_id, user_id, assigned_by)
VALUES
    (1, 2, 1),
    (3, 2, 1);

INSERT INTO community_memberships (community_id, user_id)
VALUES
    (1, 2),
    (1, 3),
    (3, 3),
    (4, 3);
