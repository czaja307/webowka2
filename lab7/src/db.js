import { Pool } from "pg";

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_DATABASE,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

async function initDb(force = false, seed = false) {
  if (force) {
    await pool.query("DROP TABLE IF EXISTS todos");
    await pool.query("DROP TABLE IF EXISTS users");
  }

  await pool.query(`
    CREATE TABLE IF NOT EXISTS users (
      id SERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL,
      login VARCHAR(255) NOT NULL
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS todos (
      id SERIAL PRIMARY KEY,
      title VARCHAR(255) NOT NULL,
      completed BOOLEAN NOT NULL,
      user_id INTEGER REFERENCES users(id) ON DELETE CASCADE
    );
  `);

  if (seed) {
    await pool.query(`
      INSERT INTO users (name, email, login) VALUES
      ('Alice', 'alice@example.com', 'alice123'),
      ('Bob', 'bob@example.com', 'bob456'),
      ('Charlie', 'charlie@example.com', 'charlie789');
    `);

    await pool.query(`
      INSERT INTO todos (title, completed, user_id) VALUES
      ('Buy groceries', false, 1),
      ('Complete project', true, 2),
      ('Read a book', false, 3),
      ('Go for a run', true, 1),
      ('Plan vacation', false, 2),
      ('Learn GraphQL', true, 3);
    `);
  }
}

export { pool, initDb };
