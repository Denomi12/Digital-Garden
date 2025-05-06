import express from 'express';

const app = express();

// middleware to automatically parse incoming JSON payloads in HTTP request bodies
app.use(express.json());

export default app;