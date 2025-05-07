import express from 'express';

const app = express();

// middleware to automatically parse incoming JSON payloads in HTTP request bodies
app.use(express.json());


app.get('/', (req, res) => {
    res.json({ message: 'API is running' });
});

export default app;