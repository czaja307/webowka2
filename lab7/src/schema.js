import { fileURLToPath } from "url";
import { pool } from "./db.js";
import { readFileSync } from "fs";
import { dirname, join } from "path";
import { createSchema } from "graphql-yoga";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const typeDefs = readFileSync(join(__dirname, "schema.graphql"), "utf8");

const resolvers = {
  Query: {
    users: async () => {
      const res = await pool.query("SELECT * FROM users");
      return res.rows;
    },
    user: async (_, { id }) => {
      const res = await pool.query("SELECT * FROM users WHERE id = $1", [id]);
      return res.rows[0];
    },
    todos: async () => {
      const res = await pool.query("SELECT * FROM todos");
      return res.rows;
    },
    todo: async (_, { id }) => {
      const res = await pool.query("SELECT * FROM todos WHERE id = $1", [id]);
      return res.rows[0];
    },
  },

  User: {
    todos: async (parent) => {
      const res = await pool.query("SELECT * FROM todos WHERE user_id = $1", [
        parent.id,
      ]);
      return res.rows;
    },
  },

  ToDoItem: {
    user: async (parent) => {
      const res = await pool.query("SELECT * FROM users WHERE id = $1", [
        parent.user_id,
      ]);
      return res.rows[0];
    },
  },

  Mutation: {
    addUser: async (_, { name, email, login }) => {
      const res = await pool.query(
        "INSERT INTO users (name, email, login) VALUES ($1, $2, $3) RETURNING *",
        [name, email, login],
      );
      return res.rows[0];
    },
    updateUser: async (_, { id, name, email, login }) => {
      const res = await pool.query(
        `UPDATE users SET 
         name = COALESCE($2, name),
         email = COALESCE($3, email),
         login = COALESCE($4, login)
         WHERE id = $1 RETURNING *`,
        [id, name, email, login],
      );
      return res.rows[0];
    },
    deleteUser: async (_, { id }) => {
      const res = await pool.query("DELETE FROM users WHERE id = $1", [id]);
      return res.rowCount > 0;
    },
    addTodo: async (_, { title, completed, userId }) => {
      const res = await pool.query(
        "INSERT INTO todos (title, completed, user_id) VALUES ($1, $2, $3) RETURNING *",
        [title, completed, userId],
      );
      return res.rows[0];
    },
    updateTodo: async (_, { id, title, completed, userId }) => {
      const res = await pool.query(
        `UPDATE todos SET 
         title = COALESCE($2, title),
         completed = COALESCE($3, completed),
         user_id = COALESCE($4, user_id)
         WHERE id = $1 RETURNING *`,
        [id, title, completed, userId],
      );
      return res.rows[0];
    },
    deleteTodo: async (_, { id }) => {
      const res = await pool.query("DELETE FROM todos WHERE id = $1", [id]);
      return res.rowCount > 0;
    },
  },
};

export const schema = createSchema({
  typeDefs,
  resolvers,
});
