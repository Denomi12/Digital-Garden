import { useState } from "react";
import styles from "../../stylesheets/Chat.module.css";
import axios from "axios";

interface ChatProps {
  refreshForum: () => void;
}

function Chat({ refreshForum }: ChatProps) {
  // zacetni message chatta
  const [messages, setMessages] = useState([
    { sender: "bot", text: "Hello how can i help you?" },
  ]);
  const [input, setInput] = useState("");
  const [savedIndexes, setSavedIndexes] = useState<number[]>([]); // shranjeni indeksi

  const askAI = async () => {
    setInput("");
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

    const chatResponse = await askAI();

    setMessages((prev) => [...prev, { sender: "bot", text: chatResponse }]);
  };

  const saveResponse = async (
    question: string,
    answer: string,
    index: number
  ) => {
    const res = await axios.post(
      `${import.meta.env.VITE_API_BACKEND_URL}/question`,
      {
        title: question,
        questionMessage: answer,
        botGenerated: true,
      },
      {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    setSavedIndexes((prev) => [...prev, index]); // dodaj index v seznam shranjenih
    refreshForum();
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
            <br />
            {msg.sender === "bot" &&
              !savedIndexes.includes(index) &&
              index > 0 &&
              messages[index - 1]?.sender === "user" &&
              msg.text !== "Vprašanje ni povezano z vrtnarjenjem." && (
                <button
                  style={{
                    marginTop: "6px",
                    background: "#ffffffaa",
                    border: "1px solid #2e7d32",
                    color: "#2e7d32",
                    padding: "4px 8px",
                    fontSize: "12px",
                    borderRadius: "6px",
                    cursor: "pointer",
                  }}
                  onClick={() =>
                    saveResponse(messages[index - 1].text, msg.text, index)
                  }
                >
                  Save
                </button>
              )}
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
