import "dotenv/config";
import http from "http";
import app from "../app";

const PORT = normalizePort(process.env.PORT || "3001");

app.set("port", PORT);

const server = http.createServer(app);

server.listen(PORT);

server.on("error", onError);
server.on("listening", onListening);

/**
 * PORT normalizer helper
 */
function normalizePort(val: string | number): number {
  const port = typeof val === "string" ? parseInt(val, 10) : val;
  return isNaN(port) ? 3001 : port;
}

/**
 * Called if server fails to start
 */
function onError(error: NodeJS.ErrnoException): void {
  if (error.syscall !== "listen") throw error;

  const bind = typeof PORT === "string" ? "Pipe " + PORT : "Port " + PORT;

  switch (error.code) {
    case "EACCES":
      console.error(`${bind} requires elevated privileges`);
      process.exit(1);
      break;
    case "EADDRINUSE":
      console.error(`${bind} is already in use`);
      process.exit(1);
      break;
    default:
      throw error;
  }
}

/**
 * Called once server starts successfully
 */
function onListening(): void {
  const addr = server.address();
  const bind = typeof addr === "string" ? addr : `port ${(addr as any).port}`;
  console.log(`Listening on ${bind}`);
}
