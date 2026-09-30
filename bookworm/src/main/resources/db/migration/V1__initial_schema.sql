-- Flyway migration V1: Initial schema for BookWorm e-bookstore platform

-- ── Categories ───────────────────────────────────────────────────────────────
CREATE TABLE categories (
    id      VARCHAR(36)  PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE,
    slug    VARCHAR(100) NOT NULL UNIQUE
);

-- ── Authors ───────────────────────────────────────────────────────────────────
CREATE TABLE authors (
    id                VARCHAR(36)  PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    bio               TEXT,
    profile_image_url VARCHAR(500)
);

-- ── Books ─────────────────────────────────────────────────────────────────────
CREATE TABLE books (
    id               VARCHAR(36)    PRIMARY KEY,
    title            VARCHAR(500)   NOT NULL,
    author_id        VARCHAR(36)    NOT NULL REFERENCES authors(id),
    description      TEXT,
    publisher        VARCHAR(255),
    language         VARCHAR(50),
    isbn             VARCHAR(20),
    format           VARCHAR(50)    NOT NULL,
    price            NUMERIC(10,2)  NOT NULL,
    currency         VARCHAR(10)    NOT NULL DEFAULT 'INR',
    cover_image_url  VARCHAR(500),
    sells_count      INTEGER        NOT NULL DEFAULT 0,
    average_rating   NUMERIC(3,2)   NOT NULL DEFAULT 0.00,
    review_count     INTEGER        NOT NULL DEFAULT 0
);

-- ── Book ↔ Category (M:M) ──────────────────────────────────────────────────
CREATE TABLE book_categories (
    book_id     VARCHAR(36) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    category_id VARCHAR(36) NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, category_id)
);

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id                   VARCHAR(36)  PRIMARY KEY,
    first_name           VARCHAR(100) NOT NULL,
    last_name            VARCHAR(100) NOT NULL,
    email                VARCHAR(255) NOT NULL UNIQUE,
    password             VARCHAR(255) NOT NULL,
    phone_number         VARCHAR(20),
    gift_points_balance  INTEGER      NOT NULL DEFAULT 0
);

-- ── Addresses ────────────────────────────────────────────────────────────────
CREATE TABLE addresses (
    id            VARCHAR(36)  PRIMARY KEY,
    user_id       VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    address_line1 VARCHAR(500) NOT NULL,
    address_line2 VARCHAR(500),
    city          VARCHAR(100) NOT NULL,
    pin           VARCHAR(20)  NOT NULL,
    state         VARCHAR(100) NOT NULL,
    country       VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL
);

-- ── Carts ─────────────────────────────────────────────────────────────────────
CREATE TABLE carts (
    id                  VARCHAR(36)   PRIMARY KEY,
    user_id             VARCHAR(36)   NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    coupon_code         VARCHAR(50),
    coupon_discount     NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    gift_points_discount NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    updated_at          TIMESTAMP
);

-- ── Cart Items ────────────────────────────────────────────────────────────────
CREATE TABLE cart_items (
    id          VARCHAR(36)   PRIMARY KEY,
    cart_id     VARCHAR(36)   NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    book_id     VARCHAR(36)   NOT NULL REFERENCES books(id),
    quantity    INTEGER       NOT NULL CHECK (quantity >= 1),
    unit_price  NUMERIC(10,2) NOT NULL,
    UNIQUE (cart_id, book_id)
);

-- ── Wishlist Items ────────────────────────────────────────────────────────────
CREATE TABLE wishlist_items (
    id      VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id VARCHAR(36) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE (user_id, book_id)
);

-- ── Orders ────────────────────────────────────────────────────────────────────
CREATE TABLE orders (
    id                   VARCHAR(36)   PRIMARY KEY,
    user_id              VARCHAR(36)   NOT NULL REFERENCES users(id),
    delivery_address_id  VARCHAR(36)   REFERENCES addresses(id),
    status               VARCHAR(30)   NOT NULL DEFAULT 'PENDING_PAYMENT',
    subtotal             NUMERIC(10,2) NOT NULL,
    tax                  NUMERIC(10,2) NOT NULL,
    delivery_charges     NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    discount             NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    total_amount         NUMERIC(10,2) NOT NULL,
    currency             VARCHAR(10)   NOT NULL DEFAULT 'INR',
    payment_method       VARCHAR(20),
    payment_session_id   VARCHAR(255),
    transaction_id       VARCHAR(255),
    created_at           TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ── Order Items ───────────────────────────────────────────────────────────────
CREATE TABLE order_items (
    id                      VARCHAR(36)   PRIMARY KEY,
    order_id                VARCHAR(36)   NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    book_id                 VARCHAR(36)   NOT NULL REFERENCES books(id),
    quantity                INTEGER       NOT NULL CHECK (quantity >= 1),
    unit_price              NUMERIC(10,2) NOT NULL,
    item_total              NUMERIC(10,2) NOT NULL,
    estimated_delivery_date DATE
);

-- ── Reviews ───────────────────────────────────────────────────────────────────
CREATE TABLE reviews (
    id         VARCHAR(36)  PRIMARY KEY,
    book_id    VARCHAR(36)  NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    user_id    VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating     INTEGER      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Coupons ───────────────────────────────────────────────────────────────────
CREATE TABLE coupons (
    id             VARCHAR(36)   PRIMARY KEY,
    code           VARCHAR(50)   NOT NULL UNIQUE,
    discount_value NUMERIC(10,2) NOT NULL,
    expires_at     TIMESTAMP,
    active         BOOLEAN       NOT NULL DEFAULT TRUE
);

-- ── Indexes ───────────────────────────────────────────────────────────────────
CREATE INDEX idx_books_author      ON books(author_id);
CREATE INDEX idx_books_format      ON books(format);
CREATE INDEX idx_books_language    ON books(language);
CREATE INDEX idx_books_price       ON books(price);
CREATE INDEX idx_books_sells_count ON books(sells_count DESC);
CREATE INDEX idx_cart_user         ON carts(user_id);
CREATE INDEX idx_orders_user       ON orders(user_id);
CREATE INDEX idx_orders_session    ON orders(payment_session_id);
CREATE INDEX idx_reviews_book      ON reviews(book_id);
CREATE INDEX idx_wishlist_user     ON wishlist_items(user_id);
