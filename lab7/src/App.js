import { createServer } from "node:http";
import { createYoga } from "graphql-yoga";
import { schema } from "./schema.js";
import { initDb } from "./db.js";

const yoga = createYoga({ schema });

const server = createServer(yoga);

const PORT = process.env.PORT || 4000;

await initDb(true, true);

server.listen(PORT, () => {
  console.info(`Server is running on http://localhost:${PORT}/graphql`);
});
