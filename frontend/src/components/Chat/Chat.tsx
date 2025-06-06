import { useState } from "react";
import styles from "../../stylesheets/Chat.module.css";
import axios from "axios";

function Chat() {
  // zacetni message chatta
  const [messages, setMessages] = useState([
    { sender: "bot", text: "Hello how can i help you?" },
  ]);
  const [input, setInput] = useState("");

  const askAI = async (question: String) => {
    try {
      const res = await axios.post(
        `${import.meta.env.VITE_API_BACKEND_URL}/generate/chat`,
        { question: input },
        { withCredentials: true }
      );
      return res.data.response;
    } catch (error) {
      console.error("Generating chat response error:", error);
    }
  };

  const handleSend = async () => {
    if (!input.trim()) return;
    setMessages([...messages, { sender: "user", text: input }]);

    const chatResponse = await askAI(input);

    setInput("");

    setMessages((prev) => [...prev, { sender: "bot", text: chatResponse }]);
  };

  return (
    <div className={styles.chatWrapper}>
      <h3>Ask Our Bot</h3>
      <div className={styles.messageArea}>
        {messages.map((msg, index) => (
          <div
            key={index}
            className={
              msg.sender === "bot" ? styles.botMessage : styles.userMessage
            }
          >
            {msg.text}
          </div>
        ))}
      </div>
      <div className={styles.inputArea}>
        <input
          type="text"
          placeholder="Ask question..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
        />
        <button onClick={handleSend}>Send</button>
      </div>
    </div>
  );
}

export default Chat;
