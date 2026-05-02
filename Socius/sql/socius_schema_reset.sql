DROP DATABASE IF EXISTS socius_db;
CREATE DATABASE socius_db;
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
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved',
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
    post_type ENUM('text', 'resource', 'event', 'image') DEFAULT 'text',
    resource_url VARCHAR(500),
    image_url VARCHAR(500),
    image_alt_text VARCHAR(255),
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
    FOREIGN KEY (author_id) REFERENCES users(user_id)
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

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(300) NOT NULL,
    target_url VARCHAR(500),
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
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

INSERT INTO posts (
    community_id,
    author_id,
    title,
    content,
    post_type,
    status,
    is_featured,
    upvotes,
    downvotes,
    view_count,
    reviewed_by,
    reviewed_at,
    created_at,
    updated_at
) VALUES
    (
        1,
        3,
        'Small accessibility wins that made our form easier to finish',
        'We changed three things in a volunteer signup form this week: labels became persistent, error text moved directly under each field, and the submit button now stays disabled until required fields are complete. Completion improved in testing because people did not have to remember hidden placeholder text. What other low effort interface changes have helped your users?',
        'text',
        'approved',
        1,
        42,
        1,
        318,
        2,
        '2026-04-22 09:20:00',
        '2026-04-22 08:48:00',
        '2026-04-22 09:20:00'
    ),
    (
        1,
        2,
        'Keyboard testing checklist for community websites',
        'A quick checklist for anyone shipping a community page: tab through the whole page, check visible focus, open every menu without a mouse, submit forms with Enter, close dialogs with Escape, and make sure the reading order matches the visual order. It sounds basic, but it catches many issues before users do.',
        'resource',
        'approved',
        0,
        31,
        0,
        221,
        2,
        '2026-04-21 14:10:00',
        '2026-04-21 13:58:00',
        '2026-04-21 14:10:00'
    ),
    (
        1,
        3,
        'How should we write alt text for civic data charts',
        'For data charts, I have been using a structure that names the chart type, explains the main trend, then points out any important exception. Example: Line chart showing bus delays rising after 5 PM, with the largest spike on Friday. Is that enough, or should we include source and sample size in the alt text too?',
        'text',
        'approved',
        0,
        18,
        2,
        144,
        2,
        '2026-04-20 18:30:00',
        '2026-04-20 17:52:00',
        '2026-04-20 18:30:00'
    ),
    (
        1,
        2,
        'Design review thread: readable moderation tables',
        'Moderation tables get dense very quickly. I am testing a layout where the report reason, post title, reporter, and action are visible without horizontal scrolling on desktop, while mobile turns every row into a compact review card. Would you keep tables for moderators, or move fully to cards?',
        'text',
        'approved',
        0,
        27,
        1,
        189,
        2,
        '2026-04-19 11:15:00',
        '2026-04-19 10:55:00',
        '2026-04-19 11:15:00'
    ),
    (
        2,
        3,
        'Local climate actions that are small enough to actually maintain',
        'The strongest ideas in our neighborhood group have been boring in a good way: shared tool libraries, weekly food waste pickups, and heat map walks for older residents. Each one has a named owner and a simple calendar. What climate actions have worked because they were easy to repeat?',
        'text',
        'approved',
        1,
        56,
        3,
        407,
        2,
        '2026-04-22 16:05:00',
        '2026-04-22 15:44:00',
        '2026-04-22 16:05:00'
    ),
    (
        2,
        2,
        'Community garden water schedule proposal',
        'Proposal for the community garden: morning watering between 6 and 8, shared notes on dry beds, and a simple sign-out sheet for hose use. The goal is to reduce waste without making volunteers feel policed. Feedback welcome before we post the final schedule at the gate.',
        'event',
        'approved',
        0,
        23,
        1,
        166,
        2,
        '2026-04-21 08:40:00',
        '2026-04-21 08:12:00',
        '2026-04-21 08:40:00'
    ),
    (
        2,
        3,
        'What should be in a climate misinformation rule',
        'I am drafting rules for a climate discussion space. My first pass says: cite credible sources for factual claims, label opinion as opinion, do not post screenshots without source links, and moderators may ask for clarification before removing a post. What is missing?',
        'text',
        'approved',
        0,
        35,
        4,
        248,
        2,
        '2026-04-20 10:25:00',
        '2026-04-20 09:57:00',
        '2026-04-20 10:25:00'
    ),
    (
        2,
        2,
        'Heat safety check-ins for apartment blocks',
        'During last summer heat alerts, residents improvised check-ins through group chats. This year we could make it more reliable with floor captains, printed cards, and a simple escalation path to local services. Has anyone tried a low-tech version of this?',
        'text',
        'approved',
        0,
        29,
        0,
        203,
        2,
        '2026-04-18 19:50:00',
        '2026-04-18 19:26:00',
        '2026-04-18 19:50:00'
    ),
    (
        3,
        3,
        'A calmer notification pattern for community apps',
        'I want fewer red dots and more useful summaries. A daily digest with urgent moderator messages separated from normal replies feels healthier than constant interruption. The tricky part is making sure people still see safety notices quickly.',
        'text',
        'approved',
        1,
        61,
        2,
        512,
        2,
        '2026-04-23 08:20:00',
        '2026-04-23 07:58:00',
        '2026-04-23 08:20:00'
    ),
    (
        3,
        2,
        'Thread idea: what makes a feed feel less addictive',
        'For me, the big changes are chronological ordering, visible stopping points, no infinite autoplay, and prompts that encourage reflection before posting. I still want discovery, just not a page that keeps pulling attention without intention.',
        'text',
        'approved',
        0,
        48,
        2,
        396,
        2,
        '2026-04-22 20:15:00',
        '2026-04-22 19:49:00',
        '2026-04-22 20:15:00'
    ),
    (
        3,
        3,
        'Digital sabbath experiments that did not feel extreme',
        'A full offline day was too much for my work schedule, but a two-hour phone basket during dinner was sustainable. Another useful practice: no community moderation after 9 PM unless there is an urgent safety issue.',
        'text',
        'approved',
        0,
        34,
        1,
        276,
        2,
        '2026-04-21 22:00:00',
        '2026-04-21 21:39:00',
        '2026-04-21 22:00:00'
    ),
    (
        3,
        2,
        'Moderation tone guide for rejected posts',
        'When rejecting a post, I try to explain the specific rule, name the path to resubmit, and avoid moral judgment. A rejection should feel like guidance, not exile. Would a template help, or would it make moderation feel too robotic?',
        'resource',
        'approved',
        0,
        39,
        0,
        301,
        2,
        '2026-04-19 15:40:00',
        '2026-04-19 15:10:00',
        '2026-04-19 15:40:00'
    ),
    (
        4,
        3,
        'Volunteer rota template for small neighborhood groups',
        'The simplest rota that worked for us had four columns: task, owner, backup, and status. The backup column mattered most because people could miss a week without the whole plan collapsing. Sharing this in case it helps another group.',
        'resource',
        'approved',
        1,
        53,
        1,
        455,
        2,
        '2026-04-23 11:15:00',
        '2026-04-23 10:50:00',
        '2026-04-23 11:15:00'
    ),
    (
        4,
        2,
        'How to ask for help without exposing private details',
        'Community care posts need privacy defaults. Instead of naming a person, describe the need, the general area, the timeframe, and the safe contact path. Moderators should remove phone numbers from public posts and move coordination to private channels.',
        'text',
        'approved',
        0,
        44,
        2,
        330,
        2,
        '2026-04-22 12:35:00',
        '2026-04-22 12:12:00',
        '2026-04-22 12:35:00'
    ),
    (
        4,
        3,
        'Weekend repair cafe planning notes',
        'We are planning a repair cafe for small appliances and clothing mending. Current needs: two extension cords, a check-in volunteer, a safety table, and signage that explains what cannot be repaired on site.',
        'event',
        'approved',
        0,
        25,
        0,
        192,
        2,
        '2026-04-20 13:45:00',
        '2026-04-20 13:18:00',
        '2026-04-20 13:45:00'
    ),
    (
        4,
        2,
        'Mutual aid posts need clear expiration dates',
        'A recurring issue in care groups is stale requests. I suggest every request includes an expiration date and a status update when resolved. That keeps the feed useful and prevents people from replying to needs that were already handled.',
        'text',
        'approved',
        0,
        37,
        1,
        264,
        2,
        '2026-04-18 09:30:00',
        '2026-04-18 09:05:00',
        '2026-04-18 09:30:00'
    ),
    (
        1,
        3,
        'Draft: icons for moderation states',
        'I am testing icon labels for pending, approved, rejected, and removed states. This is saved as a draft so I can refine the language before sending it to moderators.',
        'text',
        'draft',
        0,
        0,
        0,
        0,
        NULL,
        NULL,
        '2026-04-23 12:02:00',
        '2026-04-23 12:02:00'
    ),
    (
        3,
        3,
        'Can we add a weekly digital wellbeing check-in',
        'A short weekly check-in could ask members what felt useful, what felt noisy, and whether any thread needs moderator attention.',
        'text',
        'pending',
        0,
        0,
        0,
        0,
        NULL,
        NULL,
        '2026-04-23 12:18:00',
        '2026-04-23 12:18:00'
    );

INSERT INTO comments (post_id, author_id, content, upvotes, created_at)
VALUES
    (1, 2, 'Persistent labels helped our older members a lot. The error placement change is the one I would copy first.', 9, '2026-04-22 10:05:00'),
    (2, 3, 'Adding this checklist to our next review. Escape key behavior is the one we forget most often.', 6, '2026-04-21 15:02:00'),
    (5, 2, 'The named owner point is huge. Shared responsibility sounds nice until no one knows who is doing the task.', 12, '2026-04-22 17:12:00'),
    (7, 3, 'Source links instead of screenshots should be a hard rule. It makes moderation much easier.', 8, '2026-04-20 11:18:00'),
    (9, 2, 'Daily digest plus urgent safety override feels like the right balance.', 14, '2026-04-23 09:03:00'),
    (10, 3, 'Visible stopping points are underrated. A simple end-of-new-posts marker changes the mood of the feed.', 11, '2026-04-22 21:04:00'),
    (13, 2, 'The backup column is such a practical idea. It makes care work less brittle.', 10, '2026-04-23 11:50:00'),
    (16, 3, 'Expiration dates would also help moderators archive resolved requests without guessing.', 7, '2026-04-18 10:10:00');
