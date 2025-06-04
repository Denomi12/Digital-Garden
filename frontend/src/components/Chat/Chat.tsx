import { useState } from "react";
import styles from "../../stylesheets/Chat.module.css";

function Chat() {
  // zacetni message chatta
  const [messages, setMessages] = useState([
    { sender: "bot", text: "Hello how can i help you?" },
  ]);
  const [input, setInput] = useState("");

  const handleSend = () => {
    if (!input.trim()) return;
    setMessages([...messages, { sender: "user", text: input }]);
    setInput("");

    setTimeout(() => {
      setMessages((prev) => [
        ...prev,
        { sender: "bot", text: "I understood that as: " + input },
      ]);
    }, 1000);
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
